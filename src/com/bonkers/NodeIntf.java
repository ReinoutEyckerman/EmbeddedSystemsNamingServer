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
    void UpdateNextNeighbor(Tuple node)throws RemoteException;

    /**
     * RMI function that updates the previous neighbor of the targeted node
     * @param node Previous neighbor for that node
     * @throws RemoteException Thrown when RMI fails
     */
    void UpdatePreviousNeighbor(Tuple node)throws RemoteException;
}
