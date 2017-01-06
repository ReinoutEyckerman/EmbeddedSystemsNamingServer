package com.bonkers;


import com.bonkers.Controllers.ServerCtrl;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Class that handles the writing and reading of hashmap.
 */
public class HashTableCreator
{
    /**
     * Local hashmap for intermediate and outside use.
     */
    public TreeMap<Integer, String> htIp = new TreeMap<>();

    /**
     * Generates hash from string
     *
     * @param name Original hash code
     * @return hash integer
     */
    public static int createHash(String name)
    {
        return Math.abs(name.hashCode()) % 32768;
    }

    /**
     * Get amount of nodes currently connected
     * @return amount of nodes currently connected
     */
    public int getNodeAmount()
    {
        return htIp.size();
    }

    /**
     * Creates hash and writes it to the file.
     *
     * @param ip   Ip address of the hash host
     * @param name Name of the hash host of which the hash has to be calculated.
     */
    public void createHashTable(String ip, String name)
    {
        int digest = createHash(name);
        htIp = readHashtable();
        htIp.put(digest, ip);
        writeHashtable();
        ClientsToGUI();
    }

    /**
     * Writes hash to gson file.
     */
    public void writeHashtable()
    {
        Gson gson = new Gson();
        String json = gson.toJson(htIp);
        try
        {
            File f = new File("hashtable.json");
            if (f.exists() && !f.isDirectory())
            {
                f.delete();
            }
            FileWriter fw = new FileWriter("hashtable.json", true);
            fw.write(json);
            fw.close();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Reads hashes from file.
     *
     * @return Hashtable map
     */
    public TreeMap<Integer, String> readHashtable()
    {
        TreeMap<Integer, String> Hashtable = new TreeMap<>();
        try
        {
            JsonReader reader = new JsonReader(new FileReader("hashtable.json"));
            reader.beginObject();

            while (reader.hasNext())
            {
                Hashtable.put(Integer.valueOf(reader.nextName()), reader.nextString());
            }
            reader.endObject();
            reader.close();
        } catch (IOException e)
        {
            System.err.println("Cannot read JSON");
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return Hashtable;
    }


    /**
     * Finds host connected to hash.
     *
     * @param FileHash Filehash
     * @return Connected host
     */
    public NodeInfo findHost(int FileHash)
    {
        if (htIp.firstEntry().getKey() > FileHash)
        {
            return new NodeInfo(htIp.lastEntry().getKey(), htIp.lastEntry().getValue());
        }
        for (Map.Entry<Integer, String> entry : htIp.entrySet())
        {
            if (htIp.higherEntry(entry.getKey()) != null)
            {
                if (entry.getKey() <= FileHash && htIp.higherEntry(entry.getKey()).getKey() > FileHash)
                {
                    return new NodeInfo(entry.getKey(), entry.getValue());
                }
            }
        }
        return new NodeInfo(htIp.lastEntry().getKey(), htIp.lastEntry().getValue());
    }

    private void ClientsToGUI(){
        ServerCtrl.Clients.removeAll(ServerCtrl.Clients);
        Set<Integer> keys = htIp.keySet();
        for(Integer key: keys){
            ServerCtrl.Clients.add(key+"    "+htIp.get(key));
        }
    }
}

