package com.bonkers;

import java.io.IOException;
import java.net.InetAddress;
import java.rmi.AccessException;
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
    /**
     * The hash table, essential to its functionality
     */
    private HashTableCreator HT=null;
    /**
     * The multicast listener, listens to multicast clients wanting to joing
     */
    private MulticastCommunicator multicast =null;
    /**
     * Error string
     */
    public int error;

    /**
     * Main server object constructor, creates MulticastCommunicator and Hashtablecreator, and subscribes on the queueEvent object
     */
    public Server()  {
        try {
            Registry registry = LocateRegistry.createRegistry(1099);
            ServerIntf stub = (ServerIntf) UnicastRemoteObject.exportObject(this, 0);
            registry.bind("ServerIntf", stub);
        }catch(AlreadyBoundException e){
            e.printStackTrace();
        } catch (AccessException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        HT=new HashTableCreator();
        multicast =new MulticastCommunicator();
        multicast.start();
        multicast.packetQueue.addListener(this);
    }

    /**
     * Subscriptor of the queueEvent
     * Gets run when a multicast packet is received, then checks for double entries and then adds the node
     */
    @Override
    public void queueFilled() {
        Tuple<String, String> t= multicast.packetQueue.poll();
        error = checkDoubles(t.x, t.y);
        //(error.equals("100"))
        //{
            addNode(t);
            System.out.println();
        //}
    }

    /**
     * Sets the starting info at the new node. //TODO better naming? Node gets added at checkdoubles
     * @param t
     */
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
         * This checks for duplicates and adds them to the hashtable if not duplicate.
         * @param name Name of the device
         * @param ip Ip address
         * @return Returns error code
         */
        private int checkDoubles(String name, String ip)
        {
            int resp;
            int hash = HT.createHash(name);
            if(HT.htIp.containsKey(hash))
            {
                resp = 201;
            }
            else if (HT.htIp.containsValue(ip))
            {
                resp = 202;
            }
            else
            {
                resp = 100;
                HT.createHashTable(ip, name);
            }
            return resp;
        }

        @Override
        public NodeInfo findLocationFile(String file){

            return findLocationHash(HT.createHash(file));
        }
        @Override
        public NodeInfo findLocationHash(int hash){
            List list=new ArrayList(HT.htIp.keySet());
            Collections.sort(list);
            String previousNeighbor = HT.findHost(hash);
            String lastNode=(String)HT.htIp.get(list.get(list.size()-1));
            if (previousNeighbor != null)
                return new NodeInfo(HashTableCreator.createHash(previousNeighbor), previousNeighbor);
            else
                return new NodeInfo(HashTableCreator.createHash(previousNeighbor), previousNeighbor);
        }
        @Override
        public int error() throws RemoteException{
            return error;
        }

        @Override
        public void nodeShutdown(NodeInfo node) {
            HT.htIp=HT.readHashtable();
            if(HT.htIp.containsKey(node.Hash)){
                HT.htIp.remove(node.Hash);
                HT.writeHashtable();
            }
            else throw new IllegalArgumentException("Somehow, the node that shut down didn't exist");
        }

        @Override
        public NodeInfo[] nodeNeighbors(NodeInfo node) {
            Map hashmap=HT.readHashtable();
            List list=new ArrayList(hashmap.keySet());
            Collections.sort(list);
            int index=list.indexOf(node.Hash);
            if(hashmap.size()>2) {
                NodeInfo previousNeighbor = new NodeInfo((Integer) list.get(index - 1), (String) hashmap.get(list.get(index - 1)));
                NodeInfo nextNeighbor = new NodeInfo((Integer) list.get(index + 1), (String) hashmap.get(list.get(index + 1)));
                return new NodeInfo[]{previousNeighbor, nextNeighbor};
            }
            else{
                NodeInfo neighbor=new NodeInfo((Integer) list.get(1-index), (String) hashmap.get(list.get(1-index)));
                return new NodeInfo[]{neighbor,neighbor};
            }
        }
    }

