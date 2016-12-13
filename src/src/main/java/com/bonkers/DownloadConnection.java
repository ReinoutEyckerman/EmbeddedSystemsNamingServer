package com.bonkers;

/**
 * Created by reinout on 9/27/16.
 */
import java.io.*;
import java.net.Socket;

/***
 * The actual and technical downloading of a file. Well, more uploading, this is the thread that a tcpserver uses to connect a client node with
 */
public class DownloadConnection implements Runnable{
    /**
     * The socket to connect with
     */
    private final Socket sock;
    /**
     * File input stream read from
     */
    private FileInputStream fis = null;
    /**
     * The buffer for the fileinputstream
     */
    private BufferedInputStream bis = null;
    /**
     * The outputstream to write to, also known as the tcp connection
     */
    private DataOutputStream os = null;
    private final String folderPath;

    public DownloadConnection(Socket client) {
        this.sock = client;
        this.folderPath="";//TODO Dynamic folderpath
    }

    @Override
    public void run() {
        try {
            os = new DataOutputStream(sock.getOutputStream());
            BufferedReader inFromClient = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            String cmd=inFromClient.readLine();
            sendFile(folderPath+cmd);
            exit();
        }catch (IOException e){
            System.out.println("ConnectionIOException caught. ");
            e.printStackTrace();
        }
    }

    /**
     * The sending of a file
     * @param filepath The location of the file
     * @throws IOException If the file isnt found or w/e
     */
    private void sendFile(String filepath) throws IOException {
        os  = new DataOutputStream(sock.getOutputStream());
        System.out.println("Waiting...");
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
        System.out.println("File sent.");
    }

    /**
     * Clear all the open streams
     * @throws IOException
     */
    private void exit() throws IOException{
        if (fis!=null) fis.close();
        if (bis != null) bis.close();
        if (os != null) os.close();
        if (sock != null) sock.close();
    }
}