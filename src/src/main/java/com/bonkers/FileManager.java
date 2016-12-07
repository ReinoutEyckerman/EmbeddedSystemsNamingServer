package com.bonkers;


/**
 * Created by reinout on 12/4/16.
 */
public class FileManager implements QueueListener{
    private final String folderLocation;
    private QueueEvent<String> downloadQueue=new QueueEvent<>();
    /**
     * 
     * @param folderLocation
     */
    public FileManager(String folderLocation){
        this.folderLocation=folderLocation;
        downloadQueue.addListener(this);
    }

    @Override
    public void queueFilled() {
        new Thread(new TCPClient("","","")).start();//TODO

    }
}
