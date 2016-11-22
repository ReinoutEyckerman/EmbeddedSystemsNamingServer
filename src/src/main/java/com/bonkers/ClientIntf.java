package com.bonkers;

import java.net.InetAddress;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by reinout on 11/15/16.
 */
public interface ClientIntf extends Remote {
    void setStartingInfo(String address, int clientcount) throws RemoteException, Exception;
    void setNameError()throws RemoteException;
}
