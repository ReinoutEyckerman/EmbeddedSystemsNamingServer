package com.bonkers;

/**
 * Created by reinout on 9/27/16.
 */

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer implements Runnable {

    public final static int SOCKET_PORT = 12346;  // you may change this

    private final String fileLocation;
    public TCPServer(String fileLocation){
       this.fileLocation=fileLocation;
    }
    public void run() {
        ServerSocket serversocket = null;
        Socket clientSocket;
        try {
            serversocket = new ServerSocket(SOCKET_PORT);
        } catch (Exception e) {
            System.err.println("Port already in use.");
            System.exit(1);
        }
        System.out.println("Server succesfully started.");
        while (true) {
            try {
                clientSocket = serversocket.accept();
                System.out.println("Accepted connection : " + clientSocket);
                Thread t = new Thread(new DownloadConnection(clientSocket));
                t.start();
                t.join();
            } catch (Exception e) {
                System.err.println("Error " + e + " in connection attempt.");
                e.printStackTrace();
            }
        }
    }

}