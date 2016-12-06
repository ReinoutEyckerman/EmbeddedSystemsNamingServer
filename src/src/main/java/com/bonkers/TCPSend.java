package com.bonkers;

/**
 * Created by reinout on 9/27/16.
 */

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPSend {

    public final static int SOCKET_PORT = 12346;  // you may change this

    private final String fileLocation;
    public TCPSend(String fileLocation){
       this.fileLocation=fileLocation;
    }
    public void run() throws IOException {
        ServerSocket serversocket = null;
        Socket clientsocket = null;
        try {
            serversocket = new ServerSocket(SOCKET_PORT);
        } catch (Exception e) {
            System.err.println("Port already in use.");
            System.exit(1);
        }
        System.out.println("Server is running.");
        //TODO RMI HERE
        //TODO RMI HERE
        boolean connected=false;
        while (!connected) {
            try {
                clientsocket = serversocket.accept();
                System.out.println("Accepted connection : " + clientsocket);
                Thread t = new Thread(new Connection(clientsocket));
                t.start();
                t.join();
                connected=true;
            } catch (Exception e) {
                System.err.println("Error " + e + " in connection attempt.");
            }
        }
    }

}