package com.bonkers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import jdk.nashorn.internal.parser.JSONParser;

import java.io.*;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Hashtable;
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
    public String readHashtable(String FileHash)
    {
        String IP="";

        try {
            JsonReader reader = new JsonReader(new FileReader("hashtable.json"));
            reader.beginObject();

            while (reader.hasNext()){
                String Name = reader.nextName();
                if (Name.equals(FileHash)){
                    IP = reader.nextString();
                }
            }
            reader.endObject();
            reader.close();
        }
        catch (FileNotFoundException e){
            System.out.println("File Not Found");
        }
        catch (Exception e){
            System.out.println(e);
        }
        return IP;
    }
}

