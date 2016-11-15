package com.bonkers;

import java.io.IOException;
import java.net.InetAddress;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Server class that accepts client connections.
 */
public class Server implements QueueListener, ServerIntf{
    private HashTableCreator HT=null;
    private MulticastCommunicator multicaster=null;
    public String error=null;

    /**
     * Main server object constructor, creates MulticastCommunicator and Hashtablecreator, and subscribes on the queueEvent object
     * @throws IOException When IO fails (?)
     * @throws InterruptedException TODO LOLWAT IS DIS
     */
    public Server() throws IOException, InterruptedException {

        HT=new HashTableCreator();
        multicaster=new MulticastCommunicator();
        multicaster.start();
        multicaster.packetQueue.addListener(this);

        System.out.println("RMI server started");
        try {
            Server obj = new Server();
            ServerIntf stub = (ServerIntf) UnicastRemoteObject.exportObject(obj, 0);

            // Bind the remote object's stub in the registry
            Registry registry = LocateRegistry.createRegistry(1099);
            registry.bind("ServerIntf", stub);

            System.out.println("Server ready");
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }

    @Override
    public void packetReceived() {
        Tuple<String, InetAddress> t=multicaster.packetQueue.poll();
        error = checkDoubles(t.x, t.y);
    }

    /**
     * TODO UNFINISHED CODE :^)
     * @param name Name of the thing
     * @param ip Ip address
     * @return Returns error code
     */
    private String checkDoubles(String name, InetAddress ip)
    {
        String resp = null;
        int hash = HT.createHash(name);
        if(HT.htIp.containsKey(hash))
        {
            resp = "201";
        }
        else if (HT.htIp.containsValue(ip))
        {
            resp = "202";
        }
        else
        {
            resp = "100";
            HT.CreateHashTable(ip, name);
        }
        return resp;
    }

    @Override
    public String FindLocationFile(String FileName){
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
    public String Error(){
        return error;
    }

    public void NodeShutdown(Tuple node) {
        HashTableCreator table=new HashTableCreator();
        Map hashmap=table.readHashtable();
        if(hashmap.containsKey(node.x)){
            hashmap.remove(node.x);
        }
        else throw new IllegalArgumentException("Somehow, the node that shut down didn't exist");
    }


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
}

