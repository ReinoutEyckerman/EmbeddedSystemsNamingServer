package com.bonkers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by reinout on 12/7/16.
 */
public class FileChecker {

    private final File folderLocation;

    public FileChecker(File folderLocation){
        this.folderLocation=folderLocation;
    }

    public List checkFiles(){
        List<String> files=new ArrayList<>();
        for (final File fileEntry : folderLocation.listFiles()) {
            if (!fileEntry.isDirectory()) {
                files.add(fileEntry.getName());
            }
        }
        return files;

    }

    public List checkFiles(List existingFiles){
        List<String> files=new ArrayList<>();
        for (final File fileEntry : folderLocation.listFiles()) {
            if (!fileEntry.isDirectory() && !existingFiles.contains(fileEntry)) {
                files.add(fileEntry.getName());
            }
        }
        return files;

    }
}
