package com.bonkers;

import java.rmi.*;


/**
 * Server interface for RMI communication.
 */
public interface ServerIntf extends Remote {
    /**
     * Finds location of file on the server.
     * @param FileName Name of the file
     * @return Returns location of the file
     * @throws RemoteException Throws exception if RMI failed.
     */
    NodeInfo findLocationFile(String FileName) throws RemoteException;
    /**
     * TODO does this even work?
     * Finds location of file on the server using its hash.
     * @param hash Name of the file
     * @return Returns location of the file
     * @throws RemoteException Throws exception if RMI failed.
     */
    NodeInfo findLocationHash(int hash) throws RemoteException;

    /**
     * Gets the current error of the server
     * @return Error string
     * @throws RemoteException Throws exception if RMI failed.
     */
    int error() throws RemoteException;

    /**
     * RMI function that removes a node from the hashtable when it shuts down.
     * @param node node tuple containing hash and IP
     * @throws RemoteException Throws exception if RMI failed.
     */
    void nodeShutdown(NodeInfo node)throws RemoteException;

    /**
     * RMI function that returns the neighbors of a failing node.
     * @param node Tuple of the node that failed.
     * @return Returns array of 2 Tuples, first tuple contains hash and ip from previous neighbor, second one contains from the next neighbor.
     * @throws RemoteException Throws exception if RMI failed.
     */

    NodeInfo[] nodeNeighbors(NodeInfo node)throws RemoteException;
}
