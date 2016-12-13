package com.bonkers;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Checks for local files and changes
 */
public class FileChecker {
    /**
     * The folder location of the files
     */
    private final File folderLocation;

    /**
     * Constructor that basically only sets the folder location
     * @param folderLocation Location of the folder as a File object
     */
    public FileChecker(File folderLocation){
        this.folderLocation=folderLocation;
    }

    /**
     * Check for files locally, should only be used if you want a complete Map, not if you want additions to an existing Map
     * @param id The current node id
     * @return The Map of files
     */
    public Map checkFiles(NodeInfo id){
        Map<String,NodeInfo> files=new HashMap<>();
        for (final File fileEntry : folderLocation.listFiles()) {
            if (!fileEntry.isDirectory()) {
                files.put(fileEntry.getName(),id);
            }
        }
        return files;
    }

    /**
     * Check for files locally, Updates an already existing Map
     * @param id The current node id
     * @return The Map of files
     */
    public Map checkFiles(NodeInfo id,Map existingFiles){
        Map<String, NodeInfo> files=new HashMap<>();
        for (final File fileEntry : folderLocation.listFiles()) {
            if (!fileEntry.isDirectory() && !existingFiles.containsKey(fileEntry.getName())) {
                files.put(fileEntry.getName(),id);
            }
        }
        return files;

    }
}
