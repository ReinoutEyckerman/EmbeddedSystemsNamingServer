package com.bonkers;

import java.io.Serializable;

/**
 * Implementation of Node information.
 */
public class NodeInfo implements Serializable
{
    /**
     * Node Hash
     */
    public int hash;
    /**
     * Node IP address
     */
    public String address;

    /**
     * Constructor of the Node
     *
     * @param hash    Hash value of the node
     * @param address IP address of the node
     */
    public NodeInfo(int hash, String address)
    {
        this.hash = hash;
        this.address = address;
    }

    @Override
    public String toString()
    {
        return address + " with hash " + hash;
    }
}
