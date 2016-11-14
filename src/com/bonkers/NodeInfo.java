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
     * Constructor that generates the tuple
     * @param x X value of the tuple
     * @param y Y value of the tuple
     */
    public NodeInfo(int hash, String address){
        this.Hash=hash;
        this.Address=address;
    }
}
