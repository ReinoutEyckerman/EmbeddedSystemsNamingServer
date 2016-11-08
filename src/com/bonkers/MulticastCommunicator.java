package com.bonkers;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

/**
 * Created by reinout on 11/3/16.
 */
public class MulticastCommunicator extends Thread {
    //https://docs.oracle.com/javase/7/docs/api/java/net/MulticastSocket.html
    private final String castAdress = "234.5.6.7";
    private InetAddress group = null;
    private MulticastSocket castSocket = null;

    public MulticastCommunicator(String name) {
        JoinGroup();
        SendMulticast(name);
    }

    public void run(){

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

    public void ReceiveMulticast() {

        try {
            byte[] buf = new byte[1000];
            DatagramPacket recv = new DatagramPacket(buf, buf.length);
            castSocket.receive(recv);
            //TODO: Process packet
        } catch (Exception e) {

        }
    }

    public void LeaveGroup() {
        try {
            castSocket.leaveGroup(group);
        } catch (Exception e) {
        }
    }

}
