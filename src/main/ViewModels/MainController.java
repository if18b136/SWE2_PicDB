package main.ViewModels;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import main.Database.DBConnection;
import main.Models.Photographer;
import main.Models.Picture;
import main.PresentationModels.*;
import main.Service.*;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import main.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

public class MainController extends AbstractController {
    final Logger mcLogger = LogManager.getLogger("Main Controller");
    FileChooser fc = new FileChooser();
    MainWindowPM main = new MainWindowPM();
    Search_PM searchModel;

    @FXML
    ImageView picView;
    @FXML
    Pane picViewPane;
    @FXML
    ListView<ImageView> picPreview;
    @FXML
    ChoiceBox<String> exifChoiceBox;
    @FXML
    TextField firstName,lastName,exifValue,copyright;
    @FXML
    TextArea exifDescription,tags;
    @FXML
    Button save,search,reset;
    @FXML
    Pane iptcPane;
    @FXML
    MenuItem generatePicReport, generateTagReport;
    @FXML
    TextField searchField;

    // init the objects from business layer
    public MainController() throws Exception {
        // gets initialized before already
//        BusinessLayer bl = BusinessLayer.getInstance();
//        bl.initPicNameList();
//        bl.createPicList();
    }

    @Override
    public void initialize(URL url, ResourceBundle resources) {
        super.initialize(url, resources);
        setPicBindings();
        initPreview();
        //Binding.applyBinding(iptcPane, picturePM.getIptc()); // Does not bind
        searchModel = new Search_PM();
        applyBindings();
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
    public void addNewPic() throws Exception {
        File selectedFile = fc.showOpenDialog(this.getStage());
        Path src = Paths.get(selectedFile.getAbsolutePath());
        BusinessLayer bl = BusinessLayer.getInstance();
        String namePath = bl.getPath() + selectedFile.getName();
        main.setCurrPicturePm(bl.extractMetadata(selectedFile, src));
        FileInputStream fis = new FileInputStream(namePath);
        Image pic = new Image(fis);
        picView.setImage(pic);
        setPicBindings();
        addPreview(pic);

        refreshIptc(main.getCurrPicturePm().getIptc(),main.getCurrPicturePm().getPhotographer());
        refreshExifChoiceBox(main.getCurrPicturePm().getExifList());
        picPreview.getSelectionModel().selectLast();    //automatically select the newly added picture in the preview to let it look like an instant selection from preview
    }

    @FXML
    public void showPhotographers() {
        Stage photographerStage = new Stage();
        try{
            Parent root = FXMLLoader.load(getClass().getResource("../../ViewModels/Photographers.fxml"));
            photographerStage.setScene(new Scene(root, 800,600));
            photographerStage.setTitle("Photographers");
            photographerStage.initModality(Modality.APPLICATION_MODAL);
            photographerStage.showAndWait();
            //TODO get these 3 lines an own function
            main.refresh();
            if(main.getCurrPicturePm().getID() != 0){  //is something clicked? if yes, it could contain the changed photographer name so we have to refresh
                refreshIptc(main.getCurrPicturePm().getIptc(),main.getCurrPicturePm().getPhotographer());
            }
        } catch(IOException ioe) {
            mcLogger.info(ioe.getMessage());
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
    public void initPreview() {
        try{
            if(!main.getPictureListPm().getPictureList().isEmpty()) {
                BusinessLayer bl = BusinessLayer.getInstance();
                for(Picture_PM picturePm : main.getPictureListPm().getPictureList()) {
                    FileInputStream fis = new FileInputStream(bl.getPath() + picturePm.getName());
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
                main.getPictureListPm().setCurrentPic(picPreview.getItems().indexOf(picPreview.getSelectionModel().getSelectedItem()));
                main.setCurrPicturePm(main.getPictureListPm().getCurPicView());
                refreshIptc(main.getCurrPicturePm().getIptc(),main.getCurrPicturePm().getPhotographer());
                refreshExifChoiceBox(main.getCurrPicturePm().getExifList());
                save.setDisable(false);
                generatePicReport.setDisable(false);
                generatePicReport.setOnAction(Event -> generatePicReport());
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
        exifChoiceBox.setValue(exifsStrings.get(0));
        exifValue.setText(main.getCurrPicturePm().getExifByIndex(0).getName());
        exifDescription.setText(main.getCurrPicturePm().getExifByIndex(0).getDescription());

        exifChoiceBox.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends String> os,String old_str, String new_str) -> {
            exifValue.setText(main.getCurrPicturePm().getExifByIndex(exifChoiceBox.getSelectionModel().getSelectedIndex()).getName());
            exifDescription.setText(main.getCurrPicturePm().getExifByIndex(exifChoiceBox.getSelectionModel().getSelectedIndex()).getDescription());
        });
    }

    @FXML
    public void refreshIptc(IPTC_PM iptcPm, Photographer photographer) {
        HashMap<String,String> iptcList = iptcPm.getValues();
        firstName.setText(photographer.getFirstName());
        lastName.setText(photographer.getLastName());
        copyright.setText(iptcList.get("Copyright"));
        tags.setText(iptcList.get("Tags"));
    }

    @FXML
    public void saveIptcChanges() {
        // changes also need to be saved in picturePreviewList or loaded into the database so it can be grabbed again
        BusinessLayer bl = BusinessLayer.getInstance();
        bl.updateIptc(copyright.getText(),tags.getText(),main.getCurrPicturePm());
        boolean valid = bl.updateAssignment(firstName.getText(),lastName.getText(),main.getCurrPicturePm());
        if(!valid) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Business Layer Warning");
            alert.setContentText("Picture Assignment failed. Either wrong info or non-existing Photographer.");
            alert.showAndWait();
        }
        //is page refreshing even needed? - yes because the current Picture presentation model still has the old info
        main.refresh();
        refreshIptc(main.getCurrPicturePm().getIptc(),main.getCurrPicturePm().getPhotographer());
    }

    @FXML
    public void generatePicReport() {
        try {
            String name = main.getCurrPicturePm().getName().split("\\.")[0] + ".pdf";
            PictureReport rpt = new PictureReport(name, main.getCurrPicturePm());
            rpt.create();
            rpt.show();
        } catch (IOException ioe) {
            mcLogger.error(ioe.getMessage());
        }
    }

    @FXML
    public void generateTagReport() {
        try {
            TagReport rpt = new TagReport("TagReport.pdf");
//            BankAccountReport rpt = new BankAccountReport("BankAccountReport.pdf");
            rpt.create();
            rpt.show();
        } catch (IOException ioe) {
            mcLogger.error(ioe.getMessage());
        }
    }

    @FXML
    public void filter() {
        reset.setVisible(true);
        reset.setDisable(false);

    }

    private void applyBindings() {
        searchField.textProperty().bindBidirectional(searchModel.searchTextProperty());
        search.disableProperty().bind(searchModel.notEmptyToVisibilityBinding());
    }
}