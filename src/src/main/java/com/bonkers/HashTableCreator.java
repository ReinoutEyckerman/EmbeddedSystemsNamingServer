package com.bonkers;


import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import javax.swing.text.html.parser.Entity;
import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

/**
 * Class that handles the writing and reading of hashmap.
 * TODO: Potential singleton?
 * TODO: Jente check javadoc comments
 */
public class HashTableCreator {
    /**
     * Local hashmap for intermediate and outside use.
     */
    public TreeMap<Integer, String> htIp = new TreeMap<>();

    public InetAddress IP = null;

    public int getNodeAmount(){
        return htIp.size();
    }
    /**
     * Generates hash from string
     * @param name Original hash code
     * @return hash integer
     */
    public static int createHash(String name)
    {
        int digest = Math.abs(name.hashCode())%32768;
        return digest;
    }

    /**
     * Creates hash and writes it to the file.
     * @param ip Ip address of the hash host
     * @param name Name of the hash host of which the hash has to be calculated.
     */
    public void createHashTable(String ip, String name)
    {
        int digest = createHash(name);
        htIp=readHashtable();
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
        try{File f = new File("hashtable.json");
            if (f.exists() && !f.isDirectory())
            {
                f.delete();
            }
            FileWriter fw = new FileWriter("hashtable.json", true);
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
    public TreeMap<Integer, String> readHashtable()
    {
        TreeMap<Integer, String> Hashtable = new TreeMap<>();
        try {
            JsonReader reader = new JsonReader(new FileReader("hashtable.json"));
            reader.beginObject();

            while (reader.hasNext()){
                Hashtable.put(Integer.valueOf(reader.nextName()), reader.nextString());
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
     * @param FileHash Filehash
     * @return Connected host
     */
    //TODO (?)
    public String findHost(int FileHash) {
        final String[] IP = {null};
        if (htIp.firstEntry().getKey() > FileHash)
        {
                return htIp.lastEntry().getValue();
        }
        htIp.forEach((key,value) ->{
            if(key > FileHash && htIp.lowerEntry(key).getKey() <= FileHash)
            {
                    IP[0] = value;
            }
        });
        return IP[0];
    }
}

