package com.bonkers;

import java.io.Serializable;
import java.util.List;

/**
 * Check for node failures
 */
public class AgentFailure implements Runnable, Serializable {
    public final NodeInfo startingNode;
    private List<Tuple<String, Boolean>> fileList;
    private NodeInfo failingNode;

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
}
