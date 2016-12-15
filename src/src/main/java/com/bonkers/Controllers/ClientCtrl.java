package com.bonkers.Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.LogRecord;

/**
 * Created by Jente on 4/12/2016.
 */
public class ClientCtrl implements Initializable, ListChangeListener {
    @FXML
    private Button openBtn;
    @FXML
    private Button deleteBtn;
    @FXML
    private Button deleteLocalBtn;
    @FXML
    private static ListView fileList;
    @FXML
    private ListView logsList;

    private static ObservableList<String> oLogs = FXCollections.observableArrayList();
    private static ObservableList<String> oFiles = FXCollections.observableArrayList();


    @Override
    public void initialize(URL fxmlFileLocation, ResourceBundle resources){
        assert openBtn != null : "fx:id=\"openBtn\" was not injected: check your FXML file 'simple.fxml'.";
        assert deleteBtn != null : "fx:id=\"deleteBtn\" was not injected: check your FXML file 'simple.fxml'.";
        assert deleteLocalBtn != null : "fx:id=\"deleteLocalBtn\" was not injected: check your FXML file 'simple.fxml'.";
        assert fileList != null : "fx:id=\"fileList\" was not injected: check your FXML file 'simple.fxml'.";
        assert logsList != null : "fx:id=\"logsList\" was not injected: check your FXML file 'simple.fxml'.";

        oLogs.addListener(this);
        oFiles.addListener(this);
    }

    public static void setData(List<File> files)
    {
        files.forEach((file) -> {
            oFiles.add(file.getName());
        });
        fileList.setItems(oFiles);
    }

    public static void setLogs(LogRecord logRecord)
    {
        oLogs.add(logRecord.getMessage());
    }

    @Override
    public void onChanged(Change c) {
        logsList.setItems(oLogs);
        fileList.setItems(oFiles);
    }
}
