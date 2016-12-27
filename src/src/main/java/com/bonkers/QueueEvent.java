package com.bonkers;

import java.util.*;


/**
 * Interface for classes that want to subscribe on the QueueEvent events.
 */
interface QueueListener
{
    /**
     * "Event" when there is an item added to the queue.
     */
    void queueFilled();
}

/**
 * Extension of PriorityQueue with events. Allows classes to subscribe on the event where an item gets added to the queue.
 *
 * @param <E> Type of element
 */
public class QueueEvent<E> extends PriorityQueue<E>
{
    /**
     * Actual queue with data.
     */
    public Queue<E> queue = new LinkedList<E>();
    // usual methods for adding/removing listeners
    //TODO DISABLE PUBLIC
    /**
     * List of listeners that subscribed to the add event
     */
    private List<QueueListener> listeners = new ArrayList<QueueListener>();

    /**
     * If an item gets added, the function throws an event.
     *
     * @param item item to add.
     * @return returns 1 when failed.
     */
    public boolean add(E item)
    {
        boolean out = queue.add(item);
        notifyQueueFilled();
        return out;
    }

    /**
     * Poll the queue
     * @return value of the queue
     */
    public E poll()
    {
        return queue.poll();
    }

    /**
     * Adds a Queue Listener.
     *
     * @param listener Class that subscribes
     */
    public void addListener(QueueListener listener)
    {
        listeners.add(listener);
    }

    /**
     * "Event" That notifies all subscribers.
     */
    private void notifyQueueFilled()
    {
        listeners.forEach(QueueListener::queueFilled);
    }
}
