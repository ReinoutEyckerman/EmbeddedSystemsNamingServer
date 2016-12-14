package com.bonkers.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;
/**
 * Created by Kenny on 14/12/16.
 */
public class StartPageCtrl implements Initializable {
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

    }
    public void checkRole(){
        System.out.println(roleTxt.getText());
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
}
