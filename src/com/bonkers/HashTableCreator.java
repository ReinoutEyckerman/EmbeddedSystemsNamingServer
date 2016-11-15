package com.bonkers;


import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.*;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Class that handles the writing and reading of hashmap.
 * TODO: Potential singleton?
 */
public class HashTableCreator {
    /**
     * Local hashmap for intermediate and outside use.
     */
    public Map htIp = new HashMap();

    /**
     * Generates hash from string
     * @param name Original hash code
     * @return hash integer
     */
    public int createHash(String name)
    {
        int digest = Math.abs(name.hashCode())%32768;
        return digest;
    }

    /**
     * Creates hash and writes it to the file.
     * @param ip Ip address of the hash host
     * @param name Name of the hash host of which the hash has to be calculated.
     */
    public void CreateHashTable(InetAddress ip, String name)
    {
        int digest = createHash(name);
        htIp.put(digest, ip);
        writeHashtable();
    }

    /**
     * Writes hash to gson file.
     */
    public void writeHashtable()
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

    /**
     * Reads hashes from file.
     * @return Hashtable map
     */
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


    /**
     * Finds host connected to hash.
     * @param mp Hashmap
     * @param FileHash Filehash
     * @return Connected host
     */
    //TODO (?)
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

