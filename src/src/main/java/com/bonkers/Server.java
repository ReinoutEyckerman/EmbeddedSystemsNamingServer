package com.bonkers;

import java.io.IOException;
import java.net.InetAddress;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

/**
 * Server class that accepts client connections.
 */
public class Server implements QueueListener, ServerIntf{
    private HashTableCreator HT=null;
    private MulticastCommunicator multicast =null;
    public String error=null;

    /**
     * Main server object constructor, creates MulticastCommunicator and Hashtablecreator, and subscribes on the queueEvent object
     * @throws IOException When IO fails (?)
     */
    public Server() throws IOException {
        try {
            Registry registry = LocateRegistry.createRegistry(1099);
            ServerIntf stub = (ServerIntf) UnicastRemoteObject.exportObject(this, 0);
            registry.bind("ServerIntf", stub);
        }catch(AlreadyBoundException e){
            e.printStackTrace();
        }
        HT=new HashTableCreator();
        multicast =new MulticastCommunicator();
        multicast.start();
        multicast.packetQueue.addListener(this);
    }

    @Override
    public void packetReceived() {
        Tuple<String, String> t= multicast.packetQueue.poll();
        error = checkDoubles(t.x, t.y);
        //(error.equals("100"))
        //{
            addNode(t);
            System.out.println();
        //}
    }
    private void addNode(Tuple<String, String> t){
        try{
            Registry registry = LocateRegistry.getRegistry(t.y);
            ClientIntf stub = (ClientIntf)registry.lookup("ClientIntf");
            String[] host = InetAddress.getLocalHost().toString().split("/");
            stub.setStartingInfo(host[1],HT.getNodeAmount());
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }


    /**
     * TODO UNFINISHED CODE :^)
     * @param name Name of the thing
     * @param ip Ip address
     * @return Returns error code
     */
    private String checkDoubles(String name, String ip)
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
            HT.createHashTable(ip, name);
        }
        return resp;
    }

    @Override
    public String findLocationFile(String FileName){
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
    public String error() throws RemoteException{
        return error;
    }

    public void nodeShutdown(NodeInfo node) {
        HashTableCreator table=new HashTableCreator();
        table.htIp=table.readHashtable();
        if(table.htIp.containsKey(node.Hash)){
            table.htIp.remove(node.Hash);
            table.writeHashtable();
        }
        else throw new IllegalArgumentException("Somehow, the node that shut down didn't exist");
    }


    public NodeInfo[] nodeNeighbors(NodeInfo node) {
        HashTableCreator table=new HashTableCreator();
        Map hashmap=table.readHashtable();
        List list=new ArrayList(hashmap.keySet());
        Collections.sort(list);
        int index=list.indexOf(node.Hash);
        NodeInfo previousNeighbor=new NodeInfo((Integer)list.get(index-1),(String)hashmap.get(list.get(index-1)));
        NodeInfo nextNeighbor=new NodeInfo((Integer)list.get(index+1),(String)hashmap.get(list.get(index+1)));
        return new NodeInfo[]{previousNeighbor,nextNeighbor};
    }
}

