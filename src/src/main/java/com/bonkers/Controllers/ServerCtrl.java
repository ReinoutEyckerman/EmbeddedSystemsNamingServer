package com.bonkers.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;

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


    @Override
    public void initialize(URL fxmlFileLocation, ResourceBundle resources){
        assert nodeList != null : "fx:id=\"openBtn\" was not injected: check your FXML file 'simple.fxml'.";
        assert nodeList != null : "fx:id=\"deleteBtn\" was not injected: check your FXML file 'simple.fxml'.";
        assert shutdownBtn != null : "fx:id=\"deleteLocalBtn\" was not injected: check your FXML file 'simple.fxml'.";
    }

}
