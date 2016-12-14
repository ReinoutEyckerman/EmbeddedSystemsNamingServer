package com.bonkers;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.net.InetAddress;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

/**
 * Client class to connect to server
 */
public class Client implements NodeIntf, ClientIntf {

    /**
     * Address of the server to connect to.
     */
    private String ServerAddress = null;
    /**
     * Boolean that checks if the bootstrap has completed, essential for knowing if the node is connected properly
     */
    private boolean finishedBootstrap=false;
    /**
     * Error code TODO Jente
     */
    public boolean Error = true;
    /**
     * The download location TODO might be removed?
     */
    private final File downloadFolder;
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
    private NodeInfo id, previd, nextid;
    /**
     * File manager, handles file operations for the current node
     */
    private FileManager fm = null;
    /**
     * TODO Jente
     */
    public AgentFileList agentFileList = null;

    /**
     * Client constructor.
     * Initiates Bootstrap and the filemanager, does all essential bootup stuff
     * @param name Name of the client
     * @param downloadFolder The folder to download files to
     * @throws Exception Generic exception for when something fails TODO
     */
    public Client(String name, File downloadFolder) throws Exception {
        this.downloadFolder=downloadFolder;
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                shutdown();
            }
        }));
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
        multicast=new MulticastCommunicator();
        bootStrap();
        while(!finishedBootstrap){
        }
        fm = new FileManager(downloadFolder,server,id,previd);
        fm.CheckIfOwner(this.nextid);
        fm.StartupReplication(previd);
        Thread t=new Thread(new TCPServer());//Todo check why constructor dissapeared
        t.start();
    }

    /**
     * Starts Multicastcomms and distributes itself over the network
     */
    private void bootStrap(){
        try {
            multicast.sendMulticast(name);

        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println("Bootstrap completed.");
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
            System.out.println("The node name already exists on the server please choose another one");
                break;
            case 202:
            System.out.println("You already exist in the name server");
                break;
            case 100:
            System.out.println("No errors");
                break;
            default:
                System.out.println("Unknown error");
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

        System.out.print("shutdown\n");

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
                System.err.println("Client exception: " + e.toString());
                e.printStackTrace();
            }
        }
        try {
            server.nodeShutdown(id);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        System.out.println("Successful shutdown");
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
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }

    @Override
    public void updateNextNeighbor(NodeInfo node) {
        this.nextid=node;
        System.out.println("Next:" +node.Address);
        fm.RecheckOwnership(node);
    }

    @Override
    public void updatePreviousNeighbor(NodeInfo node) {
        this.previd=node;
        System.out.println("Previous:" +node.Address);
    }

    @Override
    public void transferAgent(AgentFileList agentFileList) throws RemoteException {
        agentFileList.started = true;
        Thread agentThread=new Thread(agentFileList);
        agentFileList.setClientFileList(fm.ownedFiles);
        agentThread.start();
        try {
            agentThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(!(nextid.Address.equals(id.Address)))
        {
            Registry registry = LocateRegistry.getRegistry(nextid.Address);
            try {
                NodeIntf neighbor = (NodeIntf) registry.lookup("NodeIntf");
                neighbor.transferAgent(agentFileList);
            } catch (NotBoundException e) {
                e.printStackTrace();
            }
        }
        else
        {
            agentFileList.started = false;
        }
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
    }

    @Override
    public void setOwnerFile( FileInfo file) throws RemoteException {
        fm.setOwnerFile(file);
    }


    @Override
    public void setStartingInfo(String address, int clientcount) throws RemoteException {
        this.ServerAddress=address;
        try {
            Registry registry = LocateRegistry.getRegistry(ServerAddress);
            server = (ServerIntf) registry.lookup("ServerIntf");
            if (CheckError(server.error())!=100){
                //TODO Joris Gooi grafische error en vraag dan nog is, die grafische error mag ook in dien checkerror geplaatst worde, dan kan deze if weg en heeft die functie tenminste nut
                Error=false;
            }
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
                agentFileList = AgentFileList.getInstance();
                agentFileList.started = true;
                transferAgent(agentFileList);
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

            if(neighbors[1]!=null)
                nextid=neighbors[1];
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
}
