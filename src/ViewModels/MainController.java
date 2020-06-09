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
    public void addPic() throws IOException {
        File selectedFile = fc.showOpenDialog(this.getStage());
        Path src = Paths.get(selectedFile.getAbsolutePath());
        //TODO paths into config file
        String namePath = "D:\\FH_Technikum\\BIF4D1\\SWE2\\PicDB\\Pictures\\" + selectedFile.getName();
        String picName = selectedFile.getName();
        Path dest = Paths.get(namePath);
        try{
            Files.copy(src,dest);
            Metadata metadata = ImageMetadataReader.readMetadata(selectedFile);
            // print out all metadata
//            for (Directory directory : metadata.getDirectories()) {
//                for (Tag tag : directory.getTags()) {
//                    System.out.println(tag);
//                }
//            }

            // obtain the Exif directory
            ExifSubIFDDirectory subIFDDirectory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
            ExifIFD0Directory IFD0Directory = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
            ExifImageDirectory ImageDirectory = metadata.getFirstDirectoryOfType(ExifImageDirectory.class);

            // create a descriptor
            ExifSubIFDDescriptor subIFDDescriptor = new ExifSubIFDDescriptor(subIFDDirectory);
            ExifIFD0Descriptor IFD0Descriptor = new ExifIFD0Descriptor(IFD0Directory);
            ExifImageDescriptor ImageDescriptor = new ExifImageDescriptor(ImageDirectory);

            // Date only works if there is a date within the EXIF data, otherwise we will get a NullPointerException
            // TODO convert date from JAVA.util to JAVA.sql
            //Date date = subIFDDirectory.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);
            String expTime = subIFDDescriptor.getExposureTimeDescription();
            String maker = IFD0Descriptor.getDescription(271);
            String model = IFD0Descriptor.getDescription(272);

            //System.out.println(date);
            System.out.println(expTime);
            System.out.println(maker);
            System.out.println(model);

            // set picture presentation model to added picture
            BusinessLayer bl = BusinessLayer.getInstance();
            picturePM = bl.addNewPicture(picName,/*date,*/expTime,maker,model);

            FileInputStream fis = new FileInputStream(namePath);
            Image pic = new Image(fis);
            picView.setImage(pic);
            setPicBindings();
            addPreview(pic);    // changed to external function so init preview can call it too.

            //changed to overall exception catch because of custom DAL exception throw
        } catch(Exception/* | SQLException */ioe) { //TODO - change Exception to custom created DALException
            IOLogger.error(ioe);
        }
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

            // Set picture presentation model to current picture
            // change index and name in list presentation model


        });
    }
}