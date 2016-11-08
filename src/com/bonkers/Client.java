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

public class Client {
    String ServerAddress = "192.168.1.1";
    BufferedReader br = null;
    DatagramSocket socket = null;
    DatagramPacket packet = null;

    public Client() throws Exception {
        sendDetailsToNameServer();
        try {
            Registry registry = LocateRegistry.getRegistry(ServerAddress);
            ServerIntf stub = (ServerIntf) registry.lookup("ServerIntf");
            String response = stub.FindLocationFile("Filename");
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
        br = new BufferedReader(new InputStreamReader(System.in));
        // get a datagram socket
        socket = new DatagramSocket();

        sendRequest();

        // display response
        String received = new String(packet.getData(), 0, packet.getLength());
        if(received == "Name is already taken")
        {
            System.out.println("The node name already exists on the server please choose another one");
            sendRequest();
        }
        else if (received == "IP already exists")
        {
            System.out.println("You already exist in the name server");
        }
        else
        {
            System.out.println("No errors");
        }

        socket.close();
    }
    private void sendRequest() throws IOException
    {
        // send request
        System.out.print("Give your client a name");
        String name = br.readLine();
        byte[] buf = new byte[2048];
        buf = name.getBytes();
        InetAddress address = InetAddress.getByAddress(ServerAddress.getBytes());
        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 6790);
        socket.send(packet);

        // get response
        packet = new DatagramPacket(buf, buf.length);
        socket.receive(packet);
    }
}
