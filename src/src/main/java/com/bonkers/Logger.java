package com.bonkers;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.nio.Buffer;

/**
 * Todo Kenny multithread support?(maybe set a buffer one can write to (QueueEvent)) and javadoc
 */
public class Logger{
    public BufferedWriter bw = null;
    public Logger()
    {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter("log.txt"));
        }
        catch(Exception e)
        {

        }
    }
    public void log(String content){
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter("log.txt",true));
            bw.write(content);
            bw.flush();
            bw.close();
        }
        catch(Exception e)
        {

        }
    }
}
