package com.bonkers;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Remote Client Interface for Client-Server Connection
 */
public interface ClientIntf extends Remote
{
    /**
     * Sets starting info for the node so it can function properly
     *
     * @param address     The server's IP address
     * @param clientCount Amount of clients/nodes currently connected
     * @throws RemoteException
     */
    void setStartingInfo(String address, int clientCount) throws RemoteException;
}
