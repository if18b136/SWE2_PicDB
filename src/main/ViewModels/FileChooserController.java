package main.ViewModels;

import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class FileChooserController extends AbstractController {
    FileChooser fc = new FileChooser();
    Stage currStage = this.getStage();

    @Override
    public void initialize(URL url, ResourceBundle resources) {
        super.initialize(url,resources);

        File selectedFile = fc.showOpenDialog(currStage);
    }
}
