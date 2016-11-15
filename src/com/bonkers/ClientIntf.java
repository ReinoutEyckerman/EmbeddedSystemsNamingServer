package com.bonkers;

import java.net.InetAddress;
import java.rmi.RemoteException;

/**
 * Created by reinout on 11/15/16.
 */
public interface ClientIntf {
    void SetServerIp(String address) throws RemoteException;
}
