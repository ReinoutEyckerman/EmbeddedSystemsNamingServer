package com.bonkers;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;

/**
 * Created by reinout on 12/4/16.
 */
public class FileManager {
    private final File folderLocation;
    public FileManager(File folderLocation){
        this.folderLocation=folderLocation;
    }

    /**
     * Returns list of files as strings in a specified folder.
     * @param folderLocation Folder File for where to search for files.
     * @return List of strings of the filenames in the folder.
     */
    private List<String> listFilesForFolder(File folderLocation) {
        List<String> files=new ArrayList<>();
        for (final File fileEntry : folderLocation.listFiles()) {
            if (!fileEntry.isDirectory()) {
                files.add(fileEntry.getName());
            }
        }
        return files;
    }
    public List<String> CheckIfOwner(NodeInfo currNode, NodeInfo prevNode, NodeInfo nextNode)
    {
        List<String> OwnerOfList = new ArrayList<>();
        List<String> fileList = listFilesForFolder(folderLocation);
        fileList.listIterator().forEachRemaining((file)->{
            int fileHash = HashTableCreator.createHash(file);
            if(fileHash > currNode.Hash)
            {
                if(fileHash < nextNode.Hash)
                {
                    OwnerOfList.add(file);
                }
            }
        });
        return OwnerOfList;
    }
}
