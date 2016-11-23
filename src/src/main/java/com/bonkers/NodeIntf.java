package com.bonkers;

import java.rmi.Remote;
import java.rmi.RemoteException;

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
    void transferAgent(Agent agent) throws RemoteException;
    void transferDoubleAgent(DoubleAgent agent) throws RemoteException;
}
