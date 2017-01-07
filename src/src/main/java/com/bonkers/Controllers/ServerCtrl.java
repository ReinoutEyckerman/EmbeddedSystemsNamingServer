package com.bonkers.Controllers;

import com.bonkers.Client;
import com.bonkers.HashTableCreator;
import com.bonkers.Server;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Set;

public class ServerCtrl implements Initializable
{
    private static ObservableList<String> items = FXCollections.observableArrayList();
    @FXML
    private ListView nodeList;
    @FXML
    private ListView errorList;
    @FXML
    private Button shutdownBtn;
    @FXML
    private Button RestartBtn;

    public static ObservableList<String> Clients = FXCollections.observableArrayList();{
    Clients.addListener(new ListChangeListener<String>() {
        @Override
        public void onChanged(Change<? extends String> c) {
            if (Clients.size()>0){
               nodeList.setItems(Clients);
            }
        }
    });
}

    public static void PrintErrors()
    {
        try (BufferedReader br = new BufferedReader(new FileReader("Logging.txt")))
        {
            String line;
            while ((line = br.readLine()) != null)
            {
                items.add(line);
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL fxmlFileLocation, ResourceBundle resources)
    {
        assert errorList != null : "fx:id=\"errorList\" was not injected: check your FXML file 'simple.fxml'.";
        assert nodeList != null : "fx:id=\"deleteBtn\" was not injected: check your FXML file 'simple.fxml'.";
        assert shutdownBtn != null : "fx:id=\"deleteLocalBtn\" was not injected: check your FXML file 'simple.fxml'.";
        assert RestartBtn != null : "fx:id=\"RestartBtn\" was not injected: check your FXML file 'simple.fxml'.";


        errorList.setItems(items);
        //nodeList.setItems(items);
    }

    @FXML
    public void CloseApp(ActionEvent actionEvent)
    {
        StartPageCtrl.Shutdown();
        System.exit(0);
    }


    public void RestartApp(ActionEvent actionEvent) throws Exception
    {
        test();
       /* StartPageCtrl.Shutdown();
        Stage stage;
        Parent root;
        //get reference to the button's stage
        stage = (Stage) RestartBtn.getScene().getWindow();
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
        ServerCtrl.PrintErrors();*/
    }
    private void test()
    {


        System.out.println(Server.hashTableCreator.htIp.size());
        Object key = Server.hashTableCreator.htIp.keySet().toArray()[0];
        System.out.println(Server.hashTableCreator.htIp.get(key));

    }
}
