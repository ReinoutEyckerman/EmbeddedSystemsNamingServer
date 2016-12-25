package com.bonkers;

import java.io.Serializable;
import java.util.List;

/**
 * TODO Jente
 */
public class AgentFailure implements Runnable, Serializable {
    private List<Tuple<String, Boolean>> fileList;

    private NodeInfo failingNode;
    public final NodeInfo startingNode;

    public AgentFailure(NodeInfo failingNode, NodeInfo startingNode) {
        this.failingNode = failingNode;
        this.startingNode = startingNode;
    }

    @Override
    public void run() {
        searchFailingNode();
    }

    private void searchFailingNode() {
        //TODO Get current files something something
        List<String> list = null;
        for (String search : list) {
            int hash = HashTableCreator.createHash(search);
        }
    }

    private void UpdateCurrentNodeFiles() {
        //TODO Get current files something something
        List<String> list = null;
        boolean found;
        for (String search : list) {
            found = false;
            for (Tuple<String, Boolean> curVal : fileList) {
                if (curVal.x.contains(search)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                fileList.add(new Tuple<String, Boolean>(search, false));
            }
        }
        //TODO Set current file list to the agents file list
    }

    private void checkLockRequests() {
        //TODO
    }

    private void checkUnlock() {

    }
}
