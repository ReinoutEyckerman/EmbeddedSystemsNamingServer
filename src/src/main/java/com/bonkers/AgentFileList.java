package com.bonkers;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

/**
 * TODO Jente
 */
public class AgentFileList implements Runnable, Serializable {
    public HashMap<File, Boolean> FileList = new HashMap<>();

    public Boolean started = false;

    private static AgentFileList instance = null;


    protected AgentFileList() {}

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

    private Client client = null;

    public void setClient(Client client)
    {
        this.client = client;
    }

    public Client getClient() { return client; }

    @Override
    public void run() {
        getAndUpdateCurrentNodeFiles();
        checkLockRequests();
        checkUnlock();
        return;
    }

    /**
     * Get the files of the node the agent runs on and check if files already exist or not
     */
    private void getAndUpdateCurrentNodeFiles(){
        client.fm.ownedFiles.forEach((fileInfo) ->{
            FileList.putIfAbsent(new File(fileInfo.fileName), false);
        });
        System.out.println(client.fm.ownedFiles.size() + " " + FileList.size());
    }
    private void checkLockRequests(){
        client.LockQueue.forEach((fileName) -> {
            if(!FileList.replace(fileName, false, true))
            {
                client.FailedLocks.add(fileName);
            }
        });
    }
    private void checkUnlock(){
        client.UnlockQueue.forEach((fileName) ->{
            FileList.replace(fileName, true, false);
        });
    }
}
