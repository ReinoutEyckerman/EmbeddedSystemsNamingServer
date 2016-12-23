package com.bonkers;


import com.bonkers.Controllers.ClientCtrl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.net.InetAddress;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.Level;

import static com.bonkers.Controllers.ClientCtrl.setData;
import static com.bonkers.Controllers.ClientCtrl.setLogs;
import static java.lang.Thread.sleep;

/**
 * Client class to connect to server
 */
public class Client implements NodeIntf, ClientIntf, ClientNodeIntf, QueueListener {

    /**
     * Address of the server to connect to.
     */
    private String ServerAddress = null;
    /**
     * Boolean that checks if the bootstrap has completed, essential for knowing if the node is connected properly
     */
    private boolean finishedBootstrap=false;
    /**
     * Name of the client.
     */
    private String name;

    /**
     * Multicast Thread.
     */
    private MulticastCommunicator multicast=null;
    /**
     * Server RMI interface.
     */
    private ServerIntf server;
    /**
     * Tuples with the hash and IPAddress from itself, previous and nextid.
     */
    public NodeInfo id, nextid;//, previd;
    public static NodeInfo previd;
    /**
     * File manager, handles file operations for the current node
     */
    public FileManager fm = null;
    /**
     * Sets the agent to handle the files on the clients but waits to start it
     */
    public AgentFileList agentFileList = null;

    Thread t = null;
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    public static QueueEvent<LogRecord> logRecordQueue = new QueueEvent<>();

    /**
     * Saves the lock and unlock request until the agent gets to the client
     */
    public Queue<File> LockQueue = new LinkedList<>();
    public Queue<File> UnlockQueue = new LinkedList<>();
    /**
     * Saves the status if the lock failed the boolean is false else the boolean is true
     */
    public QueueEvent<Tuple<File, Boolean>> LockStatusQueue = new QueueEvent();

    public Boolean setStartAgent = false;

    public List<File> globalFileList = null;

    /**
     * Client constructor.
     * Initiates Bootstrap and the filemanager, does all essential bootup stuff
     * @param name Name of the client
     * @param downloadFolder The folder to download files to
     * @throws Exception Generic exception for when something fails TODO
     */
    public Client(String name, File downloadFolder) throws Exception {
        LOGGER.addHandler(Logging.ListHandler(logRecordQueue));
        logRecordQueue.addListener(this);
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                //shutdown();
            }
        }));
        Thread t=new Thread(new TCPServer(downloadFolder));
        t.start();
        try {
            Registry registry = LocateRegistry.createRegistry(1099);
            Remote remote =  UnicastRemoteObject.exportObject(this, 0);
            registry.bind("ClientIntf", remote);
            registry.bind("NodeIntf",remote);
        }catch(AlreadyBoundException e){
            e.printStackTrace();
        }
        this.name=name;
        String ip=InetAddress.getLocalHost().toString().split("/")[1];
        this.id=new NodeInfo(HashTableCreator.createHash(name),ip);
        LOGGER.info("This node's hash is: "+id.Hash);
        multicast=new MulticastCommunicator();
        fm = new FileManager(downloadFolder,id);
        bootStrap();
        LOGGER.info("Finished bootstrap");
        LockStatusQueue.addListener(this);
        fm.server=server;
        fm.startFileChecker();
        LOGGER.info("Started up FM.");
        if(!Objects.equals(previd.Address, id.Address))
            fm.StartupReplication(previd);
        if(setStartAgent)
        {
          agentStarter();
        }
    }

    /**
     * Starts Multicastcomms and distributes itself over the network
     */
    private void bootStrap(){
        try {
            int tries=0;
            while (!finishedBootstrap){
                sleep(2000);
               if (tries<5){
                   tries++;
                   multicast.sendMulticast(name);
               }
               else if(tries==5){
                   tries++;
                   LOGGER.info("Multicast limit reached. Stopped retrying");
               }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Returns errors if there IF THERE WHAT JORIS? Todo joris
     * @param error
     * @return
     * @throws Exception
     */
    public int CheckError(int error)
    {
        switch (error){
            case 201:
            LOGGER.warning("The node name already exists on the server please choose another one");
                break;
            case 202:
            LOGGER.warning("You already exist in the name server");
                break;
            case 100:
            LOGGER.info("No errors");
                break;
            default:
                LOGGER.warning("Unknown error");
                break;
        }
        return error;
    }
    /**
     * This function gets called on shutdown.
     * It updates the neighbors so their connection can be established, and notifies the server of its shutdown.
     * TODO Replication
     */
    public void shutdown(){
        LOGGER.info("Shutdown");
        fm.shutdown(previd);

        if (previd != null && !Objects.equals(previd.Address, id.Address) && nextid != null) {
            System.out.println(previd.Address);
            try {

                Registry registry = LocateRegistry.getRegistry(previd.Address);
                NodeIntf node = (NodeIntf) registry.lookup("NodeIntf");
                node.updateNextNeighbor(nextid);
                registry = LocateRegistry.getRegistry(nextid.Address);
                node = (NodeIntf) registry.lookup("NodeIntf");
                node.updatePreviousNeighbor(previd);

            } catch (Exception e) {
                LOGGER.warning("Client exception: " + e.toString());
                e.printStackTrace();
            }
        }
        try {
            server.nodeShutdown(id);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        LOGGER.info("Successful shutdown");
        System.exit(0);
    }

    /**
     * This function will get called after connection with a neighboring node fails.
     * It updates the server that the note is down, and gets its neighboring nodes so they can make connection.
     * TODO unconnected & Untested
     * @param id Integer id/hash of the failing node
     */
    public void nodeFailure(int id){
        NodeInfo nodeFailed;
        if(id==previd.Hash)
            nodeFailed=previd;
        else if(id==nextid.Hash)
            nodeFailed=nextid;
        else {
            throw new IllegalArgumentException("What the actual fuck, this node isn't in my table yo");
        }
        try {
            NodeInfo[] neighbors=server.nodeNeighbors(nodeFailed);
            Registry registry = LocateRegistry.getRegistry(neighbors[0].Address);
            NodeIntf node = (NodeIntf) registry.lookup("NodeIntf");
            node.updateNextNeighbor(neighbors[1]);
            registry=LocateRegistry.getRegistry(neighbors[1].Address);
            node=(NodeIntf) registry.lookup("NodeIntf");
            node.updatePreviousNeighbor(neighbors[0]);
            server.nodeShutdown(nodeFailed);
        }catch(Exception e){
            LOGGER.warning("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }

    @Override
    public void updateNextNeighbor(NodeInfo node) {
        LOGGER.info("Updated next neighbor: " +node.Address);
        this.nextid=node;
        if(fm!=null)
            fm.RecheckOwnership(node);
    }

    @Override
    public void updatePreviousNeighbor(NodeInfo node) {
        LOGGER.info("Updated previous neighbor: " +node.Address);
        this.previd=node;
    }

    @Override
    public void transferAgent(AgentFileList agentFileList) {
        LOGGER.log(Level.INFO,"AgentStarted");

       /* agentFileList.setClient(this);
        agentFileList.started = true;

        ExecutorService executor = Executors.newSingleThreadExecutor();

        FutureTask<List<File>> futureTask = new FutureTask<List<File>>(agentFileList);

        executor.execute(futureTask);

        executor.shutdownNow();

        Boolean executing = true;
        while (executing) {

            try {
                if(futureTask.isDone()){
                    System.out.println("Done");
                    //shut down executor service
                    executor.shutdown();
                    executing = false;
                }

                if(!futureTask.isDone()){
                    //wait indefinitely for future task to complete
                    globalFileList = futureTask.get();
                }

            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            finally {
                return;
            }

        }
        agentFileList.setClient(null);
        if(!nextid.Address.equals(id.Address))
        {
            try {
                Registry registry = LocateRegistry.getRegistry(nextid.Address);
                try {
                    NodeIntf neighbor = (NodeIntf) registry.lookup("NodeIntf");
                    neighbor.transferAgent(agentFileList);
                } catch (NotBoundException e) {
                    e.printStackTrace();
                }
                catch (NullPointerException e)
                {
                    e.printStackTrace();
                }
            }
            catch (RemoteException e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            agentFileList.started = false;
        }
*/
        /*try {
            Future<List<File>> future
            while (!future.isDone())
            {

            }
            if(!future.get().isEmpty()) {
                globalFileList = future.get();
            }
            if(globalFileList.size() > 0)
                setData(globalFileList);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        finally {

        }*/
        agentFileList.started = true;
        agentFileList.setClient(this);
        Thread agentThread=new Thread(agentFileList);

        agentThread.start();

    }

    @Override
    public void transferDoubleAgent(AgentFailure agent) throws RemoteException {
        Thread agentThread=new Thread(agent);
        agentThread.start();
        try {
            agentThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(!agent.startingNode.Address.equals(id.Address)){
            Registry registry = LocateRegistry.getRegistry(nextid.Address);
            try {
                NodeIntf neighbor = (NodeIntf) registry.lookup("NodeIntf");
                neighbor.transferDoubleAgent(agent);
            } catch (NotBoundException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void requestDownload(NodeInfo node, String file) throws RemoteException {
       fm.downloadQueue.add(new Tuple<>(node.Address,file ));
       fm.localFiles.add(file);
    }

    @Override
    public void setOwnerFile( FileInfo file) throws RemoteException {
        fm.setOwnerFile(file);
    }

    @Override
    public void removeFromOwnerList(String file, NodeInfo node) throws RemoteException {
        fm.removeFromOwnerList(file, node);
    }


    @Override
    public void setStartingInfo(String address, int clientcount) throws RemoteException {
        LOGGER.info("Setting starting info");
        this.ServerAddress=address;
        try {
            Registry registry = LocateRegistry.getRegistry(ServerAddress);
            server = (ServerIntf) registry.lookup("ServerIntf");
            CheckError(server.error());
        }catch (NotBoundException e){
            e.printStackTrace();
        }
        if(clientcount<=1){
            previd=nextid=id;
        }
        else{
            try {
                setNeighbors();
                Registry registry = LocateRegistry.getRegistry(previd.Address);
                NodeIntf node = (NodeIntf) registry.lookup("NodeIntf");
                node.updateNextNeighbor(id);
                registry = LocateRegistry.getRegistry(nextid.Address);
                node = (NodeIntf) registry.lookup("NodeIntf");
                node.updatePreviousNeighbor(id);
            }catch(NotBoundException e){
                e.printStackTrace();
            }
            if(clientcount == 2)
            {
                setStartAgent = false;
            }
        }
        finishedBootstrap=true;
    }

    /**
     * Sets neighbors of current node.
     */
    private void setNeighbors(){
        try {
            NodeInfo[] neighbors=server.nodeNeighbors(id);
            if(neighbors[0]!=null)
                previd=neighbors[0];
            LOGGER.info("Received " +previd+ " as previous neighbor.");
            if(neighbors[1]!=null)
                nextid=neighbors[1];
            LOGGER.info("Received " +nextid+ " as next neighbor.");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void setNameError() throws RemoteException {
        System.out.println("Error: Name already taken.");
        System.out.println("Exiting...");
        System.exit(1);
    }

    /**
     * Method that gets fired when the LockStatusQueue gets filled
     */
    @Override
    public void queueFilled()
    {
        if(LockStatusQueue.queue.size() > 0)
        {
            LockStatusQueue.forEach((fileTuple) ->{
                if(fileTuple.y)
                {
                    //TODO start download
                }
                else
                {
                    LockQueue.add(fileTuple.x);
                }
            });
        }
        if(logRecordQueue.queue.size() > 0)
        {
            LogRecord lr = logRecordQueue.poll();
            //ClientCtrl.setLogs(lr);
        }
    }

    /**
     * Method to start the agent
     */
    public void agentStarter()
    {
        agentFileList = AgentFileList.getInstance();
        agentFileList.started = true;
        agentFileList.setClient(this);
        transferAgent(agentFileList);
    }
}
