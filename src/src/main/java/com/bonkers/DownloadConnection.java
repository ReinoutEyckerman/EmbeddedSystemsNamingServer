package com.bonkers;

/**
 * Created by reinout on 9/27/16.
 */
import java.io.*;
import java.net.Socket;

public class DownloadConnection implements Runnable{
    private final Socket sock;
    private FileInputStream fis = null;
    private BufferedInputStream bis = null;
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
    private void exit() throws IOException{
        if (fis!=null) fis.close();
        if (bis != null) bis.close();
        if (os != null) os.close();
        if (sock != null) sock.close();
    }
}