package com.bonkers;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

/**
 * Multicast communication Thread.
 */
public class MulticastCommunicator extends Thread
{
    /**
     * Multicast address of the group to join.
     */
    private final String castAddress = "234.5.6.7";
    /**
     * QueueEvent of packages that are received.
     * You can subscribe to its events.
     */
    public QueueEvent<Tuple<String, String>> packetQueue = new QueueEvent<Tuple<String, String>>();
    /**
     * InetAddress of the group.
     */
    private InetAddress group = null;
    /**
     * Boolean of the finished thread.
     */
    private boolean isFinished = false;
    /**
     * Multicastsocket of the thread.
     */
    private MulticastSocket castSocket = null;

    private Logging LOGGER = new Logging();

    /**
     * Constructor that automatically joins the multicast group.
     */
    public MulticastCommunicator()
    {
        joinGroup();
    }

    /**
     * Main server loop, runs and gets multicasts and adds them to the QueueEvent.
     * No interaction required.
     */
    public void run()
    {
        LOGGER.logger.info("Started multicastserver successfully");
        while (!isFinished)
        {
            Tuple<String, String> info = receiveMulticast();
            if (info != null)
            {
                packetQueue.add(info);
            }
        }
    }

    /**
     * Join group function. Connects to the multicast group.
     */
    private void joinGroup()
    {
        try
        {
            group = InetAddress.getByName(castAddress);
            castSocket = new MulticastSocket(6789);
            castSocket.joinGroup(group);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Sends a string over multicast.
     *
     * @param msg Message to send.
     */
    public void sendMulticast(String msg)
    {
        try
        {
            LOGGER.logger.info("Sent " + msg + " as multicast.");
            DatagramPacket packet = new DatagramPacket(msg.getBytes(), msg.length(), group, 6789);
            castSocket.send(packet);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Receives a multicast packet.
     *
     * @return returns a Tuple containing its information as string and its IP address as InetAddress
     */
    private Tuple<String, String> receiveMulticast()
    {
        try
        {
            byte[] buf = new byte[2048];
            DatagramPacket recv = new DatagramPacket(buf, buf.length);
            castSocket.receive(recv);
            String address = recv.getAddress().getHostAddress();
            LOGGER.logger.info("Received a multicast packet from address " + address);
            String Nodename = new String(buf, 0, recv.getLength());
            return new Tuple<String, String>(Nodename, address);
        } catch (Exception e)
        {
            isFinished = true;
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Leave Multicast group.
     */
    public void leaveGroup()
    {
        try
        {
            LOGGER.logger.info("Leaving multicast group.");
            castSocket.leaveGroup(group);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}
