package com.bonkers;

import java.util.List;

/**
 * Information object about a file, only the fileowner has this object.
 */
public class FileInfo {
    /**
     * The filename related
     */
    public String fileName;
    /**
     * List of nodes who also have this file
     */
    public List<NodeInfo> fileOwners;
}
