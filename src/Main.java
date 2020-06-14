import Database.DBConnection;
import ViewModels.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main extends Application{
    final Logger IOLogger = LogManager.getLogger("Input Output");

    @Override
    public void start(Stage primaryStage){

        try{
            System.out.println("IPTC change with button in main controller");

            //DBConnection jdbc = DBConnection.getInstance();
            //Connection con = jdbc.getConnection();
            System.out.println("Test1");
            // TODO get the preview initialisation done properly instead of setting it up like this
            // needed for resize binding
//            FXMLLoader fl = new FXMLLoader();
//            fl.setLocation(getClass().getResource("ViewModels/Main.fxml"));
//            fl.load();
//            MainController mainController = fl.getController();
//            System.out.println("Test2");
//            // TODO change to get path from log file
//            mainController.initPreview();  // File needs String, so I changed Path input to String input

            // need the already filled main controller object instead of a new one
//            VBox root = fl.getRoot();
            Parent root = FXMLLoader.load(getClass().getResource("ViewModels/Main.fxml"));
            System.out.println("Test3");
            primaryStage.setScene(new Scene(root, 1024,768));
            primaryStage.setTitle("PicBD");
            primaryStage.show();
        } catch(IOException ioe) {
            IOLogger.info(ioe); // default catches only from error
        }
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
