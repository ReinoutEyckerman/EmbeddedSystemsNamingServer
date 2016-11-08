package com.bonkers;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;

import com.bonkers.ServerIntf;
import com.sun.org.apache.xpath.internal.operations.Mult;


public class Client implements QueueListener,NodeIntf {
    String ServerAddress = "192.168.1.230";

    private String name;
    BufferedReader br = null;
    DatagramSocket socket = null;
    DatagramPacket packet = null;
    byte[] buf = null;
    MulticastCommunicator multicast=null;
    private ServerIntf server;
    private Tuple<Integer, String> id, previd, nextid;


    public Client(String name) throws Exception {
        this.name=name;
        multicast=new MulticastCommunicator(name);
        multicast.start();
        multicast.packetQueue.addListener(this);
        sendDetailsToNameServer();
        try {
            Registry registry = LocateRegistry.getRegistry(ServerAddress);

            ServerIntf stub = (ServerIntf) registry.lookup("ServerIntf");
            String response = stub.FindLocationFile("8814");
            System.out.println("IP is");

            server = (ServerIntf) registry.lookup("ServerIntf");
            String response1 = server.FindLocationFile("Filename");

            System.out.println(response);


        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }
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
        buf = new byte[2048];
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

        socket.close();
    }
    private DatagramPacket sendRequest() throws IOException
    {
        // send request
        System.out.println("Give your client a name");
        String name = br.readLine();
        byte[] buf = new byte[2048];
        buf = name.getBytes();
        InetAddress address = InetAddress.getByName(ServerAddress);
        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 6790);
        socket.send(packet);

        // get response
        packet = new DatagramPacket(buf, buf.length);
        socket.receive(packet);
        return packet;
    }
    public void Shutdown(){
        try {
            Registry registry = LocateRegistry.getRegistry(previd.y);
            NodeIntf node = (NodeIntf) registry.lookup("NodeIntf");
            node.UpdateNextNeighbor(nextid);
            registry=LocateRegistry.getRegistry(nextid.y);
            node=(NodeIntf) registry.lookup("NodeIntf");
            node.UpdatePreviousNeighbor(previd);
            server.NodeShutdown(id);
            System.exit(0);
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }
    public void NodeFailure(int id){
        Tuple nodeFailed;
        if(id==previd.x)
            nodeFailed=previd;
        else if(id==nextid.x)
            nodeFailed=nextid;
        else {
            throw new IllegalArgumentException("What the actual fuck, this node isn't in my table yo");
        }
        try {
            Tuple<Tuple<Integer,String>,Tuple<Integer,String>> neighbors=server.NodeFailure(nodeFailed);
            Registry registry = LocateRegistry.getRegistry(neighbors.x.y);
            NodeIntf node = (NodeIntf) registry.lookup("NodeIntf");
            node.UpdateNextNeighbor(neighbors.y);
            registry=LocateRegistry.getRegistry(neighbors.y.y);
            node=(NodeIntf) registry.lookup("NodeIntf");
            node.UpdatePreviousNeighbor(neighbors.x);
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
    public void UpdateNextNeighbor(Tuple node) {
        this.nextid=node;
    }

    @Override
    public void UpdatePreviousNeighbor(Tuple node) {
        this.previd=node;

    }
}
