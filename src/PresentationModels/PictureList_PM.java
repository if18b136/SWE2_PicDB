package PresentationModels;

import Models.Picture;
import javafx.beans.property.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import Service.BusinessLayer;

public class PictureList_PM {
    private List<Picture_PM> pictureList;
    private IntegerProperty currentPicIndex = new SimpleIntegerProperty();
    private StringProperty currentPicName = new SimpleStringProperty();

    public PictureList_PM() throws Exception {
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

    public List<Picture_PM> getPictureList() { return this.pictureList; }

    // TODO - evaluate if refresh is needed anywhere
    public void refreshPictureList() throws Exception {
        BusinessLayer bl = BusinessLayer.getInstance();
        bl.initPicNameList();
        bl.createPicList();
        pictureList = bl.getPicList();

        if (!pictureList.get(currentPicIndex.get()).getName().equals(currentPicName.get())) {
            //pic not there anymore
            currentPicIndex.set(-1);
        } // else is redundant because we already have the index set if the picture still exists
    }
}
