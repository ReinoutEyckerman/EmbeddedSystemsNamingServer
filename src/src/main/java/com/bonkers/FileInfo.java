package com.bonkers;

import java.io.Serializable;
import java.util.List;

/**
 * Information object about a file, only the fileowner has this object.
 */
public class FileInfo implements Serializable{
    /**
     * The filename related
     */
    public String fileName;
    /**
     * List of nodes who also have this file
     */
    public List<NodeInfo> fileOwners;
    @Override
    public String toString(){
        return fileName+" with following nodes "+fileOwners;
    }
}
