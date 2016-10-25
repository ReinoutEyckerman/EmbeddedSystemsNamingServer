package com.bonkers;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerIntf extends Remote {
    public String FindLocationFile(String FileName) throws RemoteException;
}
