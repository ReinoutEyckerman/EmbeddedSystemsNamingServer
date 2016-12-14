package com.bonkers.Controllers;

import com.bonkers.Client;
import com.bonkers.Logging;
import com.bonkers.Server;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
/**
 * Created by Kenny on 14/12/16.
 */
public class StartPageCtrl implements Initializable, Runnable {
    @FXML
    private Button submitBtn;
    @FXML
    private TextField nameTxt;
    @FXML
    private TextField roleTxt;
    @FXML
    private Label roleLbl;
    @FXML
    private Label nameLbl;

    public static Client client;

    File file = new File(System.getProperty("user.dir") + "/tmp");




    @Override
    public void initialize(URL fxmlFileLocation, ResourceBundle resources) {
        assert submitBtn != null : "fx:id=\"submitBtn\" was not injected: check your FXML file 'simple.fxml'.";
        assert nameTxt != null : "fx:id=\"nameTxt\" was not injected: check your FXML file 'simple.fxml'.";
        assert roleTxt != null : "fx:id=\"roleTxt\" was not injected: check your FXML file 'simple.fxml'.";
        assert roleLbl != null : "fx:id=\"roleLbl\" was not injected: check your FXML file 'simple.fxml'.";
        assert nameLbl != null : "fx:id=\"nameLbl\" was not injected: check your FXML file 'simple.fxml'.";

        roleTxt.textProperty().addListener((observable, oldValue, newValue) -> {
            checkRole();
        });

        file.mkdirs();



    }
    @FXML
    public void exitApplication(ActionEvent event) {
        System.out.println("Close from controller");
    }
    @Override
    public void run() {

    }
    public void checkRole(){
        if(roleTxt.getText().toLowerCase().equals("client")) {
            nameLbl.setVisible(true);
            nameTxt.setVisible(true);
        }
        else
        {
            nameLbl.setVisible(false);
            nameTxt.setVisible(false);
        }
    }
    @FXML
    private void ClickedSubmitBtn(ActionEvent event) throws IOException, Exception{
        Stage stage;
        Parent root;
        if(roleTxt.getText().toLowerCase().equals("client")) {
            if(nameTxt.getText().toLowerCase().isEmpty())
            {
                infoBox("Empty name","No name given", "Please give us a valid name (not empty)");
            }
            else
            {
                //get reference to the button's stage
                stage = (Stage) submitBtn.getScene().getWindow();
                //load up OTHER FXML document
                root = FXMLLoader.load(getClass().getResource("/View/Client.fxml"));

                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();


                //Start new client;
                new Thread(() -> {
                    try {
                        client = new Client(nameTxt.getText(), file);

                    }
                    catch (Exception e){
                        System.out.println("Cannot start client");
                        e.printStackTrace();
                    }
                }).start();

            }
        }
        else if (roleTxt.getText().toLowerCase().equals("server"))
        {
            //get reference to the button's stage
            stage=(Stage) submitBtn.getScene().getWindow();
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
        else
        {
            infoBox("Wrong role","You appeared to have given the wrong role","Please give us a valid System Y role (Server or Client");
        }
    }
    public static void infoBox(String titleBar,String headerMessage,String infoMessage)
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titleBar);
        alert.setHeaderText(headerMessage);
        alert.setContentText(infoMessage);
        alert.showAndWait();
    }
    public static void Shutdown(){
        client.shutdown();
    }
}
