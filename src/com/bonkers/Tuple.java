package com.bonkers;

/**
 * Implementation of a double Tuple.
 * @param <X> X element of the tuple
 * @param <Y> Y element of the tuple
 */
public class Tuple<X,Y> {
    /**
     * X value of the tuple
     */
    public final X x;
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
