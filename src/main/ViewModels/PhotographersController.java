package main.ViewModels;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import main.Models.Photographer;
import main.PresentationModels.PhotographerList_PM;
import main.PresentationModels.Photographer_PM;
import main.Service.BusinessLayer;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;


public class PhotographersController extends AbstractController {
    @FXML
    ListView<String> photographerList;
    @FXML
    TextField firstName, lastName;
    @FXML
    DatePicker birthday;
    @FXML
    TextArea notes;
    @FXML
    Button save, delete;

    private Photographer_PM currentPhotographerPM;
    private PhotographerList_PM photographerListPm = new PhotographerList_PM();
    ObservableList<String> photographerListItems = FXCollections.observableArrayList("Add new Photographer...");

    @Override
    public void initialize(URL url, ResourceBundle resources) {
        super.initialize(url, resources);
        initPhotographerList();
        System.out.println("DATEPICKER RESTRICTION STILL MISSING FOR DATE > TODAY");
    }

    public void loadPhotographerList() {
        BusinessLayer bl = BusinessLayer.getInstance();
        photographerListPm.setPhotographerList(bl.createPhotographerList());
        photographerListItems = photographerListPm.getNames();
        photographerListItems.add("Add new Photographer...");
        photographerList.setItems(photographerListItems);
    }

    public void initPhotographerList() {
        loadPhotographerList();   //TODO somewhere the list gets initialized already - find where
        photographerList.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends String> ov, String old_name, String new_name) -> {
            if(new_name != null) {  //TODO find out why there is a Event call without a new selection
                if(photographerList.getSelectionModel().getSelectedItem().equals("Add new Photographer...")) {
                    resetInfo();
                    save.setText("Save new");
                    save.setOnMouseClicked(mouseEvent -> saveInfo());
                    delete.setVisible(false);
                    delete.setDisable(true);
                } else {
                    photographerListPm.setCurrentListIndex(photographerList.getSelectionModel().getSelectedIndex());
                    currentPhotographerPM = photographerListPm.getCurrentPhotographerPm();
                    refreshInfo();
                    save.setText("save edit");
                    save.setOnMouseClicked(mouseEvent -> editInfo());
                    delete.setVisible(true);
                    delete.setDisable(false);
                }
                save.setDisable(false);
            }
        });
    }

    public void resetInfo() {
        firstName.setText("");
        lastName.setText("");
        birthday.setValue(LocalDate.of(1,1,1));
        notes.setText("");
    }

    public void refreshInfo() {
        firstName.setText(currentPhotographerPM.getPhotographer().getFirstName());
        lastName.setText(currentPhotographerPM.getPhotographer().getLastName());
        birthday.setValue(currentPhotographerPM.getPhotographer().getBirthDay());
        notes.setText(currentPhotographerPM.getPhotographer().getNotes());
    }

    //TODO - catch either adding new or make an change so that existing photographer don't get inserted again
    @FXML
    public void saveInfo() {
        BusinessLayer bl = BusinessLayer.getInstance();
        boolean valid = bl.addNewPhotographer(firstName.getText(),lastName.getText(),birthday.getValue(),notes.getText());
        if(valid) {
            loadPhotographerList();
            currentPhotographerPM = new Photographer_PM(new Photographer());    // reset to no current PM
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Business Layer Warning");
            alert.setContentText("The new Photographer could not be created - your input failed the validation.");
            alert.showAndWait();
        }
    }

    @FXML
    public void editInfo() {
        BusinessLayer bl = BusinessLayer.getInstance();
        boolean valid = bl.editPhotographer(currentPhotographerPM.getPhotographer().getID(),firstName.getText(),lastName.getText(),birthday.getValue(),notes.getText());
        if(valid) {
            int index = photographerList.getSelectionModel().getSelectedIndex();
            loadPhotographerList();
            photographerList.getSelectionModel().select(index);
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Business Layer Warning");
            alert.setContentText("The new Photographer could not be created - your input failed the validation.");
            alert.showAndWait();
        }
    }

    @FXML
    public void deletePhotographer() {
        BusinessLayer bl = BusinessLayer.getInstance();
        bl.deletePhotographer(currentPhotographerPM.getPhotographer().getID());
        currentPhotographerPM = new Photographer_PM(new Photographer());    // reset to no current PM
        loadPhotographerList();
    }
}
