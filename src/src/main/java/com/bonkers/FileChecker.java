package com.bonkers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
     *
     * @param folderLocation Location of the folder as a File object
     */
    public FileChecker(File folderLocation) {
        this.folderLocation = folderLocation;
    }

    /**
     * Check for files locally, should only be used if you want a complete Map, not if you want additions to an existing Map
     *
     * @return The Map of files
     */
    public List checkFiles() {
        List<String> files = new ArrayList<>();
        for (final File fileEntry : folderLocation.listFiles()) {
            if (!fileEntry.isDirectory()) {
                files.add(fileEntry.getName());
            }
        }
        return files;
    }

    /**
     * Check for files locally, Updates an already existing Map
     *
     * @return The Map of files
     */
    public List checkFiles(List existingFiles) {
        List<String> files = new ArrayList<>();
        for (final File fileEntry : folderLocation.listFiles()) {
            if (!fileEntry.isDirectory() && !existingFiles.contains(fileEntry.getName())) {
                files.add(fileEntry.getName());
            }
        }
        return files;

    }
}
