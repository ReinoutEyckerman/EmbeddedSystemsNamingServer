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
     * TODO Jente
     */
    private static AgentFileList instance = null;
    /**
     * TODO Jente
     */
    public List<File> fileList = null;
    /**
     * TODO Jente
     */
    public boolean started = false;
    /**
     * TODO Jente
     */
    private HashMap<File, Boolean> fileMap = new HashMap<>();
    /**
     * TODO Jente
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

    //TODO Why return? Return is unused, still necessary?

    /**
     * TODO Jente
     * @param List TODO
     * @return TODO
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
     * TODO Jente
     * @return TODO
     */
    public Client getClient()
    {
        return client;
    }

    /**
     * TODO Jente
     * @param client TODO
     */
    public void setClient(Client client)
    {
        this.client = client;
    }

    /**
     * Get the files of the node the agent runs on and check if files already exist or not TODO Jente then what?
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
     * TODO Jente
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
     * TODO Jente
     */
    private void checkUnlock()
    {
        client.unlockQueue.forEach((fileName) ->
        {
            fileMap.replace(fileName, true, false);
        });
    }
}
