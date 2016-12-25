package com.bonkers.Controllers;

import com.bonkers.Client;
import com.bonkers.Server;
import javafx.event.ActionEvent;
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

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class StartPageCtrl implements Initializable, Runnable
{
    private static Client client;
    private static Server server;
    private File file = new File(System.getProperty("user.dir") + "/tmp");
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

    private static void infoBox(String titleBar, String headerMessage, String infoMessage)
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titleBar);
        alert.setHeaderText(headerMessage);
        alert.setContentText(infoMessage);
        alert.showAndWait();
    }

    public static void Shutdown()
    {
        if (client != null)
        {
            client.shutdown();
        }
        else if (server != null)
        {
            server.shutdown();
        }
        else
        {
            System.exit(0);
        }
    }

    @Override
    public void initialize(URL fxmlFileLocation, ResourceBundle resources)
    {
        assert submitBtn != null : "fx:id=\"submitBtn\" was not injected: check your FXML file 'simple.fxml'.";
        assert nameTxt != null : "fx:id=\"nameTxt\" was not injected: check your FXML file 'simple.fxml'.";
        assert roleTxt != null : "fx:id=\"roleTxt\" was not injected: check your FXML file 'simple.fxml'.";
        assert roleLbl != null : "fx:id=\"roleLbl\" was not injected: check your FXML file 'simple.fxml'.";
        assert nameLbl != null : "fx:id=\"nameLbl\" was not injected: check your FXML file 'simple.fxml'.";

        roleTxt.textProperty().addListener((observable, oldValue, newValue) ->
        {
            checkRole();
        });

        file.mkdirs();

        submitBtn.defaultButtonProperty().bind(submitBtn.focusedProperty());


    }

    @Override
    public void run()
    {

    }

    private void checkRole()
    {
        if (roleTxt.getText().toLowerCase().equals("client"))
        {
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
    private void ClickedSubmitBtn(ActionEvent event) throws Exception
    {
        Stage stage;
        Parent root;
        if (roleTxt.getText().toLowerCase().equals("client"))
        {
            if (nameTxt.getText().toLowerCase().isEmpty())
            {
                infoBox("Empty name", "No name given", "Please give us a valid name (not empty)");
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
                new Thread(() ->
                {
                    try
                    {
                        client = new Client(nameTxt.getText(), file);

                    } catch (Exception e)
                    {
                        System.out.println("Cannot start client");
                        e.printStackTrace();
                    }
                }).start();

            }
        }
        else if (roleTxt.getText().toLowerCase().equals("server"))
        {
            //get reference to the button's stage
            stage = (Stage) submitBtn.getScene().getWindow();
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
            server = new Server();
            ServerCtrl.PrintErrors();
        }
        else
        {
            infoBox("Wrong role", "You appeared to have given the wrong role", "Please give us a valid System Y role (Server or Client");
        }
    }

    @FXML
    public void onEnter(ActionEvent actionEvent) throws Exception
    {
        ClickedSubmitBtn(actionEvent);
    }
}
