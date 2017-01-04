package com.bonkers.Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ClientCtrl implements Initializable
{
    @FXML
    private ListView fileList;
    @FXML
    private static ListView logsList;

    private static ObservableList<String> oLogs = FXCollections.observableArrayList();
    private static ObservableList<String> sFiles = FXCollections.observableArrayList();
    private int count=0;
    public static ObservableList<File> oFiles = FXCollections.observableArrayList();{
    oFiles.addListener(new ListChangeListener<File>() {
        @Override
        public void onChanged(Change<? extends File> c) {
            if (oFiles.size()>0){
                if (oFiles.size() != count){
                    sFiles.clear();
                    for (int i=0;i<oFiles.size();i++){
                        sFiles.add(oFiles.get(i).toString());
                    }
                    setData();
                    count = oFiles.size();
                }

            }


        }
    });
}
    @FXML
    private Button openBtn;
    @FXML
    private Button deleteBtn;
    @FXML
    private Button deleteLocalBtn;

    public void setData(){
        fileList.setItems(sFiles);
    }

    public static void setLogs() throws IOException
    {
        //oLogs.add(logRecord.getMessage());

    }

    @Override
    public void initialize(URL fxmlFileLocation, ResourceBundle resources)
    {
        assert openBtn != null : "fx:id=\"openBtn\" was not injected: check your FXML file 'simple.fxml'.";
        assert deleteBtn != null : "fx:id=\"deleteBtn\" was not injected: check your FXML file 'simple.fxml'.";
        assert deleteLocalBtn != null : "fx:id=\"deleteLocalBtn\" was not injected: check your FXML file 'simple.fxml'.";
        assert fileList != null : "fx:id=\"fileList\" was not injected: check your FXML file 'simple.fxml'.";
        assert logsList != null : "fx:id=\"logsList\" was not injected: check your FXML file 'simple.fxml'.";


        //setData(Client.globalFileList);

    }
/*
    @Override
    public void onChanged(Change c) {
        if(oLogs.size() > 0)
            //logsList.setItems(oLogs);
        if(oFiles.size() >0){}
            //fileList.setItems(oFiles);
    }
*/
}
