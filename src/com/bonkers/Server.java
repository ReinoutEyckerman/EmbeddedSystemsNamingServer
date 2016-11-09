package com.bonkers;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server implements QueueListener{
    HashTableCreator HT=null;
    MulticastCommunicator multicaster=null;
    public Server() throws IOException, InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(2);

        List<Callable<Double>> Callables = Arrays.asList(
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
        checkDoubles(t.x, t.y);
    }
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

