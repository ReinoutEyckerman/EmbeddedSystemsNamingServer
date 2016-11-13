package com.bonkers;

import java.io.File;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.concurrent.Callable;


/**
 * Server subpart that handles RMI events.
 */
<<<<<<< HEAD
public class RMIServer implements  Callable,  ServerIntf {
    public String FindLocationFile(String FileName)throws InterruptedException{
=======
public class RMIServer implements Callable, ServerIntf {

    @Override
    public String FindLocationFile(String FileName){
>>>>>>> d7da951f10f87eb2cc7a8c0ef0cabf0b9ca52f5f
        HashTableCreator obj = new HashTableCreator();

        int Hash = obj.createHash(FileName);
        Map hashmap=obj.readHashtable();
        List list=new ArrayList(hashmap.keySet());
        Collections.sort(list);
        String previousNeighbor = null;
        String lastNode=(String)hashmap.get(list.get(list.size()-1));

        for (int i=0;(Integer.parseInt(list.get(i).toString())-Hash)>0;i++)
        {
            if(((Integer)list.get(i)-Hash)<0)
            {
                 previousNeighbor =(String)hashmap.get(list.get(i));
            }
        }

        if (previousNeighbor != null)
        {
            return previousNeighbor;
        }
        else
        {
            return lastNode;
       }
    }

    @Override
    public void NodeShutdown(Tuple node) {
        HashTableCreator table=new HashTableCreator();
        Map hashmap=table.readHashtable();
        if(hashmap.containsKey(node.x)){
            hashmap.remove(node.x);
        }
        else throw new IllegalArgumentException("Somehow, the node that shut down didn't exist");
    }

    @Override
    public Tuple<Tuple<Integer, String>, Tuple<Integer, String>> NodeFailure(Tuple node) {
        HashTableCreator table=new HashTableCreator();
        Map hashmap=table.readHashtable();
        List list=new ArrayList(hashmap.keySet());
        Collections.sort(list);
        int index=list.indexOf(node.x);
        Tuple<Integer, String> previousNeighbor=new Tuple<Integer,String>((Integer)list.get(index-1),(String)hashmap.get(list.get(index-1)));
        Tuple<Integer, String> nextNeighbor=new Tuple<Integer,String>((Integer)list.get(index+1),(String)hashmap.get(list.get(index+1)));
        return new Tuple<>(previousNeighbor,nextNeighbor);
    }

<<<<<<< HEAD
    public RMIServer() {}

    public Integer call() throws Exception
=======
    /**
     * Server start function.
     * @return returns error code.
     */
    public Integer call()
>>>>>>> d7da951f10f87eb2cc7a8c0ef0cabf0b9ca52f5f
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
