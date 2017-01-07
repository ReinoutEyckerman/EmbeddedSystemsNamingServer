package com.bonkers;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.Serializable;
import java.net.Socket;
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
     * Socket to check failing node
     */
    Socket socket;

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
        checkFailure();
    }

    /**
     * checking failing node
     */
    private void checkFailure() {
        try {
            socket = new Socket(failingNode.address, 5879);
            socket.setSoTimeout(10000);
        } catch (InterruptedIOException e){
            reportFailure();
            if (socket != null && !socket.isClosed()) {
                try {
                    socket.close();
                } catch (IOException f)
                {
                    f.printStackTrace(System.err);
                }
            }
        }catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Reports failure to neighbours of failing node
     */
    private void reportFailure()
    {
        //TODO write code to report failure to neighbours of failing node
    }
}
