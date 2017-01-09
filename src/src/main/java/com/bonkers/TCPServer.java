package com.bonkers;


import java.io.File;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Thread that allows TCP clients to connect and opens a Connection thread to the client
 * This is implemented on every node, so that every node can connect to every other node for file transfer
 */
public class TCPServer implements Runnable
{

    /**
     * Predefined socket port on which the server listens
     */
    private final int SOCKET_PORT = 12346;
    /**
     * Folder location where the files are located
     */
    private final File folderLocation;
    /**
     * Thread pool for limiting the amount of connections
     */
    private final ExecutorService pool;

    private Logging LOGGER = new Logging();

    public TCPServer(File folderLocation)
    {
        this.folderLocation = folderLocation;
        pool = Executors.newFixedThreadPool(10);
    }

    /**
     * Implementation of the runnable interface. It opens the socket, then waits for clients to connect,
     * and finally gives every client it's own connection threads.
     */
    @Override
    public void run()
    {
        ServerSocket serversocket = null;
        try
        {
            serversocket = new ServerSocket(SOCKET_PORT);

        } catch (Exception e)
        {
            LOGGER.logger.severe("TCP Server socket already in use. Exiting...");
            System.exit(1);
        }
        LOGGER.logger.info("TCP Server succesfully started.");
        while (true)
        {
            try
            {
                pool.execute(new DownloadConnection(serversocket.accept(), folderLocation));
            } catch (Exception e)
            {
                LOGGER.logger.warning("Error " + e + " in connection attempt.");
                e.printStackTrace();
                pool.shutdown();
            }
        }
    }
}