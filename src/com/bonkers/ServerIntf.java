package com.bonkers;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerIntf extends Remote {
    public int getBalance() throws RemoteException;
    public void addMoney(int money) throws RemoteException;
    public void withdrawMoney(int money) throws RemoteException;
}
