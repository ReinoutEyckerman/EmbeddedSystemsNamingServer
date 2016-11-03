package com.bonkers;

import com.google.gson.Gson;

import java.io.FileWriter;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/**
 * Created by Jente on 25/10/2016.
 */
public class HashTableCreator {
    public Map htIp = new HashMap();
    private int createHash(String ip)
    {
        int digest = Math.abs(ip.hashCode())%32768;
        return digest;
    }

    public void CreateHashTable(String ip, String name)
    {
        int digest = createHash(name);
        htIp.put(digest, ip);
    }
    public void writeHashtable(Hashtable htIp)
    {
        Gson gson = new Gson();
        String json = gson.toJson(htIp);
        try{
            FileWriter fw = new FileWriter("hashtable.json");
            fw.write(json);
            fw.close();
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
    }
}

