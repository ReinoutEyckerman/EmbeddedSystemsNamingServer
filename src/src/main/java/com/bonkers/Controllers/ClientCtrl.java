package com.bonkers.Controllers;

import com.bonkers.AgentFileList;

import com.bonkers.FileManager;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.ResourceBundle;

public class ClientCtrl implements Initializable
{
    @FXML
    private ListView fileList;
    @FXML
    private Button openBtn;
    @FXML
    private Button deleteBtn;
    @FXML
    private Button deleteLocalBtn;
    @FXML
    private Button logoutBtn;

    AgentFileList agent = new AgentFileList();

    public static ObservableList<String> LocalFiles = FXCollections.observableArrayList();
    private static ObservableList<String> oLogs = FXCollections.observableArrayList();
    private static ObservableList<String> sFiles = FXCollections.observableArrayList();
    private int count=0;
    private String PathToLocalFile = null;
    private Object SelectedFile = null;
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
                    fileList.setItems(sFiles);
                    count = oFiles.size();
                }

            }


        }
    });
}

    @Override
    public void initialize(URL fxmlFileLocation, ResourceBundle resources)
    {
        assert openBtn != null : "fx:id=\"openBtn\" was not injected: check your FXML file 'simple.fxml'.";
        assert deleteBtn != null : "fx:id=\"deleteBtn\" was not injected: check your FXML file 'simple.fxml'.";
        assert deleteLocalBtn != null : "fx:id=\"deleteLocalBtn\" was not injected: check your FXML file 'simple.fxml'.";
        assert fileList != null : "fx:id=\"fileList\" was not injected: check your FXML file 'simple.fxml'.";
        assert logoutBtn != null : "fx:id=\"logoutBtn\" was not injected: check your FXML file 'simple.fxml'.";
    }

    @FXML
    public void FileSelected(MouseEvent mouseEvent) throws Exception {
        SelectedFile = fileList.getSelectionModel().getSelectedItem();
        if (LocalFiles.size()>0) {
            for (int i = 0; i < LocalFiles.size(); i++) {
                if (SelectedFile.equals(LocalFiles.get(i))) {
                    deleteLocalBtn.setVisible(true);
                    for (int y = 0; y < oFiles.size(); y++) {
                        if (SelectedFile.toString().equals(oFiles.get(y).toString())) {
                            PathToLocalFile = oFiles.get(y).getPath();
                            break;
                        }
                        else
                            PathToLocalFile = null;
                    }
                }
                else
                    deleteLocalBtn.setVisible(false);
            }
        }
    }

    @FXML
    public void DeleteLocalFile(MouseEvent mouseEvent) {
        File currentDirFile = new File("");
        File f = new File(currentDirFile.getAbsolutePath()+File.separator+"tmp"+File.separator+PathToLocalFile);
        f.delete();
        //TODO ownership
    }
    @FXML
    public void Logout(MouseEvent mouseEvent) {
        Platform.exit();
        StartPageCtrl.Shutdown();
    }
}
