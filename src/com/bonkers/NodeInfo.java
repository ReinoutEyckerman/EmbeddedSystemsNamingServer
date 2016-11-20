package com.bonkers;

import java.net.InetAddress;

/**
 * Implementation of Node information.
 */
public class NodeInfo {
    /**
     * X value of the tuple
     */
    public int Hash ;
    /**
     * Y value of the tuple
     */
    public String Address;

    /**
     * Constructor of the Node
     * @param hash Hash value of the node
     * @param address IP address of the node
     */
    public NodeInfo(int hash, String address){
        this.Hash=hash;
        this.Address=address;
    }
}
