package PresentationModels;

import Models.IPTC;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.apache.logging.log4j.core.util.JsonUtils;

public class IPTC_PM {
    private IntegerProperty copyrightID = new SimpleIntegerProperty();
    private IntegerProperty photographerID = new SimpleIntegerProperty();
    private IntegerProperty tagListID = new SimpleIntegerProperty();
    private StringProperty photographer = new SimpleStringProperty();
    private StringProperty copyright = new SimpleStringProperty();
    private StringProperty tagList = new SimpleStringProperty();

    public IPTC_PM (IPTC model) {
        System.out.println("Test IPTC PM constructor");
        if(model == null){ model = new IPTC();}
        copyrightID.set(model.getCopyrightID());
        photographerID.set(model.getPhotographerID());
        tagListID.set(model.getTagListID());
        photographer.set(model.getPhotographer());
        copyright.set(model.getCopyright());
        tagList.set(model.getTagList());
    }
}
