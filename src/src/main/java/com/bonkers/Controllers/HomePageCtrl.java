package com.bonkers.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Jente on 4/12/2016.
 */
public class HomePageCtrl implements Initializable {
    @FXML
    private Button btnBuyTicket;
    @FXML
    private Button btnCheckIn;
    @FXML
    private Button btnDetails;


    @Override
    public void initialize(URL fxmlFileLocation, ResourceBundle resources){
        assert btnBuyTicket != null : "fx:id=\"BtnBuyTicket\" was not injected: check your FXML file 'simple.fxml'.";
        assert btnCheckIn != null : "fx:id=\"BtnCheckIn\" was not injected: check your FXML file 'simple.fxml'.";
        assert btnDetails != null : "fx:id=\"BtnDetails\" was not injected: check your FXML file 'simple.fxml'.";
    }

}
