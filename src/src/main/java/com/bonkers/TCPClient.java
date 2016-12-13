package com.bonkers;

/**
 * Created by reinout on 9/27/16.
 */

import java.io.*;
import java.net.Socket;

/**
 * Thread that connects to a TCPServer, aka another node. This then downloads the specified file from the connected server.
 * Note: this only downloads a single file. Multiple clients need to be created for multiple downloads. This allows for parallel download.
 */
public class TCPClient implements Runnable{
    /**
     * Port over which the connection will take place
     */
    public final static int SOCKET_PORT = 12346;
    /**
     * Ip adress of the server to which the client connects
     */
    private String Server;  // IP Address of the server
    /**
     * Socket to which is connected
     */
    private Socket serverSocket=null;
    /**
     * Stream to which the server data gets written
      */
    private DataOutputStream os = null;
    /**
     * Uses the bufferoutputstream to output to the file
     */
    private FileOutputStream fos = null;
    /**
     * buffers the output for the fileoutputstream
     */
    private BufferedOutputStream bos = null;
    /**
     * Inputstream for the server socket
      */
    private DataInputStream is = null;
    /**
     * The location to which the file will get downloaded
     */
    private final File downloadLocation;
    /**
     * The name of the remote file to download
     */
    private final String remoteFileLocation;

    /**
     * Constructor
     * @param ip ip address of the remote server
     * @param remoteFileLocation Name of the remote file
     * @param downloadLocation download location
     */
    public TCPClient(String ip, String remoteFileLocation, File downloadLocation){
        Server=ip;
        this.downloadLocation = downloadLocation; //TODO
        this.remoteFileLocation = remoteFileLocation; //TODO
    }

    /**
     * Threading implementation that connects to the server and requests the file to download.
     */
    @Override
    public void run() {
        System.out.println("Connecting...");
        try {
            serverSocket = new Socket(Server, SOCKET_PORT);
            os = new DataOutputStream(serverSocket.getOutputStream());
            is = new DataInputStream(serverSocket.getInputStream());

            os.writeBytes(remoteFileLocation + '\n');
            getFile(downloadLocation.getPath()+remoteFileLocation);
            exit();
        }catch (IOException e){
            System.out.println("IO exception caught while downloading file.");
            e.printStackTrace();
        }
    }

    /**
     * Gets the actual file from the server and writes it to the download directory
     * @param destinyFilePath The directory to which it will be downloaded
     * @throws IOException
     */
    private void getFile(String destinyFilePath) throws IOException{
        int bytesRead;
        // receive file
        byte [] buffer  = new byte [1024];
        fos = new FileOutputStream(destinyFilePath);
        bos = new BufferedOutputStream(fos);
        long fileLength=is.readLong();
        long bytesReceived=0;
        do {
            bytesRead = is.read(buffer);
            bos.write(buffer, 0, bytesRead);
            bytesReceived+=bytesRead;
        }while(bytesReceived<fileLength);
        bos.flush(); //Force buffered bytes to be written
        System.out.println("File " +destinyFilePath + " downloaded." );
    }

    /**
     * Closes all open streams when the thread is finishing
     * @throws IOException Gets thrown when one of the streams throws an error
     */
    private void exit()throws IOException{
        if(fos!=null)fos.close();
        if(bos!=null)bos.close();
        if(is!=null)is.close();
        if (serverSocket != null) serverSocket.close();
        if(os!=null) os.close();

    }
}