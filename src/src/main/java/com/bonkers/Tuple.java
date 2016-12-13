package com.bonkers;

/**
 * Implementation of a 2-value Tuple.
 */
public class Tuple<X,Y> {
    /**
     * X value of the tuple
     */
    public final X x ;
    /**
     * Y value of the tuple
     */
    public final Y y;

    /**
     * Constructor that generates the tuple
     * @param x X value of the tuple
     * @param y Y value of the tuple
     */
    public Tuple(X x, Y y){
        this.x=x;
        this.y=y;
    }
}
