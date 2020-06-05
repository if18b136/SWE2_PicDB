package ViewModels;

import Database.DBConnection;
import PresentationModels.PictureModel;
import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.*;

import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
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
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

public class MainController extends AbstractController {
    final Logger IOLogger = LogManager.getLogger("Input Output");
    FileChooser fc = new FileChooser();

    @FXML
    ImageView picView;
    @FXML
    Pane picViewPane;
    @FXML
    ListView<ImageView> picPreview;

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
        String namePath = "D:\\#FH_Technikum\\BIF4D1\\SWE2\\PicDB\\Pictures\\" + selectedFile.getName();
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

            // 22.05.20 added DB entry to insert picture data
            DBConnection jdbc = DBConnection.getInstance();
            jdbc.uploadPic(picName,/*date,*/expTime,maker,model);

            // now we need to create a instance of the pictureModel class and use it's data to display the picture
            PictureModel newPic = new PictureModel();
            newPic.setName(picName);
            newPic.setID();
            FileInputStream fis = new FileInputStream(namePath);
            Image pic = new Image(fis);
            picView.setImage(pic);
            setPicBindings();
            // TODO Changing AnchorPane to Pane fixed resizing problem - currently onlu resizes with width but can be changed by uncommenting the second code line in setBinding
            addPreview(pic);    // changed to external function so init preview can call it too.

        } catch(IOException | ImageProcessingException | SQLException ioe) {
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

        // TODO currently only shows the latest loaded image
//        picPreview.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
//            System.out.println("Tile pressed ");
//            picView.setImage(pic);
//            setPicBindings();
//            event.consume();
//        });
    }

    // File needs String, so I changed Path input to String input
    @FXML
    public void initPreview(String picFolderPath) throws FileNotFoundException {
        File folder = new File(picFolderPath);
        String[] pictures = folder.list();
        if(pictures != null) {
            for(String pic : pictures) {
                FileInputStream fis = new FileInputStream((picFolderPath + pic));
                Image img = new Image(fis);
                addPreview(img);
            }
        } else {
            IOLogger.info("Picture directory empty at the time of preview initialization.");
        }
        picPreview.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends ImageView> ov,ImageView old_pic, ImageView new_pic) -> {
            picView.setImage(picPreview.getSelectionModel().getSelectedItem().getImage());
            // so for some reason, it is a legal assignment to just do picView = selected imageview from listview.
            // BUT the assignment just gets ignored - so instead you have to set the picView image to the selected items image, as you can see in the code line above.
            //there is absolutely no documentation about this anywhere.
            setPicBindings(); // don't forget to call the resizing again
        });
    }

    @FXML
    public void viewPic(MouseEvent event) {

    }
}