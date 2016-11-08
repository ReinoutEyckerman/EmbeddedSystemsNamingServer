package com.bonkers;

import java.lang.reflect.Type;
import java.util.*;

/**
 * Created by reinout on 11/8/16.
 */
interface QueueListener{
  void packetReceived();
}
public class QueueEvent<E> extends PriorityQueue<E> {
  private List<QueueListener> listeners = new ArrayList<QueueListener>();
  // usual methods for adding/removing listeners
  private Queue<E> queue=new LinkedList<E>();
  public boolean add(E item){
    NotifyPacketReceived();
    return queue.add(item);
  }

  public void addListener(QueueListener listener) {
    listeners.add(listener);  // like that
  }

  public void NotifyPacketReceived(){
    listeners.forEach(QueueListener::packetReceived);
  }
}
