package com.bonkers;

import java.io.IOException;
import java.net.InetAddress;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Server class that accepts client connections.
 */
public class Server implements QueueListener{
    private HashTableCreator HT=null;
    private MulticastCommunicator multicaster=null;

    /**
     * Main server object constructor, creates MulticastCommunicator and Hashtablecreator, and subscribes on the queueEvent object
     * @throws IOException When IO fails (?)
     * @throws InterruptedException TODO LOLWAT IS DIS
     */
    public Server() throws IOException, InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(2);

        List<Callable<Integer>> Callables = Arrays.asList(
                new RMIServer()
        );
        executor.invokeAll(Callables).stream().map(future -> {
            try {
                return future.get();
            }
            catch (Exception e)
            {
                throw new IllegalStateException(e);
            }
        }).forEach(System.out::println);
        executor.shutdownNow();
        HT=new HashTableCreator();
        multicaster=new MulticastCommunicator();
        multicaster.start();
        multicaster.packetQueue.addListener(this);
    }

    @Override
    public void packetReceived() {
        Tuple<String, InetAddress> t=multicaster.packetQueue.poll();
        //TODO ? checkDoubles(t.x, t.y);
        try {
            Registry registry = LocateRegistry.getRegistry(t.y.toString());

            ClientIntf stub = (ClientIntf) registry.lookup("ClientIntf");
            stub.SetServerIp(InetAddress.getLocalHost().toString());
        } catch(Exception e){

        }
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
}

