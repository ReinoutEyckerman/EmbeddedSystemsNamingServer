package com.bonkers;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by reinout on 11/22/16.
 */
public class AgentFileList implements Runnable, Serializable {
    List<Tuple<String, Boolean>> FileList;

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

    @Override
    public void run() {
        getAndUpdateCurrentNodeFiles();
        checkLockRequests();
        checkUnlock();
    }
    private void getAndUpdateCurrentNodeFiles(){

        for (String fileName: Client.ownerOfFilesList) {
            FileList.add(new Tuple<String, Boolean>(fileName, false));
        }
        //TODO Get current files something something
        List<String> s=null;
        boolean found;
        for(String search:s){
            found=false;
            for (Tuple<String, Boolean> curVal : FileList) {
                if (curVal.x.contains(search)) {
                   found=true;
                    break;
                }
            }
            if(!found){
                FileList.add(new Tuple<String, Boolean>(search, false));
            }
        }
        //TODO Set current file list to the agents file list
    }
    private void checkLockRequests(){
        //TODO
    }
    private void checkUnlock(){

    }
}
