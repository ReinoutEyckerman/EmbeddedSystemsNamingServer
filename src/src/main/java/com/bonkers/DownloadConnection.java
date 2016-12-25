package com.bonkers;

import java.io.*;
import java.net.Socket;

import static com.sun.xml.internal.ws.spi.db.BindingContextFactory.LOGGER;

/***
 * The actual and technical downloading of a file. Well, more uploading, this is the thread that a tcpserver uses to connect a client node with
 */
public class DownloadConnection implements Runnable
{
    /**
     * The socket to connect with
     */
    private final Socket socket;
    private final File folderPath;
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

    public DownloadConnection(Socket client, File folderPath)
    {
        LOGGER.info("Accepted connection from " + client.getInetAddress());
        this.socket = client;
        this.folderPath = folderPath;
    }

    @Override
    public void run()
    {
        try
        {
            os = new DataOutputStream(socket.getOutputStream());
            BufferedReader inFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String cmd = inFromClient.readLine();
            sendFile(folderPath.getAbsolutePath() + "/" + cmd);
            exit();
        } catch (IOException e)
        {
            LOGGER.severe("ConnectionIOException caught. ");
            e.printStackTrace();
        }
    }

    /**
     * The sending of a file
     *
     * @param filepath The location of the file
     * @throws IOException If the file isnt found or w/e
     */
    private void sendFile(String filepath) throws IOException
    {
        os = new DataOutputStream(socket.getOutputStream());
        File myFile = new File(filepath);
        os.writeLong(myFile.length());
        byte[] buffer = new byte[1024];
        fis = new FileInputStream(myFile);
        bis = new BufferedInputStream(fis);
        LOGGER.info("Sending " + filepath + "(" + myFile.length() + " bytes)");
        int count;
        while ((count = fis.read(buffer)) > 0)
        {
            os.write(buffer, 0, count);
        }
        os.flush();
        LOGGER.info("File sent.");
    }

    /**
     * Clear all the open streams
     *
     * @throws IOException
     */
    private void exit() throws IOException
    {
        if (fis != null)
        {
            fis.close();
        }
        if (bis != null)
        {
            bis.close();
        }
        if (os != null)
        {
            os.close();
        }
        if (socket != null)
        {
            socket.close();
        }
    }
}