package com.bonkers;

import java.io.Serializable;
import java.net.InetAddress;

/**
 * Implementation of Node information.
 */
public class NodeInfo implements Serializable{
    /**
     * Node Hash
     */
    public int Hash ;
    /**
     *  Node IP address
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
