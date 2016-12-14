package com.bonkers;

import javafx.beans.value.ObservableBooleanValue;
import javafx.beans.value.ObservableListValue;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TODO Jente
 */
public class AgentFileList implements Runnable, Serializable, QueueListener {
    public HashMap<File, Boolean> FileList = new HashMap<>();

    public Boolean started = false;

    public QueueEvent<File> LockRequestQueue = new QueueEvent<>();
    public QueueEvent<File> UnlockRequestQueue = new QueueEvent<>();

    //public ObservableListValue<List<>>


    private static AgentFileList instance = null;

    protected AgentFileList() {
        LockRequestQueue.addListener(this);
        UnlockRequestQueue.addListener(this);
    }

    /**
     * Singleton make instance if none exists else make one
     * @return instance
     */
    public static AgentFileList getInstance() {
        if(instance == null) {
            instance = new AgentFileList();
        }
        return instance;
    }

    private List<FileInfo> ClientFileList = null;

    public void setClientFileList(List<FileInfo> ClientFileList)
    {
        this.ClientFileList = ClientFileList;
    }

    public List<FileInfo> getClientFileList()
    {
        return ClientFileList;
    }

    @Override
    public void run() {
        getAndUpdateCurrentNodeFiles();
        checkLockRequests();
        checkUnlock();
    }

    /**
     * Get the files of the node the agent runs on and check if files already exist or not
     */
    private void getAndUpdateCurrentNodeFiles(){

        ClientFileList.forEach((fileInfo) -> {
            FileList.putIfAbsent(new File(fileInfo.fileName), false);
        });
    }
    private void checkLockRequests(){
        //TODO
    }
    private void checkUnlock(){

    }

    @Override
    public void queueFilled() {
        if(LockRequestQueue.notifyPacketReceived())
        {
            FileList.replace(LockRequestQueue.poll(),false, true);
        }
        else if(UnlockRequestQueue.notifyPacketReceived())
        {
            FileList.replace(UnlockRequestQueue.poll(),true, false);
        }
    }
}
