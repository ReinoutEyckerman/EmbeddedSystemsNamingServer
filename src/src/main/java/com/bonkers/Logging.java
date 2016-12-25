package com.bonkers;

import java.io.IOException;
import java.util.logging.*;

/**
 * Todo Kenny multithread support?(maybe set a buffer one can write to (QueueEvent)) and javadoc
 */
public class Logging
{


    static public void setup() throws IOException
    {

        // get the global logger to configure it
        Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);


        logger.setLevel(Level.INFO);
        FileHandler fileTxt = new FileHandler("Logging.txt");

        // create a TXT formatter
        SimpleFormatter formatterTxt = new SimpleFormatter();
        fileTxt.setFormatter(formatterTxt);
        logger.addHandler(fileTxt);
    }

    public static Handler listHandler(QueueEvent<LogRecord> logRecordsQueue)
    {
        return new Handler()
        {
            StreamHandler sh = new StreamHandler();

            @Override
            public void publish(LogRecord record)
            {
                logRecordsQueue.add(record);
            }

            @Override
            public void flush()
            {
                sh.flush();
            }

            @Override
            public void close() throws SecurityException
            {
                flush();
            }
        };
    }
}
