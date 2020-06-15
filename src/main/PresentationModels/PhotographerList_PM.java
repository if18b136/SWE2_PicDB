package main.PresentationModels;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import main.Service.BusinessLayer;

import java.util.ArrayList;
import java.util.List;

public class PhotographerList_PM {
    private List<Photographer_PM> photographerList;
    private IntegerProperty currentListIndex = new SimpleIntegerProperty();
    private IntegerProperty currentPhotographerID = new SimpleIntegerProperty();

    public PhotographerList_PM() {
        BusinessLayer bl = BusinessLayer.getInstance();
        photographerList = bl.createPhotographerList();
        //setting to unavailable var so I can detect if the value is called somewhere before the list selection is done.
        currentListIndex.set(-1);
        currentPhotographerID.set(-1);
    }

    public void setCurrentListIndex(int index) { this.currentListIndex.set(index); }
    public int getCurrentListIndex() { return this.currentListIndex.get(); }
    public void setCurrentPhotographerID(int ID) { this.currentPhotographerID.set(ID); }
    public int getCurrentPhotographerID() { return this.currentPhotographerID.get(); }
    public void setPhotographerList(List<Photographer_PM> photographerList) { this.photographerList = photographerList; }
    public List<Photographer_PM> getPhotographerList() { return this.photographerList; }
    public Photographer_PM getCurrentPhotographerPm() { return this.photographerList.get(currentListIndex.get()); }

    public ObservableList<String> getNames() {
        ObservableList<String> photographerNamesList = FXCollections.observableArrayList();
        for (Photographer_PM photographerPm : photographerList) {
            photographerNamesList.add(photographerPm.getPhotographer().getFirstName() + " " + photographerPm.getPhotographer().getLastName());
        }
        return photographerNamesList;
    }

}
