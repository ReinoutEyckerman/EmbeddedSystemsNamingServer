package com.bonkers;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    public static void main(String[] args) throws Exception {
        if(args.length>0) {
            switch (args[0]) {
                case "server":
                    System.out.println("Starting Server");
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
                        if (IsIP(args[1])) {
                            Client client = new Client("Benito");
                        /*try {
                            client.run();
                        } catch (IOException e) {
                            System.out.println("Client "+e+"ge IOException caught. Exiting...");
                            System.exit(1);
                        }*/
                        } else {
                            System.out.println("Argument is not a valid IPv4 address");
                        }
                    }
                    else
                    {
                        System.out.println("No IP address given");
                    }
                    break;
                case "rmiserver":
                    System.out.println("Starting RMIServer");
                    RMIServer RMIServer = new RMIServer();
                    RMIServer.call();
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

    public static boolean IsIP(String text) {
        Pattern p = Pattern.compile("^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$");
        Matcher m = p.matcher(text);
        return m.find();
    }

}
