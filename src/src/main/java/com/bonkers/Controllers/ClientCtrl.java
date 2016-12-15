package com.bonkers.Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by Jente on 4/12/2016.
 */
public class ClientCtrl implements Initializable {
    @FXML
    private Button openBtn;
    @FXML
    private Button deleteBtn;
    @FXML
    private Button deleteLocalBtn;
    @FXML
    private static ListView fileList;


    @Override
    public void initialize(URL fxmlFileLocation, ResourceBundle resources){
        assert openBtn != null : "fx:id=\"openBtn\" was not injected: check your FXML file 'simple.fxml'.";
        assert deleteBtn != null : "fx:id=\"deleteBtn\" was not injected: check your FXML file 'simple.fxml'.";
        assert deleteLocalBtn != null : "fx:id=\"deleteLocalBtn\" was not injected: check your FXML file 'simple.fxml'.";
        assert fileList != null : "fx:id=\"fileList\" was not injected: check your FXML file 'simple.fxml'.";
    }

    public static void setData(List<File> files)
    {
        ObservableList<String> oFiles = FXCollections.observableArrayList();
        files.forEach((file) -> {
            oFiles.add(file.getName());
        });
        fileList.setItems(oFiles);
    }

}
