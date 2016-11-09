package com.bonkers;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Callable;

/**
 * Created by reinout on 11/3/16.
 */
public class MulticastCommunicator extends Thread {
    //https://docs.oracle.com/javase/7/docs/api/java/net/MulticastSocket.html
    private final String castAdress = "234.5.6.7";
    private InetAddress group = null;
    private Boolean IsFinished = false;
    private MulticastSocket castSocket = null;
    public QueueEvent<Tuple<String, InetAddress>> packetQueue=new QueueEvent<Tuple<String,InetAddress>>();

    public MulticastCommunicator() {
        JoinGroup();
    }
    public MulticastCommunicator(String name) {
        JoinGroup();
        SendMulticast(name);
    }

    public void run(){
        while(!IsFinished) {
            Tuple t = ReceiveMulticast();
            if(t!=null)
                packetQueue.add(t);
        }
    }

    private void JoinGroup() {
        try {
            group = InetAddress.getByName(castAdress);
            castSocket = new MulticastSocket(6789);
            castSocket.joinGroup(group);
        } catch (Exception e) {
        }
    }

    public void SendMulticast(String msg) {
        try {
            DatagramPacket packet = new DatagramPacket(msg.getBytes(), msg.length(), group, 6789);
            castSocket.send(packet);
        } catch (Exception e) {
        }
    }

    public Tuple<String,InetAddress> ReceiveMulticast() {
        try {
            byte[] buf = new byte[2048];
            DatagramPacket recv = new DatagramPacket(buf, buf.length);
            castSocket.receive(recv);
            System.out.println("packet received" + recv.getData());
            InetAddress address=recv.getAddress();
            System.out.println(address);
            String Nodename=new String(recv.getData());
            System.out.println(Nodename);
            return new Tuple<String,InetAddress>(Nodename,address);
        } catch (Exception e) {
            IsFinished=true;
            System.out.println(e);
        }
        return null;
    }

    public void LeaveGroup() {
        try {
            castSocket.leaveGroup(group);
        } catch (Exception e) {
        }
    }

}
