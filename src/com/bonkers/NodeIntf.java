package com.bonkers;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by reinout on 11/8/16.
 */
public interface NodeIntf extends Remote {
    void UpdateNextNeighbor(Tuple node)throws RemoteException;
    void UpdatePreviousNeighbor(Tuple node)throws RemoteException;
}
