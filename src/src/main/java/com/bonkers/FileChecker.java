package com.bonkers;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by reinout on 12/7/16.
 */
public class FileChecker {

    private final File folderLocation;

    public FileChecker(File folderLocation){
        this.folderLocation=folderLocation;
    }

    public Map checkFiles(NodeInfo id){
        Map<String,NodeInfo> files=new HashMap<>();
        for (final File fileEntry : folderLocation.listFiles()) {
            if (!fileEntry.isDirectory()) {
                files.put(fileEntry.getName(),id);
            }
        }
        return files;
    }

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
