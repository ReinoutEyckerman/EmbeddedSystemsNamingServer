package com.bonkers;

import com.bonkers.Controllers.StartPageCtrl;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.InetAddress;
import java.util.logging.Logger;

/**
 * Startup class
 */
public class Main extends Application
{
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    /**
     * Main function.
     * Accepts client or server mode from args
     *
     * @param args startup arguments.
     * @throws Exception Throws exception on fail
     */
    public static void main(String[] args) throws Exception
    {
        startLog();
        LOGGER.info("Ip: " + InetAddress.getLocalHost().getHostAddress());
        launch(args);

    }

    /**
     * Starts the logger
     */
    private static void startLog()
    {
        try
        {
            Logging.setup();
        } catch (IOException e)
        {
            e.printStackTrace();
            throw new RuntimeException("Problems with creating the log files");
        }
    }

    /**
     * Function to start JavaFX UI
     *
     * @param primaryStage Canvas to write UI on
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception
    {
        Parent root = FXMLLoader.load(getClass().getResource("/View/StartPage.fxml"));
        primaryStage.setTitle("SystemY");
        primaryStage.setScene(new Scene(root, 300, 200));
        primaryStage.show();

        primaryStage.setOnCloseRequest(event -> closeProgram());
    }

    @SuppressWarnings("restriction")
    private void closeProgram()
    {
        LOGGER.info("Closed GUI");
        Platform.exit();
        StartPageCtrl.Shutdown();
        //StartPageCtrl.client.shutdown();
        //client.shutdown();
        //Todo shutdown
    }

}
