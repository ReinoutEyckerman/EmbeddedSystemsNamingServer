package com.bonkers;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Startup class
 */
public class Main {
    /**
     * Main function.
     * Accepts client or server mode from args
     * @param args startup arguments.
     * @throws Exception Throws exception on fail
     */
    public static void main(String[] args) throws Exception {
        System.out.print(InetAddress.getLocalHost().toString());
        if(args.length>0) {
            switch (args[0]) {
                case "server":
                    System.out.println("Starting Server");
                    File f = new File("hashtable.json");
                    if (f.exists() && !f.isDirectory())
                    {
                        f.delete();
                    }
                    Server server = new Server();
                /* try{
                    //server.run();
                } catch (IOException e){
                    System.out.println("Server IOException caught. Exiting...");
                    System.exit(1);
                }*/

                    break;
                case "client":
                    if(args.length>1)
                    {
                            Client client = new Client(args[1]);

                    }
                    else
                    {
                        System.out.println("No Name given");
                    }
                    break;
                default:
                    System.out.println("Unknown parameter: " + args[0]);
                    System.out.println("Exiting...");
            }
        }
        else
        {
            System.out.println("Please enter a parameter");
            System.out.println("Exiting...");
        }

    }

    /**
     * Checks if entered IP is an actual legit IP.
     * @param text Ip as string
     * @return Boolean that returns true or false depending of the string is an IP.
     * TODO: Replace this by an InetAddress function?
     */
    public static boolean IsIP(String text) {
        Pattern p = Pattern.compile("^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$");
        Matcher m = p.matcher(text);
        return m.find();
    }

}
