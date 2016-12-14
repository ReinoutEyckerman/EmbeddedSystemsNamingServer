package com.bonkers;

/**
 * Created by reinout on 9/27/16.
 */

import java.io.File;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Thread that allows TCP clients to connect and opens a Connection thread to the client
 * This is implemented on every node, so that every node can connect to every other node for file transfer
 */
public class TCPServer implements Runnable {

    /**
     * Predefined socket port on which the server listens
     */
    public final  int SOCKET_PORT = 12346;  // you may change othis
    private final File folderLocation;

    public TCPServer(File folderLocation) {
        this.folderLocation = folderLocation;
    }

    /**
     * Implementation of the runnable interface. It opens the socket, then waits for clients to connect,
     * and finally gives every client it's own connection threads.
     */
    @Override
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
                Thread t = new Thread(new DownloadConnection(clientSocket, folderLocation));
                t.start();
                t.join();
            } catch (Exception e) {
                System.err.println("Error " + e + " in connection attempt.");
                e.printStackTrace();
            }
        }
    }
}