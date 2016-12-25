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
     * @param clientcount Amount of clients/nodes currently connected
     * @throws RemoteException
     * @throws Exception
     */
    void setStartingInfo(String address, int clientcount) throws RemoteException;

    /**
     * This is created, but not used for it's purpose
     * TODO Joris Jente?
     *
     * @throws RemoteException
     */
    void setNameError() throws RemoteException;
}
