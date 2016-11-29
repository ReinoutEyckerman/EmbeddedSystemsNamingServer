package com.bonkers;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ReceiveFiles {
    static long size;
    static ArrayList<Long> lijst = new ArrayList();
    public void ReceiveFiles(){
        Socket socket = null;
        try {
            socket = new Socket("192.168.1.1", 2589);
        } catch (IOException ex) {
            // Do exception handling
        }
        try {
            DataInputStream dis = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            //read the number of files from the client
            int number = dis.readInt();
            ArrayList<File> files = new ArrayList<>(number);
            System.out.println("Number of Files to be received: " + number);
            //read file names, add files to arraylist
            for (int i = 0; i < number; i++) {
                File file = new File(dis.readUTF());
                size = dis.readLong();
                lijst.add(i, size);
                files.add(file);
            }
            int n;
            byte[] buf = new byte[4092];

            //outer loop, executes one for each file
            for (int i = 0; i < files.size(); i++) {
                System.out.println("Receiving file: " + files.get(i).getName());
                //create a new fileoutputstream for each new file
                File file2 = new File("/Users/Kenny/Downloads/" + files.get(i).getName());
                FileOutputStream fos = new FileOutputStream(file2);
                //read file
                while ((n = dis.read(buf)) != -1) {
                    fos.write(buf, 0, n);
                    fos.flush();
                }
                fos.close();
                System.out.println("Finished with file");
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
