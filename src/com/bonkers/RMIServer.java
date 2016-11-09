package com.bonkers;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.concurrent.Callable;

/**
 * Created by Jente on 8/11/2016.
 */
public class RMIServer implements Callable, ServerIntf {
    Map hashmap=new HashMap();
    public String FindLocationFile(String FileName){
        HashTableCreator obj = new HashTableCreator();
        String FileHash = FileName;//obj.createHash(FileName);
        String result = obj.FindHost(obj.readHashtable(), FileHash);

        if (result != null)
            return result;
        else
            return " File Not Found";
    }

    @Override
    public void NodeShutdown(Tuple node) {
        //TODO: WHOAT THE FUCK?
        if(hashmap.containsKey(node.x)){
            hashmap.remove(node.x);
        }
        else throw new IllegalArgumentException("Somehow, the node that shut down didn't exist");
    }

    @Override
    public Tuple<Tuple<Integer, String>, Tuple<Integer, String>> NodeFailure(Tuple node) {
        List list=new ArrayList(hashmap.keySet());
        Collections.sort(list);
        int index=list.indexOf(node.x);
        Tuple<Integer, String> previousNeighbor=new Tuple<Integer,String>((Integer)list.get(index-1),(String)hashmap.get(list.get(index-1)));
        Tuple<Integer, String> nextNeighbor=new Tuple<Integer,String>((Integer)list.get(index+1),(String)hashmap.get(list.get(index+1)));
        return new Tuple<>(previousNeighbor,nextNeighbor);
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

            System.out.println("Server ready");
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
        return 0;
    }
}