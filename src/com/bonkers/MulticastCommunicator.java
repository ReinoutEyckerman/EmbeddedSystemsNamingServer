package com.bonkers;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Callable;

/**
 * Multicast communication Thread.
 */
public class MulticastCommunicator extends Thread {
    //https://docs.oracle.com/javase/7/docs/api/java/net/MulticastSocket.html
    /**
     * Multicast address of the group to join.
     */
    private final String castAdress = "234.5.6.7";

    /**
     *  InetAddress of the group.
     */
    private InetAddress group = null;

    /**
     * Boolean of the finished thread.
     */
    private Boolean IsFinished = false;
    /**
     * Multicastsocket of the thread.
     */
    private MulticastSocket castSocket = null;
    /**
     * QueueEvent of packages that are received.
     * You can subscribe to its events.
     */
    public QueueEvent<Tuple<String, InetAddress>> packetQueue=new QueueEvent<Tuple<String,InetAddress>>();

    /**
     * Constructor meant for server that automatically joins the multicast group.
     */
    public MulticastCommunicator() {
        JoinGroup();
    }

    /**
     * Constructor meant for client that automatically joins the multicast group and sends its info over multicast.
     * @param name Name of the client.
     */
    public MulticastCommunicator(String name) {
        JoinGroup();
        SendMulticast(name);
    }

    /**
     * Main server loop, runs and gets multicasts and adds them to the QueueEvent.
     * No interaction required.
     */
    public void run(){
        while(!IsFinished) {
            Tuple t = ReceiveMulticast();
            if(t!=null)
                packetQueue.add(t);
        }
    }

    /**
     * Join group function. Connects to the multicast group.
     */
    private void JoinGroup() {
        try {
            group = InetAddress.getByName(castAdress);
            castSocket = new MulticastSocket(6789);
            castSocket.joinGroup(group);
        } catch (Exception e) {
        }
    }

    /**
     * Sends a string over multicast.
     * @param msg Message to send.
     */
    public void SendMulticast(String msg) {
        try {
            DatagramPacket packet = new DatagramPacket(msg.getBytes(), msg.length(), group, 6789);
            castSocket.send(packet);
        } catch (Exception e) {
        }
    }

    /**
     * Receives a multicast packet.
     *
     * @return returns a Tuple containing its information as string and its IP address as InetAddress
     */
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

    /**
     * Leave Multicast group.
     */
    public void LeaveGroup() {
        try {
            castSocket.leaveGroup(group);
        } catch (Exception e) {
        }
    }

}
