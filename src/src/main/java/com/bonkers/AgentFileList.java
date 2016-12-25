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
public class AgentFileList implements Serializable {
    private static AgentFileList instance = null;
    private HashMap<File, Boolean> fileMap = new HashMap<>();
    public List<File> fileList = null;
    public Boolean started = false;
    private Client client = null;

    /**
     * Singleton make instance if none exists else make one
     *
     * @return instance
     */
    public static AgentFileList getInstance() {
        if (instance == null) {
            instance = new AgentFileList();
        }
        return instance;
    }
    //TODO Why return? Return is unused, still necessary?
    public List<File> update(List<File> List) {
        fileList = List;
        started = true;
        updateCurrentNodeFiles();
        checkLockRequests();
        checkUnlock();
        fileList = new LinkedList<>();
        fileMap.forEach(((file, aBoolean) -> {
            fileList.add(file);
        }));
        return fileList;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    /**
     * Get the files of the node the agent runs on and check if files already exist or not
     */
    private void updateCurrentNodeFiles() {
        if (client.fm.ownedFiles != null) {
            if (client.fm.ownedFiles.size() > 0) {
                client.fm.ownedFiles.forEach((fileInfo) -> {
                    fileMap.putIfAbsent(new File(fileInfo.fileName), false);
                });
            }
            ClientCtrl.setData(fileList);
        }
        System.out.println(client.fm.ownedFiles.size() + " " + fileMap.size());
    }

    private void checkLockRequests() {
        client.lockQueue.forEach((fileName) -> {
            if (!fileMap.replace(fileName, false, true)) {
                client.lockStatusQueue.add(new Tuple<>(fileName, false));
            } else {
                client.lockStatusQueue.add(new Tuple<>(fileName, true));
            }
        });
    }

    private void checkUnlock() {
        client.unlockQueue.forEach((fileName) -> {
            fileMap.replace(fileName, true, false);
        });
    }
}
