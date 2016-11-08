package com.bonkers;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.Callable;

/**
 * Created by Jente on 8/11/2016.
 */
public class GetIPThread implements Callable {
    private DatagramSocket socket = null;
    private HashTableCreator HT = null;
    private String Nodename = null;
    private Boolean IsFinished = false;

    public GetIPThread() throws IOException
    {
        HT = new HashTableCreator();
        socket = new DatagramSocket(6790);
    }
    public Integer call() {
        try {
            byte[] buf = new byte[2048];

            while (!IsFinished) {
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
                System.out.println("packet received" + packet.getData());
                InetAddress address = packet.getAddress();
                //InetAddress address = InetAddress.getByName(Address);
                System.out.println(address);
                int port = packet.getPort();
                Nodename = new String(packet.getData());
                System.out.println(Nodename);
                String resp = checkDoubles(Nodename, address);
                System.out.println(resp);
                buf = resp.getBytes();
                packet = new DatagramPacket(buf, buf.length, address, port);
                DatagramSocket socket = new DatagramSocket();
                socket.send(packet);
                System.out.println(new String(packet.getData()));
            }
        } catch (IOException e) {
            IsFinished = true;
            System.out.println(e);
        }
        return 0;
    }
    private String checkDoubles(String name, InetAddress ip)
    {
        String resp = null;
        int hash = HT.createHash(name);
        if(HT.htIp.containsKey(hash))
        {
            resp = "201";
        }
        else if (HT.htIp.containsValue(ip))
        {
            resp = "202";
        }
        else
        {
            resp = "100";
            HT.CreateHashTable(ip, Nodename);
        }
        return resp;
    }
}
