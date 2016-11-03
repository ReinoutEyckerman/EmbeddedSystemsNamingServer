package com.bonkers;

import java.io.IOException;
import java.net.*;

public class Server {
    private DatagramSocket socket = null;
    private HashTableCreator HT = null;
    private String Nodename = null;
    private Boolean IsFinished = false;

    public Server() throws IOException {
        HT = new HashTableCreator();
        socket = new DatagramSocket(6790);
        GetIP();
    }

    private void GetIP()
    {
        try {
            byte[] buf = new byte[256];

            while (!IsFinished)
            {
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
                InetAddress address = packet.getAddress();
                int port = packet.getPort();
                Nodename = new String(packet.getData());
                String resp = "OK";
                buf = resp.getBytes();
                packet = new DatagramPacket(buf, buf.length, address, port);
                DatagramSocket socket = new DatagramSocket();
                socket.send(packet);
                HT.CreateHashTable(Nodename, address.toString());
            }
        }
        catch (IOException e)
        {
            IsFinished = true;
            System.out.println(e);
        }
    }
}

