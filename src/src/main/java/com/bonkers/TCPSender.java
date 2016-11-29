package com.bonkers;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by Jente on 29/11/2016.
 */
public class TCPSender {
    static File file = null;

    public void TCPSender() throws IOException {
        System.out.println("starting server");

        ServerSocket FileDownloadSocket = null;
        Socket ClientAccessSocket = null;
        ArrayList<File> files = new ArrayList<File>();
        file =new File("C:\\Users\\Jente\\Documents\\test\\SettingUp a Broadcast Ready Virtual Studio_Final_Oct2016.pdf");
        files.add(file);
        file =new File("C:\\Users\\Jente\\Documents\\test\\VHDLDebuging.pdf");
        files.add(file);
        FileDownloadSocket = new ServerSocket(2589);
        ClientAccessSocket = FileDownloadSocket.accept();
        try {
            DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(ClientAccessSocket.getOutputStream()));
            System.out.println(files.size());
            //write the number of files to the server
            dos.writeInt(files.size());
            dos.flush();

            //write file names
            for (int i = 0; i < files.size(); i++) {
                dos.writeUTF(files.get(i).getName());
                dos.writeLong(files.get(i).length());
                dos.flush();
            }
            //buffer for file writing, to declare inside or outside loop?
            int n = 0;
            byte[] buf = new byte[1024];
            //outer loop, executes one for each file
            for (int i = 0; i < files.size(); i++) {
                System.out.println(files.get(i).getName() + files.get(i).length());
                //create new fileinputstream for each file
                FileInputStream fis = new FileInputStream(files.get(i));
                dos = new DataOutputStream(new BufferedOutputStream(ClientAccessSocket.getOutputStream()));
                //write file to dos
                while ((n = fis.read(buf)) != -1) {
                    dos.write(buf, 0, n);
                    dos.flush();
                }
                Thread.sleep(20);
            }
        } catch(Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
