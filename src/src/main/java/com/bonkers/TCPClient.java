package com.bonkers;

/**
 * Created by reinout on 9/27/16.
 */

import java.io.*;
import java.net.Socket;
import java.util.Objects;
import java.util.Scanner;

public class TCPClient {
    public final static int SOCKET_PORT = 12345;      // Port over which the program will connect
    public String Server;  // IP Address of the server
    private Socket serverSocket=null;
    private DataOutputStream os = null;
    private FileOutputStream fos = null;
    private BufferedOutputStream bos = null;
    private DataInputStream is = null;

    public TCPClient(String ip){
        Server=ip;
    }

    public void run() throws IOException {
        Scanner s = new Scanner(System.in);
        System.out.println("Connecting...");
        serverSocket = new Socket(Server, SOCKET_PORT);
        os=new DataOutputStream(serverSocket.getOutputStream());
        is=new DataInputStream(serverSocket.getInputStream());
        printFiles();
        boolean exit=false;
        while (!exit) {
            System.out.println("Please select which to get, list or exit");
            String cmd = s.nextLine();
            String[] args = new String[3]; //Potential bug
            if (cmd.contains(" "))
                args = cmd.split(" ");
            else args[0] = cmd;
            if (Objects.equals(args[0] , "list")) {
                printFiles();
            } else if (Objects.equals(args[0], "get")) {
                os.writeBytes(cmd+'\n');
                getFile(args[2]);
            } else if (Objects.equals(args[0] , "exit")) {
                os.writeBytes(cmd+'\n');
                exit();
                exit = true;
            }
        }
    }
    private void printFiles() throws IOException{
        os.writeBytes("list\n");
        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
        System.out.println("Available Files");
        String inputLine;
        while (!Objects.equals(inputLine = inFromServer.readLine(),"")) {
            System.out.println(inputLine);
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