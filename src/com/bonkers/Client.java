package com.bonkers;

import java.io.File;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;

public class Client {
    public static void main(String args[]) throws Exception {
        String host = "192.168.1.1";
        try {
            Registry registry = LocateRegistry.getRegistry(host);
            ServerIntf stub = (ServerIntf) registry.lookup("ServerIntf");
            int response = stub.getBalance();
            System.out.println(response);
            stub.withdrawMoney(10);
            response = stub.getBalance();
            System.out.println(response);
            stub.addMoney(40);
            response = stub.getBalance();
            System.out.println(response);


        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }
    private List<String> listFilesForFolder(final File folder) {
        List<String> files=new ArrayList<>();
        for (final File fileEntry : folder.listFiles()) {
            if (!fileEntry.isDirectory()) {
                files.add(fileEntry.getName());
            }
        }
        return files;
    }

}
