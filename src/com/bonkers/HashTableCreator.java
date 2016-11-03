package com.bonkers;

import org.json.simple.JSONObject;
import org.json.simple.JsonObject;

import java.io.FileWriter;
import java.util.Hashtable;

/**
 * Created by Jente on 25/10/2016.
 */
public class HashTableCreator {
    public Hashtable htName = new Hashtable();
    public Hashtable htIp = new Hashtable();
    private int createHash(String ip)
    {
        int digest = Math.abs(ip.hashCode())%32768;
        return digest;
    }

    public void CreateHashTable(String ip, String name)
    {
        int digest = createHash(ip);
        htName.put(name, digest);
        htIp.put(digest, ip);
        JsonObject joHtIp = new JsonObject(htIp);
        try{
            FileWriter fw = new FileWriter("hashtable.json");
            fw.write(joHtIp.toJson());
            fw.close();
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
    }
}

