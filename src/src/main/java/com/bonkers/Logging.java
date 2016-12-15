package com.bonkers;

import java.io.IOException;
import java.util.List;
import java.util.logging.*;

/**
 * Todo Kenny multithread support?(maybe set a buffer one can write to (QueueEvent)) and javadoc
 */
public class Logging{
    static private FileHandler fileTxt;
    static private SimpleFormatter formatterTxt;

    static private FileHandler fileHTML;
    static private Formatter formatterHTML;

    static public void setup() throws IOException {

        // get the global logger to configure it
        Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);



        logger.setLevel(Level.INFO);
        fileTxt = new FileHandler("Logging.txt");

        // create a TXT formatter
        formatterTxt = new SimpleFormatter();
        fileTxt.setFormatter(formatterTxt);
        logger.addHandler(fileTxt);
    }

    public static Handler ListHandler(QueueEvent<LogRecord> logRecordsQueue)
    {
        Handler h = new Handler() {
            StreamHandler sh = new StreamHandler();
            @Override
            public void publish(LogRecord record) {
                logRecordsQueue.add(record);
            }

           @Override
            public void flush() {
                sh.flush();
            }

            @Override
            public void close() throws SecurityException {
                flush();
            }
        };
        return h;
    }
}
