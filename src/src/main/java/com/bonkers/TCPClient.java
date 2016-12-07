package com.bonkers;

/**
 * Created by reinout on 9/27/16.
 */

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class TCPClient implements Runnable{
    public final static int SOCKET_PORT = 12346;      // Port over which the program will connect
    public String Server;  // IP Address of the server
    private Socket serverSocket=null;
    private DataOutputStream os = null;
    private FileOutputStream fos = null;
    private BufferedOutputStream bos = null;
    private DataInputStream is = null;
    private final File downloadLocation;
    private final String remoteFileLocation;
    public TCPClient(String ip, String remoteFileLocation, File downloadLocation){
        Server=ip;
        this.downloadLocation = downloadLocation; //TODO
        this.remoteFileLocation = remoteFileLocation; //TODO
    }

    public void run() {
        System.out.println("Connecting...");
        try {
            serverSocket = new Socket(Server, SOCKET_PORT);
            os = new DataOutputStream(serverSocket.getOutputStream());
            is = new DataInputStream(serverSocket.getInputStream());

            os.writeBytes(remoteFileLocation + '\n');
            getFile(downloadLocation.getPath());
            exit();
        }catch (IOException e){
            System.out.println("IO exception caught while downloading file.");
            e.printStackTrace();
        }
    }

    private void exit()throws IOException{
        if(fos!=null)fos.close();
        if(bos!=null)bos.close();
        if(is!=null)is.close();
        if (serverSocket != null) serverSocket.close();
        if(os!=null) os.close();

    }

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
}