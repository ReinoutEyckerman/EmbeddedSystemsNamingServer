package com.bonkers.Controllers;

import com.bonkers.Logging;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
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

    static ObservableList<String> items =FXCollections.observableArrayList ();

    @Override
    public void initialize(URL fxmlFileLocation, ResourceBundle resources){
        assert errorList != null : "fx:id=\"errorList\" was not injected: check your FXML file 'simple.fxml'.";
        assert nodeList != null : "fx:id=\"deleteBtn\" was not injected: check your FXML file 'simple.fxml'.";
        assert shutdownBtn != null : "fx:id=\"deleteLocalBtn\" was not injected: check your FXML file 'simple.fxml'.";



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

}
