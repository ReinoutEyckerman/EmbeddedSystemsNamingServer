package com.bonkers;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

/**
 * Client class to connect to server
 */
public class Client implements NodeIntf, ClientIntf {

    /**
     * Address of the server to connect to.
     */

    private String ServerAddress = null;

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
     * Client constructor.
     * Initiates Bootstrap
     * @param name Name of the client
     * @throws Exception Generic exception for when something fails TODO
     */
    public Client(String name) throws Exception {
        try {
            Registry registry = LocateRegistry.createRegistry(1099);
            Remote remote =  UnicastRemoteObject.exportObject(this, 0);
            registry.bind("ClientIntf", remote);
            registry.bind("NodeIntf",remote);
        }catch(AlreadyBoundException e){
            e.printStackTrace();
        }
        this.name=name;
        this.id=new NodeInfo(HashTableCreator.createHash(name),InetAddress.getLocalHost().toString());
        bootStrap();
    }

    /**
     * Starts Multicastcomms and distributes itself over the network
     */
    private void bootStrap(){
        multicast=new MulticastCommunicator(name);
        try {
            int timeout = 10;
            int count = 0;
            while (ServerAddress == null) {
                if (count > timeout) {
                    multicast.sendMulticast(name);
                    count = 0;
                }
                count++;
                Thread.sleep(10000);
            }
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        try {
        }catch(Exception e){
            e.printStackTrace();
        }
        System.out.println("Bootstrap completed.");
    }

    /**
     * Returns list of files as strings in a specified folder.
     * @param folder Folder File for where to search for files.
     * @return List of strings of the filenames in the folder.
     */
    private List<String> listFilesForFolder(final File folder) {
        List<String> files=new ArrayList<>();
        for (final File fileEntry : folder.listFiles()) {
            if (!fileEntry.isDirectory()) {
                files.add(fileEntry.getName());
            }
        }
        return files;
    }


    private void CheckError(String error) throws Exception
    {
        if(error.equals("201"))
        {
            System.out.println("The node name already exists on the server please choose another one");
        }
        else if (error.equals("202"))
        {
            System.out.println("You already exist in the name server");
        }
        else if (error.equals("100"))
        {
            System.out.println("No errors");
        }
        else
        {
            System.out.println("Unknown error");
        }
    }
    private DatagramPacket sendRequest() throws IOException
    {
        //TODO

/*
        // send request
        byte[] buf = new byte[2048];
        buf = name.getBytes();
        InetAddress address = InetAddress.getByName(ServerAddress);
        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 6790);
        socket.send(packet);

        // get response
        packet = new DatagramPacket(buf, buf.length);
        //socket.receive(packet);
        return packet;*/
        return null;
    }
    /**
     * This function gets called on shutdown.
     * It updates the neighbors so their connection can be established, and notifies the server of its shutdown.
     */

    public void shutdown(){
        try {
            Registry registry = LocateRegistry.getRegistry(previd.Address);
            NodeIntf node = (NodeIntf) registry.lookup("NodeIntf");
            node.updateNextNeighbor(nextid);
            registry=LocateRegistry.getRegistry(nextid.Address);
            node=(NodeIntf) registry.lookup("NodeIntf");
            node.updatePreviousNeighbor(previd);
            server.nodeShutdown(id);
            System.exit(0);
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }

    /**
     * This function will get called after connection with a neighboring node fails.
     * It updates the server that the note is down, and gets its neighboring nodes so they can make connection.
     *
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
    }

    @Override
    public void updatePreviousNeighbor(NodeInfo node) {
        this.previd=node;
    }

    @Override
    public void setStartingInfo(String address, int clientcount) throws RemoteException, Exception {
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
        }
    }

    /**
     * Sets neighbors of current node.
     */
    private void setNeighbors(){
        try {
            NodeInfo[] neighbors=server.nodeNeighbors(id);
            if(neighbors[0]!=null)
                previd=neighbors[0];
            else if(neighbors[1]!=null)
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
