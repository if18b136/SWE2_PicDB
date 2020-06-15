package main;

import main.Database.DBConnection;
import main.ViewModels.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main extends Application{
    final Logger IOLogger = LogManager.getLogger("Input Output");

    @Override
    public void start(Stage primaryStage){
        try{
            Parent root = FXMLLoader.load(getClass().getResource("../ViewModels/Main.fxml"));
            primaryStage.setScene(new Scene(root, 1024,768));
            primaryStage.setTitle("PicBD");
            primaryStage.show();
        } catch(IOException ioe) {
            IOLogger.info(ioe.getMessage());
        }
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
