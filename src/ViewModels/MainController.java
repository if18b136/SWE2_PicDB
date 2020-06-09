package ViewModels;

import Database.DBConnection;
import Models.Picture;
import PresentationModels.PictureList_PM;
import PresentationModels.Picture_PM;
import Service.BusinessLayer;
import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.*;

import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MainController extends AbstractController {
    final Logger IOLogger = LogManager.getLogger("Input Output");
    FileChooser fc = new FileChooser();
    Picture_PM picturePM = new Picture_PM(new Picture());
    PictureList_PM pictureListPM = new PictureList_PM();

    @FXML
    ImageView picView;
    @FXML
    Pane picViewPane;
    @FXML
    ListView<ImageView> picPreview;

    // init the objects from business layer
    public MainController() throws Exception {
        BusinessLayer bl = BusinessLayer.getInstance();

        // gets initialized before already
        //bl.initPicNameList();
        //bl.createPicList();
    }

    public void setPicBindings() {
        picView.setPreserveRatio(true);
        picView.fitHeightProperty().bind(picViewPane.heightProperty());
        picView.fitWidthProperty().bind(picViewPane.widthProperty());
    }

    public void setPreviewBindings(ImageView preview) {
        preview.setPreserveRatio(true);
        preview.fitHeightProperty().bind(picPreview.heightProperty());
        preview.fitWidthProperty().bind(picPreview.widthProperty());
    }

    @FXML
    public void addPic() throws Exception {
        File selectedFile = fc.showOpenDialog(this.getStage());
        Path src = Paths.get(selectedFile.getAbsolutePath());
        //TODO paths into config file
        String namePath = "D:\\FH_Technikum\\BIF4D1\\SWE2\\PicDB\\Pictures\\" + selectedFile.getName();

        BusinessLayer bl = BusinessLayer.getInstance();
        picturePM = bl.extractMetadata(selectedFile, src, namePath);

        FileInputStream fis = new FileInputStream(namePath);
        Image pic = new Image(fis);
        picView.setImage(pic);
        setPicBindings();
        addPreview(pic);
    }

    // TODO pic in preview are bigger than list height
    @FXML
    public void addPreview(Image pic) {
        ImageView preview = new ImageView();
        preview.setImage(pic);
        setPreviewBindings(preview);
        picPreview.getItems().add(preview);
    }

    // File needs String, so I changed Path input to String input
    public void initPreview(String picFolderPath) throws FileNotFoundException {
        if(!pictureListPM.getPictureList().isEmpty()) {
            for (Picture_PM picPM : pictureListPM.getPictureList()) {
                FileInputStream fis = new FileInputStream((picFolderPath + picPM.getName()));
                Image img = new Image(fis);
                addPreview(img);
            }
        } else {
            IOLogger.info("Picture directory empty at the time of preview initialization.");
        }
        picPreview.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends ImageView> ov,ImageView old_pic, ImageView new_pic) -> {
            picView.setImage(picPreview.getSelectionModel().getSelectedItem().getImage());
            setPicBindings(); // don't forget to call the resizing again
            // Set picture presentation model to current picture, change index and name in list presentation model
            pictureListPM.setCurrentPic(picPreview.getItems().indexOf(picPreview.getSelectionModel().getSelectedItem()));
            picturePM = pictureListPM.getCurPicView();
        });
    }
}