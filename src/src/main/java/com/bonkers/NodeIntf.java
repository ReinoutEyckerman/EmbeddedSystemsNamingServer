package com.bonkers;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * RMI interface that defines inter-node-communication.
 */
public interface NodeIntf extends Remote
{
    /**
     * RMI function that updates the next neighbor of the targeted node
     *
     * @param node Next neighbor for that node
     * @throws RemoteException Thrown when RMI fails
     */
    void updateNextNeighbor(NodeInfo node) throws RemoteException;

    /**
     * RMI function that updates the previous neighbor of the targeted node
     *
     * @param node Previous neighbor for that node
     * @throws RemoteException Thrown when RMI fails
     */
    void updatePreviousNeighbor(NodeInfo node) throws RemoteException;

    /**
     * RMI function to transfer the current file agent to the next node
     * @param agentFileList the file agent
     * @throws RemoteException Thrown when RMI fails
     */
    void transferAgent(AgentFileList agentFileList) throws RemoteException;

    /**
     * RMI function to transfer the failure agent to the next node
     * @param agent the failure agent
     * @throws RemoteException Thrown when RMI fails
     */
    void transferFailureAgent(AgentFailure agent) throws RemoteException;

    /**
     * Sends a download request to the node to download the specified file
     *
     * @param node Node that should download the file
     * @param file file to download
     * @throws RemoteException
     */
    void requestDownload(NodeInfo node, String file) throws RemoteException;

    /**
     * Set target node as owner of the file
     * @param file The fileinfo of the file
     * @throws RemoteException Thrown when RMI fails
     */
    void setOwnerFile(FileInfo file) throws RemoteException;

    /**
     * Remove the calling node from the fileinfo with the specified file.
     * @param file
     * @param node
     * @throws RemoteException
     */
    void removeFromOwnerList(String file, NodeInfo node) throws RemoteException;
}
