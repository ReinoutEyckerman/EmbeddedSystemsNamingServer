package com.bonkers.Controllers;

import com.bonkers.Client;
import com.bonkers.Logging;
import com.bonkers.Main;
import com.bonkers.Server;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Jente on 4/12/2016.
 */
public class ServerCtrl implements Initializable {
    @FXML
    private ListView nodeList;
    @FXML
    private ListView errorList;
    @FXML
    private Button shutdownBtn;
    @FXML
    private Button RestartBtn;

    static ObservableList<String> items =FXCollections.observableArrayList ();

    @Override
    public void initialize(URL fxmlFileLocation, ResourceBundle resources){
        assert errorList != null : "fx:id=\"errorList\" was not injected: check your FXML file 'simple.fxml'.";
        assert nodeList != null : "fx:id=\"deleteBtn\" was not injected: check your FXML file 'simple.fxml'.";
        assert shutdownBtn != null : "fx:id=\"deleteLocalBtn\" was not injected: check your FXML file 'simple.fxml'.";
        assert RestartBtn != null : "fx:id=\"RestartBtn\" was not injected: check your FXML file 'simple.fxml'.";



        errorList.setItems(items);
        //nodeList.setItems(items);
    }
    public static void PrintErrors() throws Exception {
        try (BufferedReader br = new BufferedReader(new FileReader("Logging.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                items.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void CloseApp(ActionEvent actionEvent){
        StartPageCtrl.Shutdown();
        System.exit(0);
    }


    public void RestartApp(ActionEvent actionEvent) throws Exception{
        StartPageCtrl.Shutdown();
        Stage stage;
        Parent root;
        //get reference to the button's stage
        stage=(Stage) RestartBtn.getScene().getWindow();
        //load up OTHER FXML document
        root = FXMLLoader.load(getClass().getResource("/View/Server.fxml"));

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();

        //Start new server
        System.out.println("Starting Server");
        File f = new File("hashtable.json");
        if (f.exists() && !f.isDirectory())
        {
            f.delete();
        }
        Server server = new Server();
        ServerCtrl.PrintErrors();
    }
}
