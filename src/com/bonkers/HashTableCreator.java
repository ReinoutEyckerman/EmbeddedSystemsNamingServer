package com.bonkers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import jdk.nashorn.internal.parser.JSONParser;

import java.io.*;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;


/**
 * Created by Jente on 25/10/2016.
 */
public class HashTableCreator {
    public Map htIp = new HashMap();
    public int createHash(String name)
    {
        int digest = Math.abs(name.hashCode())%32768;
        return digest;
    }

    public void CreateHashTable(InetAddress ip, String name)
    {
        int digest = createHash(name);
        htIp.put(digest, ip);
        writeHashtable(htIp);
    }
    public void writeHashtable(Map htIp)
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
    public Map readHashtable()
    {
        Map Hashtable = new HashMap();
        try {
            JsonReader reader = new JsonReader(new FileReader("hashtable.json"));
            reader.beginObject();

            while (reader.hasNext()){
                Hashtable.put(reader.nextName(), reader.nextString());
            }
            reader.endObject();
            reader.close();
        }
        catch (IOException e){
            System.err.println("Cannot read JSON");
        }
        catch (Exception e){
            System.err.println(e);
        }
        return Hashtable;
    }
    public String FindHost(Map mp, String FileHash)
    {
        String IP="";
        Iterator it = mp.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            if (pair.getKey().equals(FileHash)) {
                IP = pair.getValue().toString();
            }
        }
        return IP;
    }
}

