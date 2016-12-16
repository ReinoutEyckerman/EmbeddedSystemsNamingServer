package com.bonkers;

import jdk.nashorn.internal.codegen.CompilerConstants;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * TODO Jente
 */
public class AgentFileList implements Callable, Serializable {
    public HashMap<File, Boolean> FileMap = new HashMap<>();
    public List<File> Filelist = new LinkedList<>();

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
    public List<File> call() {
        getAndUpdateCurrentNodeFiles();
        checkLockRequests();
        checkUnlock();
        FileMap.forEach(((file, aBoolean) -> {
            Filelist.add(file);
        }));
        return Filelist;
    }

    /**
     * Get the files of the node the agent runs on and check if files already exist or not
     */
    private void getAndUpdateCurrentNodeFiles(){
        if(client.fm.ownedFiles != null && client.fm.ownedFiles.size() > 0)
        {
            client.fm.ownedFiles.forEach((fileInfo) ->{
                FileMap.putIfAbsent(new File(fileInfo.fileName), false);
            });
        }
        System.out.println(client.fm.ownedFiles.size() + " " + FileMap.size());
    }
    private void checkLockRequests(){
        client.LockQueue.forEach((fileName) -> {
            if(!FileMap.replace(fileName, false, true))
            {
                client.LockStatusQueue.add(new Tuple<>(fileName, false));
            }
            else
            {
                client.LockStatusQueue.add(new Tuple<>(fileName, true));
            }
        });
    }
    private void checkUnlock(){
        client.UnlockQueue.forEach((fileName) ->{
            FileMap.replace(fileName, true, false);
        });
    }
}
