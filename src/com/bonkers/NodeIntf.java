package com.bonkers;

import java.rmi.Remote;

/**
 * Created by reinout on 11/8/16.
 */
public interface NodeIntf extends Remote {
    void UpdateNextNeighbor(int nodeNumber);
}
