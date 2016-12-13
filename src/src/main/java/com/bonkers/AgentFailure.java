package com.bonkers;

import java.io.Serializable;
import java.util.List;

/**
 * TODO Jente
 */
public class AgentFailure implements Runnable, Serializable {
    List<Tuple<String, Boolean>> FileList;

    NodeInfo failingNode, startingNode;
    public AgentFailure(NodeInfo failingNode, NodeInfo startingNode){
        this.failingNode=failingNode;
        this.startingNode=startingNode;
    }

    @Override
    public void run() {
        searchFailingNode();
    }
    private void searchFailingNode(){
        //TODO Get current files something something
        List<String> s=null;
        for(String search:s) {
            int hash = HashTableCreator.createHash(search);
        }
    }
    private void getAndUpdateCurrentNodeFiles(){
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
