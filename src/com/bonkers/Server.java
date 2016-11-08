package com.bonkers;

import java.io.IOException;
import java.net.*;

public class Server implements ServerIntf {

    private DatagramSocket socket = null;
    private HashTableCreator HT = null;
    private String Nodename = null;
    private Boolean IsFinished = false;

    public Server() throws IOException {
        HT = new HashTableCreator();
        socket = new DatagramSocket(6790);
        GetIP();
    }

    public String FindLocationFile(String FileName){
        HashTableCreator obj = new HashTableCreator();
        int FileHash = obj.createHash(FileName);
        String result =obj.readHashtable(FileHash);

        if (result != null)
            return result;
        else
            return " File Not Found";
    }

    private void GetIP()
    {
        try {
            byte[] buf = new byte[2048];

            while (!IsFinished)
            {
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
                System.out.println("packet received" + packet);
                InetAddress address = packet.getAddress();
                int port = packet.getPort();
                Nodename = new String(packet.getData());
                System.out.println(Nodename);
                String resp = checkDoubles(Nodename, address);
                System.out.println(resp);
                buf = resp.getBytes();
                packet = new DatagramPacket(buf, buf.length, address, port);
                DatagramSocket socket = new DatagramSocket();
                socket.send(packet);
            }
        }
        catch (IOException e)
        {
            IsFinished = true;
            System.out.println(e);
        }
    }

    private String checkDoubles(String name, InetAddress ip)
    {
        String resp = null;
        int hash = HT.createHash(name);
        if(HT.htIp.containsKey(hash))
        {
            resp = "Name is already taken";
        }
        else if (HT.htIp.containsValue(ip.toString()))
        {
            resp = "IP already exists";
        }
        else
        {
            resp = "OK";
            HT.CreateHashTable(Nodename, ip.toString());
        }
        return resp;
    }
}

