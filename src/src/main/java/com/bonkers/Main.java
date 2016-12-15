package com.bonkers;

import com.bonkers.Controllers.StartPageCtrl;
import com.sun.javafx.application.PlatformImpl;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.bonkers.Logging;

/**
 * Startup class
 * Todo public check because ?
 */
public class Main extends Application {
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    /**
     * Function to start JavaFX UI
     * @param primaryStage Canvas to write UI on
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("/View/StartPage.fxml"));
        primaryStage.setTitle("SystemY");
        primaryStage.setScene(new Scene(root, 300, 200));
        primaryStage.show();

        primaryStage.setOnCloseRequest(event -> closeProgram());
    }

    @SuppressWarnings("restriction")
    private void closeProgram() {
        LOGGER.info("Closed GUI");
        StartPageCtrl.Shutdown();
        //StartPageCtrl.client.shutdown();
        //client.shutdown();
        //Todo shutdown
    }

    /**
     * Main function.
     * Accepts client or server mode from args
     * @param args startup arguments.
     * @throws Exception Throws exception on fail
     */
    public static void main(String[] args)  throws Exception  {
        StartLog();
        LOGGER.info("Client Ip: "+ InetAddress.getLocalHost().getHostAddress().toString());
        launch(args);

    }

    public static void StartLog(){
        try {
            Logging.setup();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Problems with creating the log files");
        }
    }
}
