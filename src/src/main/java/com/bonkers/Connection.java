package com.bonkers;

/**
 * Created by reinout on 9/27/16.
 */
import java.io.*;
import java.net.Socket;
import java.util.*;

public class Connection implements Runnable{
    private Socket sock;
    public final static String FolderPath = "/home/reinout/";  // you may change this
    FileInputStream fis = null;
    BufferedInputStream bis = null;
    DataOutputStream os = null;
    public Connection(Socket client) {
        this.sock = client;
    }

    @Override
    public void run() {
        boolean exit=false;
        try {
            os = new DataOutputStream(sock.getOutputStream());
            BufferedReader inFromClient = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            while (!exit){
                String cmd=inFromClient.readLine();
                String[] args =new String[3]; //Potential bug
                if(cmd.contains(" "))
                    args=cmd.split(" ");
                else args[0] =cmd;
                if(Objects.equals(args[0],"list")) {
                    for (String file : listFilesForFolder(new File(FolderPath))) {
                        os.writeBytes(file+'\n');
                    }
                    os.writeBytes("\n");
                }
                else if(Objects.equals(args[0],"get"))
                {
                    sendFile(FolderPath+args[1]);
                }
                else if(Objects.equals(args[0],"exit"))
                {
                    exit();
                    exit=true;
                }
            }
        }catch (IOException e){
            System.out.println("IOException get " +e+" caught. Exiting...");
            System.exit(1);
        }
    }

    private void sendFile(String filepath) throws IOException {

        os  = new DataOutputStream(sock.getOutputStream());
        System.out.println("Waiting...");
        // send file
        File myFile = new File(filepath);
        os.writeLong(myFile.length());
        byte[] buffer=new byte[1024];
        fis = new FileInputStream(myFile);
        bis = new BufferedInputStream(fis);
        System.out.println("Sending " + filepath + "(" + myFile.length() + " bytes)");
        int count;
        while ((count = fis.read(buffer)) > 0) {
            os.write(buffer, 0, count);
        }
        os.flush();
        System.out.println("Done.");
    }
    private void exit() throws IOException{
            if (bis != null) bis.close();
            if (os != null) os.close();
            if (sock != null) sock.close();
    }
    private List<String> listFilesForFolder(final File folder) {
        List<String> files=new ArrayList<>();
        for (final File fileEntry : folder.listFiles()) {
            if (!fileEntry.isDirectory()) {
                files.add(fileEntry.getName());
            }
        }
        return files;
    }

}