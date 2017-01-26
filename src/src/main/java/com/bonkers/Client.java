package com.bonkers;


import com.bonkers.Controllers.StartPageCtrl;

import java.io.File;
import java.net.InetAddress;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import static com.sun.xml.internal.ws.spi.db.BindingContextFactory.LOGGER;
import static java.lang.Thread.sleep;
/**
 * Client class to connect to server
 */
public class Client implements NodeIntf, ClientIntf, QueueListener
{

    /**
     *  to get previd for filemanager
     */
    public static NodeInfo previd;
    /**
     * queue logs get placed in
     */
    private static QueueEvent<LogRecord> logRecordQueue = new QueueEvent<>();
    /**
     * File manager, handles file operations for the current node
     */
    public FileManager fm = null;
    /**
     * Saves the lock and unlock request until the agent gets to the client
     */
    public Queue<File> lockQueue = new LinkedList<>();
    /**
     * saves unlock requests
     */
    public Queue<File> unlockQueue = new LinkedList<>();
    /**
     * Saves the status if the lock failed the boolean is false else the boolean is true
     */
    public QueueEvent<Tuple<File, Boolean>> lockStatusQueue = new QueueEvent<>();
    /**
     *  Itself and the next neighbor
     */
    private NodeInfo id, nextid;//, previd;
    /**
     * Agent singleton that will run whenever it receives a new one.
     */
    private AgentFileList agentFileList = null;
    /**
     * Is set on true when this node starts up the agent. No other use.
     */
    private boolean isSetStartAgent = false;
    /**
     * Boolean that checks if the bootstrap has completed, essential for knowing if the node is connected properly
     */
    private boolean finishedBootstrap = false;
    /**
     * Name of the client.
     */
    private String name;
    /**
     * Multicast Thread.
     */
    private MulticastCommunicator multicast = null;
    /**
     * Server RMI interface.
     */
    private ServerIntf server;
    /**
     * Thread that holds the TCP Server runnable
     */
    private Thread tcpServer;
    /**
     * Client constructor.
     * Initiates Bootstrap and the filemanager, does all essential bootup stuff
     *
     * @param name           Name of the client
     * @param downloadFolder The folder to download files to
     */
    public Client(String name, File downloadFolder)
    {
        LOGGER.addHandler(Logging.listHandler(logRecordQueue));
        logRecordQueue.addListener(this);
        tcpServer = new Thread(new TCPServer(downloadFolder));
        tcpServer.start();

        try
        {
            Registry registry = LocateRegistry.createRegistry(1099);
            Remote remote = UnicastRemoteObject.exportObject(this, 0);
            registry.bind("ClientIntf", remote);
            registry.bind("NodeIntf", remote);
        } catch (AlreadyBoundException e)
        {
            e.printStackTrace();
        } catch (AccessException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        this.name = name;
        String ip = null;
        try {
            ip = InetAddress.getLocalHost().toString().split("/")[1];
        } catch (java.net.UnknownHostException e) {
            e.printStackTrace();
        }

        this.id = new NodeInfo(HashTableCreator.createHash(name), ip);
        LOGGER.info("This node's hash is: " + id.hash);

        multicast = new MulticastCommunicator();

        fm = new FileManager(downloadFolder, id);

        bootStrap();
        LOGGER.info("Finished bootstrap");
        lockStatusQueue.addListener(this);

        fm.server = server;
        fm.startFileChecker();
        LOGGER.info("Started up FM.");
        if (!Objects.equals(previd.address, id.address))
        {
            fm.startupReplication(previd);
        }
        notifyExistence();
        if (isSetStartAgent)
        {
            agentStarter();
        }
    }

    /**
     * Starts Multicastcommunication thread and distributes itself over the network
     * WARNING: Bug: it still sends 5 times
     */
    private void bootStrap()
    {
        try
        {
            int tries = 0;
            while (!finishedBootstrap)
            {
                if (tries < 5)
                {
                    tries++;
                    multicast.sendMulticast(name);
                }
                else if (tries == 5)
                {
                    tries++;
                    LOGGER.info("Multicast limit reached. Stopped retrying");
                }
                sleep(2000);
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Checks error from server, if there is an error, print something.
     * @param error     error from server
     */
    private void checkError(int error)
    {
        switch (error) {
            case 201:
                LOGGER.warning("The node name already exists on the server please choose another one");
                StartPageCtrl.AskNewName();
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
    }

    /**
     * This function gets called on shutdown.
     * It updates the neighbors so their connection can be established, and notifies the server of its shutdown.
     * It signals the fm first so that the files it is owner of get relocated properly.
     */
    public void shutdown()
    {
        LOGGER.info("Shutdown");
        fm.shutdown(previd);
        tcpServer.interrupt();
        multicast.leaveGroup();
        if (previd != null && !Objects.equals(previd.address, id.address) && nextid != null)
        {
            System.out.println(previd.address);
            try
            {

                Registry registry = LocateRegistry.getRegistry(previd.address);
                NodeIntf node = (NodeIntf) registry.lookup("NodeIntf");
                node.updateNextNeighbor(nextid);
                registry = LocateRegistry.getRegistry(nextid.address);
                node = (NodeIntf) registry.lookup("NodeIntf");
                node.updatePreviousNeighbor(previd);

            } catch (Exception e)
            {
                LOGGER.warning("Client exception: " + e.toString());
                e.printStackTrace();
            }
        }
        try
        {
            server.nodeShutdown(id);
        } catch (RemoteException e)
        {
            e.printStackTrace();
        }
        LOGGER.info("Successful shutdown");
        System.exit(0);
    }

    /**
     * This function will get called after connection with a neighboring node fails.
     * It updates the server that the note is down, and gets its neighboring nodes so they can make connection.
     * TODO unconnected & Untested
     *
     * @param id Integer id/hash of the failing node
     */
    public void nodeFailure(int id)
    {
        NodeInfo nodeFailed;
        if (id == previd.hash)
        {
            nodeFailed = previd;
        }
        else if (id == nextid.hash)
        {
            nodeFailed = nextid;
        }
        else
        {
            LOGGER.info("Node does not appear to be a neighbor. Potential Error. Skipping...");
            return;
        }
        try
        {
            NodeInfo[] neighbors = server.nodeNeighbors(nodeFailed);
            Registry registry = LocateRegistry.getRegistry(neighbors[0].address);
            NodeIntf node = (NodeIntf) registry.lookup("NodeIntf");
            node.updateNextNeighbor(neighbors[1]);
            registry = LocateRegistry.getRegistry(neighbors[1].address);
            node = (NodeIntf) registry.lookup("NodeIntf");
            node.updatePreviousNeighbor(neighbors[0]);
            server.nodeShutdown(nodeFailed);
        } catch (Exception e)
        {
            LOGGER.warning("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }

    @Override
    public void updateNextNeighbor(NodeInfo node)
    {
        LOGGER.info("Updated next neighbor: " + node.address);
        this.nextid = node;
        if (fm != null)
        {
            fm.recheckOwnership(node);
        }
    }

    @Override
    public void updatePreviousNeighbor(NodeInfo node)
    {
        LOGGER.info("Updated previous neighbor: " + node.address);
        previd = node;
    }

    @Override
    public void transferAgent(AgentFileList agentFileList)
    {
        LOGGER.log(Level.INFO, "AgentStarted");

        agentFileList.started = true;
        agentFileList.setClient(this);
        new Thread(() ->
        {
            agentFileList.update(agentFileList.fileList);
            if (!nextid.address.equals(id.address))
            {
                agentFileList.setClient(null);
                try
                {
                    Registry registry = LocateRegistry.getRegistry(nextid.address);
                    NodeIntf neighbor = (NodeIntf) registry.lookup("NodeIntf");
                    neighbor.transferAgent(agentFileList);
                } catch (RemoteException e)
                {
                    e.printStackTrace();
                } catch (NotBoundException e)
                {
                    e.printStackTrace();
                }
            }
            else
            {
                agentFileList.started = false;
            }
        }).start();
    }

    @Override
    public void transferFailureAgent(AgentFailure agent) throws RemoteException
    {
        Thread agentThread = new Thread(agent);
        agentThread.start();
        try
        {
            agentThread.join();
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        if (!agent.startingNode.address.equals(id.address))
        {
            Registry registry = LocateRegistry.getRegistry(nextid.address);
            try
            {
                NodeIntf neighbor = (NodeIntf) registry.lookup("NodeIntf");
                neighbor.transferFailureAgent(agent);
            } catch (NotBoundException e)
            {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void requestDownload(NodeInfo node, String file) throws RemoteException
    {
        fm.downloadQueue.add(new Tuple<>(node.address, file));
        fm.localFiles.add(file);
    }

    @Override
    public void setOwnerFile(FileInfo file) throws RemoteException
    {
        fm.setOwnerFile(file);
    }

    @Override
    public void removeFromOwnerList(String file, NodeInfo node) throws RemoteException
    {
        fm.removeFromOwnerList(file, node);
    }


    @Override
    public void setStartingInfo(String address, int clientCount) throws RemoteException
    {
        LOGGER.info("Setting starting info");
        try
        {
            Registry registry = LocateRegistry.getRegistry(address);
            server = (ServerIntf) registry.lookup("ServerIntf");
            checkError(server.error());
        } catch (NotBoundException e)
        {
            e.printStackTrace();
        }
        if (clientCount <= 1)
        {
            previd = nextid = id;
        }
        else
        {
            setNeighbors();
            if (clientCount == 2)
            {
                isSetStartAgent = true;
            }
        }
        finishedBootstrap = true;
    }

    /**
     * Notifies its neighbors of its existence. Is seperated from original bootstrap because of race conditions
     */
    private void notifyExistence()
    {
        if (!Objects.equals(id, previd))
        {
            try
            {
                LOGGER.info("Notifying neighbors of my existence.");
                Registry registry = LocateRegistry.getRegistry(previd.address);
                NodeIntf node = (NodeIntf) registry.lookup("NodeIntf");
                node.updateNextNeighbor(id);
                registry = LocateRegistry.getRegistry(nextid.address);
                node = (NodeIntf) registry.lookup("NodeIntf");
                node.updatePreviousNeighbor(id);
            } catch (AccessException e)
            {
                e.printStackTrace();
            } catch (RemoteException e)
            {
                e.printStackTrace();
            } catch (NotBoundException e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * Sets neighbors of current node.
     */
    private void setNeighbors()
    {
        try
        {
            NodeInfo[] neighbors = server.nodeNeighbors(id);
            if (neighbors[0] != null)
            {
                previd = neighbors[0];
            }
            LOGGER.info("Received " + previd + " as previous neighbor.");
            if (neighbors[1] != null)
            {
                nextid = neighbors[1];
            }
            LOGGER.info("Received " + nextid + " as next neighbor.");
        } catch (RemoteException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void queueFilled()
    {
        if (lockStatusQueue.queue.size() > 0)
        {
            lockStatusQueue.forEach((fileTuple) ->
            {
                if (fileTuple.y)
                {
                    //TODO start download
                }
                else
                {
                    lockQueue.add(fileTuple.x);
                }
            });
        }
        if (logRecordQueue.queue.size() > 0)
        {
            LogRecord lr = logRecordQueue.poll();
            //ClientCtrl.setLogs(lr);
        }
    }

    /**
     * Method to start the agent
     */
    private void agentStarter()
    {
        agentFileList = AgentFileList.getInstance();
        agentFileList.started = true;
        agentFileList.setClient(this);
        transferAgent(agentFileList);
    }
}