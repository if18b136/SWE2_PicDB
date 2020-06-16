package main.PresentationModels;

import main.Models.Picture;
import main.Service.BusinessLayer;
import javafx.beans.property.*;

import java.util.ArrayList;
import java.util.List;


public class PictureList_PM {
    private List<Picture_PM> pictureList;
    private IntegerProperty currentPicIndex = new SimpleIntegerProperty();
    private StringProperty currentPicName = new SimpleStringProperty();

    public PictureList_PM(){
        BusinessLayer bl = BusinessLayer.getInstance();
        bl.initPicNameList();
        bl.createPicList();
        pictureList = bl.getPicList();
        currentPicIndex.set(-1);
    }

    public Picture_PM getCurPicView() {
        return pictureList.get(currentPicIndex.get());
    }
    public void setCurrentPic(int index) {
        this.currentPicIndex.set(index);
        this.currentPicName.set(pictureList.get(index).getName());
        System.out.println("User clicked on " + pictureList.get(index).getName());
    }

    public int getCurrentPicIndex() { return this.currentPicIndex.get(); }
    public List<Picture_PM> getPictureList() { return this.pictureList; }

    public void refreshPictureList(){
        BusinessLayer bl = BusinessLayer.getInstance();
        bl.initPicNameList();
        bl.createPicList();
        pictureList = bl.getPicList();
        if(currentPicIndex.get() != -1) {
            if (!pictureList.get(currentPicIndex.get()).getName().equals(currentPicName.get())) { //pic not there anymore
                currentPicIndex.set(-1);
            } // else is redundant because we already have the index set if the picture still exists
        }
    }
}
