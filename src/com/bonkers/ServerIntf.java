package com.bonkers;

import java.rmi.*;


public interface ServerIntf extends Remote {
    String FindLocationFile(String FileName) throws RemoteException;
    void NodeShutdown(Tuple node);
    Tuple<Tuple<Integer,String>,Tuple<Integer,String>> NodeFailure(Tuple node);
}
