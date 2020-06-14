package ViewModels;

import Database.DBConnection;
import Models.Picture;
import PresentationModels.EXIF_PM;
import PresentationModels.IPTC_PM;
import PresentationModels.PictureList_PM;
import PresentationModels.Picture_PM;
import Service.Binding;
import Service.BusinessLayer;
import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.*;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

public class MainController extends AbstractController {
    final Logger mcLogger = LogManager.getLogger("Main Controller");
    FileChooser fc = new FileChooser();
    Picture_PM picturePM = new Picture_PM(new Picture());
    PictureList_PM pictureListPM = new PictureList_PM();

    @FXML
    ImageView picView;
    @FXML
    Pane picViewPane;
    @FXML
    ListView<ImageView> picPreview;
    @FXML
    ChoiceBox<String> exifChoiceBox;
    @FXML
    TextField exifValue,photographer,copyright;
    @FXML
    TextArea exifDescription,tags;
    @FXML
    Button save;
    @FXML
    Pane iptcPane;


    // init the objects from business layer
    public MainController() throws Exception {
        // gets initialized before already
        BusinessLayer bl = BusinessLayer.getInstance();
        System.out.println("1");
        bl.initPicNameList();
        System.out.println("2");
        bl.createPicList();
        System.out.println("3");
    }

    @Override
    public void initialize(URL url, ResourceBundle resources) {
        super.initialize(url, resources);

        setPicBindings();
        initPreview();

        //Binding.applyBinding(iptcPane, picturePM.getIptc()); // Does not bind
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
        //preview.setFitWidth(100); // solves scroll problem but makes list not well-resizeable
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
    //TODO init list in business layer, so MC does not need path
    public void initPreview() {
        try{
            //pictureListPM.refreshPictureList(); //TODO is this even needed when I already init the list with new PicListPM()?
            if(!pictureListPM.getPictureList().isEmpty()) {
                BusinessLayer bl = BusinessLayer.getInstance();
                for (Picture_PM picPM : pictureListPM.getPictureList()) {
                    FileInputStream fis = new FileInputStream((bl.getPath() + picPM.getName()));
                    Image img = new Image(fis);
                    addPreview(img);
                }
            } else {
                mcLogger.info("Picture directory empty at the time of preview initialization.");
            }
            picPreview.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends ImageView> ov,ImageView old_pic, ImageView new_pic) -> {
                picView.setImage(picPreview.getSelectionModel().getSelectedItem().getImage());
                    setPicBindings(); // don't forget to call the resizing again
                    // Set picture presentation model to current picture, change index and name in list presentation model
                    pictureListPM.setCurrentPic(picPreview.getItems().indexOf(picPreview.getSelectionModel().getSelectedItem()));
                    picturePM = pictureListPM.getCurPicView();
                    //System.out.println(picPreview.getSelectionModel().getSelectedIndex());    // current index of selected item in listView
                    //System.out.println(picturePM.getName());  //to check if picturePM is getting refreshed correctly
                    refreshExifChoiceBox(picturePM.getExifList());  //TODO add value and description
                    refreshIptcChoiceBox(picturePM.getIptc());
            });
        } catch (FileNotFoundException fnf) {
            mcLogger.error(fnf);
        }
    }

    @FXML
    public void refreshExifChoiceBox(List<EXIF_PM> exif_pmList) {
        ObservableList<String> exifsStrings = FXCollections.observableArrayList();
        for(EXIF_PM exifPm : exif_pmList) {
            exifsStrings.add(exifPm.getName());
        }
        exifChoiceBox.setItems(exifsStrings);
        // The default value needs to be set - I did not find a smarter way than simply using the eventHandle for the initial value - but it works fine!
        //TODO clean up double use of text refresh
        exifChoiceBox.setValue(exifsStrings.get(0));
        exifValue.setText(picturePM.getExifByIndex(0).getName());
        exifDescription.setText(picturePM.getExifByIndex(0).getDescription());

        exifChoiceBox.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends String> os,String old_str, String new_str) -> {
            exifValue.setText(picturePM.getExifByIndex(exifChoiceBox.getSelectionModel().getSelectedIndex()).getName());
            exifDescription.setText(picturePM.getExifByIndex(exifChoiceBox.getSelectionModel().getSelectedIndex()).getDescription());
        });
    }


    @FXML
    public void refreshIptcChoiceBox(IPTC_PM iptcPm) {
        HashMap<String,String> iptcList = iptcPm.getValues();
        photographer.setText(iptcList.get("Photographer"));
        copyright.setText(iptcList.get("Copyright"));
        tags.setText(iptcList.get("Tags"));
    }

    @FXML
    public void saveIptcChanges() {
        // changes also need to be saved in picturePreviewList or loaded into the database so it can be grabbed again
        BusinessLayer bl = BusinessLayer.getInstance();
        bl.updateIptc(photographer.getText(),copyright.getText(),tags.getText(),picturePM);
        //is page refreshing even needed? - yes because the current Picture presentation model still has the old info
        pictureListPM.refreshPictureList(); //this should be called from Business Layer but does it make sense to call BL so that it then calls the pictureList function?
    }

}