import Database.DBConnection;
import ViewModels.MainController;
import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.ExifSubIFDDescriptor;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.appender.db.jdbc.ConnectionSource;

public class Main extends Application{

    final Logger dbConnectionLogger = LogManager.getLogger("DB Connection");
    final Logger IOLogger = LogManager.getLogger("Input Output");

    @Override
    public void start(Stage primaryStage){

        try{
            DBConnection jdbc = DBConnection.getInstance();
            Connection con = jdbc.getConnection();

            // TODO get the preview initialisation done properly instead of setting it up like this
            // needed for resize binding
            FXMLLoader fl = new FXMLLoader();
            fl.setLocation(getClass().getResource("ViewModels/Main.fxml"));
            fl.load();
            MainController mainController = fl.getController();
            // TODO change to get path from log file
            String picFolderPath = "D:\\#FH_Technikum\\BIF4D1\\SWE2\\PicDB\\Pictures\\";
            mainController.initPreview(picFolderPath);  // File needs String, so I changed Path input to String input

            // need the already filled main controller object instead of a new one
            //Parent root = FXMLLoader.load(getClass().getResource("ViewModels/Main.fxml"));
            VBox root = fl.getRoot();

            primaryStage.setScene(new Scene(root, 600,400));
            primaryStage.setTitle("PicBD");
            primaryStage.show();
        } catch(IOException | SQLException e) {
            dbConnectionLogger.info(e); // default catches only from error
        }
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
