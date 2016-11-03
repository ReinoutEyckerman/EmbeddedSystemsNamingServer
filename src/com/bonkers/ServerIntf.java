package com.bonkers;

import java.rmi.*;


public interface ServerIntf extends Remote {
    String FindLocationFile(String FileName) throws RemoteException;
}
