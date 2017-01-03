package com.bonkers;

import com.bonkers.Controllers.ClientCtrl;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * TODO Jente
 */
public class AgentFileList implements Serializable
{
    /**
     * instance of agent
     */
    private static AgentFileList instance = null;
    /**
     * file list
     */
    public List<File> fileList = null;
    /**
     * is agnet started
     */
    public boolean started = false;
    /**
     * hashmap of all files
     */
    private HashMap<File, Boolean> fileMap = new HashMap<>();
    /**
     * client it's on
     */
    private Client client = null;

    /**
     * Singleton make instance if none exists else make one
     *
     * @return instance
     */
    public static AgentFileList getInstance()
    {
        if (instance == null)
        {
            instance = new AgentFileList();
        }
        return instance;
    }

    /**
     * update file lists and lock and unlock requests
     * @param List list of files
     * @return list of files
     */
    public List<File> update(List<File> List)
    {
        fileList = List;
        started = true;
        updateCurrentNodeFiles();
        checkLockRequests();
        checkUnlock();
        fileList = new LinkedList<>();
        fileMap.forEach(((file, aBoolean) ->
        {
            fileList.add(file);
        }));
        return fileList;
    }

    /**
     * get which client it's on
     * @return client it's on
     */
    public Client getClient()
    {
        return client;
    }

    /**
     * set client its on
     * @param client client it's on
     */
    public void setClient(Client client)
    {
        this.client = client;
    }

    /**
     * Get the files of the node the agent runs on and check if files already exist or not then put them in a list
     */
    private void updateCurrentNodeFiles()
    {
        if (client.fm.ownedFiles != null)
        {
            if (client.fm.ownedFiles.size() > 0)
            {
                client.fm.ownedFiles.forEach((fileInfo) ->
                {
                    fileMap.putIfAbsent(new File(fileInfo.fileName), false);
                });
            }
            ClientCtrl.setData(fileList);
        }
        System.out.println(client.fm.ownedFiles.size() + " " + fileMap.size());
    }

    /**
     * checks if there are lock requests and execute them if it is possible
     */
    private void checkLockRequests()
    {
        client.lockQueue.forEach((fileName) ->
        {
            if (!fileMap.replace(fileName, false, true))
            {
                client.lockStatusQueue.add(new Tuple<>(fileName, false));
            }
            else
            {
                client.lockStatusQueue.add(new Tuple<>(fileName, true));
            }
        });
    }

    /**
     * checks if there are unlock requests and execute them
     */
    private void checkUnlock()
    {
        client.unlockQueue.forEach((fileName) ->
        {
            fileMap.replace(fileName, true, false);
        });
    }
}
