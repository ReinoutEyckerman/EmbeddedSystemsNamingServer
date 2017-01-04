package com.bonkers;

import java.io.Serializable;
import java.util.List;

/**
 * Agent designed to check and and repair data loss appearing with node failures. Not completely implemented.
 */
public class AgentFailure implements Runnable, Serializable {
    /**
     * Node it's starting from
     */
    public final NodeInfo startingNode;
    /**
     * Failing Node info
     */
    private NodeInfo failingNode;

    /**
     * setting params right
     * @param failingNode Failing Node info
     * @param startingNode Node it's starting from
     */
    public AgentFailure(NodeInfo failingNode, NodeInfo startingNode) {
        this.failingNode = failingNode;
        this.startingNode = startingNode;
    }

    @Override
    public void run() {
        searchFailingNode();
    }

    /**
     * Searching for failing node
     */
    private void searchFailingNode() {
        //create list of all nodes in network
        List<String> list = null;
        for (String search : list) {
            int hash = HashTableCreator.createHash(search);
        }
    }
}
