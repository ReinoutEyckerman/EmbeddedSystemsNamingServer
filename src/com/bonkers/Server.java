package com.bonkers;

import java.io.IOException;
import java.net.*;

public class Server implements ServerIntf {

    public Server() throws IOException {
        GetIPThread CreateHashTable = new GetIPThread();
        CreateHashTable.start();
    }

    public String FindLocationFile(String FileName){
        HashTableCreator obj = new HashTableCreator();
        int FileHash = obj.createHash(FileName);
        String result =obj.readHashtable(FileHash);

        if (result != null)
            return result;
        else
            return " File Not Found";
    }
}

