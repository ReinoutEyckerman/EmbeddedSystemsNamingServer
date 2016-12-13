package com.bonkers;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * RMI interface that defines inter-node-communication.
 */
public interface NodeIntf extends Remote {
    /**
     * RMI function that updates the next neighbor of the targeted node
     * @param node Next neighbor for that node
     * @throws RemoteException Thrown when RMI fails
     */
    void updateNextNeighbor(NodeInfo node)throws RemoteException;

    /**
     * RMI function that updates the previous neighbor of the targeted node
     * @param node Previous neighbor for that node
     * @throws RemoteException Thrown when RMI fails
     */
    void updatePreviousNeighbor(NodeInfo node)throws RemoteException;
    void transferAgent(AgentFileList agentFileList) throws RemoteException;
    void transferDoubleAgent(AgentFailure agent) throws RemoteException;

    /**
     * Sends a download request to the node to download the specified file
     * @param node Node that should download the file
     * @param file file to download
     * @throws RemoteException
     */
    void requestDownload(NodeInfo node, String file) throws RemoteException;
}
