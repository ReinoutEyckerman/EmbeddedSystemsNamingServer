package com.bonkers;

import java.io.Serializable;
import java.util.List;

/**
 * Agent designed to check and and repair data loss appearing with node failures. Not completely implemented.
 */
public class AgentFailure implements Runnable, Serializable {
    /**
     * TODO Jente
     */
    public final NodeInfo startingNode;
    /**
     * TODO Jente
     */
    private List<Tuple<String, Boolean>> fileList;
    /**
     * TODO Jente
     */
    private NodeInfo failingNode;

    /**
     * TODO Jente
     * @param failingNode TODO
     * @param startingNode TODO
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
     * TODO Jente
     */
    private void searchFailingNode() {
        //TODO Get current files something something
        List<String> list = null;
        for (String search : list) {
            int hash = HashTableCreator.createHash(search);
        }
    }
}
