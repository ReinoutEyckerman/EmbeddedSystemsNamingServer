package com.bonkers;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;

/**
 * Client class to connect to server
 */
public class Client implements QueueListener,NodeIntf, ClientIntf {

    /**
     * Address of the server to connect to.
     * TODO( Necessary, since multicast?)
     */

    private String ServerAddress = "192.168.1.1";

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
     * Starts MulticastCommunicator.
     * @param name Name of the client
     * @throws Exception Generic exception for when something fails TODO
     */
    public Client(String name) throws Exception {
        this.name=name;
        BootStrap();
    }

    private void BootStrap(){
        multicast=new MulticastCommunicator(name);
        multicast.start();
        multicast.packetQueue.addListener(this);
        try {
            int timeout = 10;//time in seconds
            int count = 0;
            while (ServerAddress == null) {
                if (count > timeout) {
                    multicast.SendMulticast(name);
                    count = 0;
                }
                count++;
                Thread.sleep(1000);
            }
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        try {
            Registry registry = LocateRegistry.getRegistry(ServerAddress);

            server = (ServerIntf) registry.lookup("ServerIntf");
            server.NodeNeighbors(new NodeInfo(HashTableCreator.createHash(name),InetAddress.getLocalHost().toString()));
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


    private void sendDetailsToNameServer() throws IOException
    {
        //TODO
        /*
        byte[] buf = new byte[2048];
        br = new BufferedReader(new InputStreamReader(System.in));
        // get a datagram socket
        socket = new DatagramSocket();

        DatagramPacket packet = sendRequest();

        // display response

        String received = new String(packet.getData());
        System.out.println(received);
        if(received.equals("201t"))
        {
            System.out.println("The node name already exists on the server please choose another one");
            sendRequest();
        }
        else if (received.equals("202t"))
        {
            System.out.println("You already exist in the name server");
        }
        else if (received.equals("100t"))
        {
            System.out.println("No errors");
        }
        else
        {
            System.out.println("Unknown error");
        }

        socket.close();*/
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

    public void Shutdown(){
        try {
            Registry registry = LocateRegistry.getRegistry(previd.Address);
            NodeIntf node = (NodeIntf) registry.lookup("NodeIntf");
            node.UpdateNextNeighbor(nextid);
            registry=LocateRegistry.getRegistry(nextid.Address);
            node=(NodeIntf) registry.lookup("NodeIntf");
            node.UpdatePreviousNeighbor(previd);
            server.NodeShutdown(id);
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
    public void NodeFailure(int id){
        NodeInfo nodeFailed;
        if(id==previd.Hash)
            nodeFailed=previd;
        else if(id==nextid.Hash)
            nodeFailed=nextid;
        else {
            throw new IllegalArgumentException("What the actual fuck, this node isn't in my table yo");
        }
        try {
            NodeInfo[] neighbors=server.NodeNeighbors(nodeFailed);
            Registry registry = LocateRegistry.getRegistry(neighbors[0].Address);
            NodeIntf node = (NodeIntf) registry.lookup("NodeIntf");
            node.UpdateNextNeighbor(neighbors[1]);
            registry=LocateRegistry.getRegistry(neighbors[1].Address);
            node=(NodeIntf) registry.lookup("NodeIntf");
            node.UpdatePreviousNeighbor(neighbors[0]);
            server.NodeShutdown(nodeFailed);
        }catch(Exception e){
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }

    @Override
    public void packetReceived() {

    }

    @Override
    public void UpdateNextNeighbor(NodeInfo node) {
        this.nextid=node;
    }

    @Override
    public void UpdatePreviousNeighbor(NodeInfo node) {
        this.previd=node;
    }

    @Override
    public void SetServerIp(String address) throws RemoteException {
        this.ServerAddress=address;
    }
}
