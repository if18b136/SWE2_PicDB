package PresentationModels;

import Models.Picture;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.sql.SQLException;
import java.util.List;

import Service.BusinessLayer;

public class PictureList_PM {
    private List<Picture_PM> pictureList;  // does this need to be PicturePM or Picture?
    private IntegerProperty currentPicIndex = new SimpleIntegerProperty();

    public PictureList_PM() throws SQLException {
        // TODO get the picture initialization into the business layer so a list can be called from here
        BusinessLayer bl = new BusinessLayer();
        bl.initPicNameList();
        pictureList = bl.getPicList();
        currentPicIndex.set(-1);
    }

    public Picture_PM viewPicture() {
        return pictureList.get(currentPicIndex.get());
    }
}
