package com.bonkers;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.Callable;

/**
 * Created by Jente on 8/11/2016.
 */
public class RMIServer implements Callable, ServerIntf {

    public String FindLocationFile(String FileName){
        HashTableCreator obj = new HashTableCreator();
        int FileHash = obj.createHash(FileName);
        String result = obj.readHashtable(FileHash);

        if (result != null)
            return result;
        else
            return " File Not Found";
    }

    @Override
    public void NodeShutdown(Tuple node) {
        //TODO: WHAT THE FUCK?
    }

    @Override
    public Tuple<Tuple<Integer, String>, Tuple<Integer, String>> NodeFailure(Tuple node) {
        //TODO: Zie NodeShutdown
        return null;
    }

    public RMIServer() {}

    public Integer call()
    {
        System.out.println("RMI server started");
        try {
            RMIServer obj = new RMIServer();
            ServerIntf stub = (ServerIntf) UnicastRemoteObject.exportObject(obj, 0);

            // Bind the remote object's stub in the registry
            Registry registry = LocateRegistry.createRegistry(1099);
            registry.bind("ServerIntf", stub);

            System.err.println("Server ready");
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
        return 0;
    }
}
