package PresentationModels;

import Models.IPTC;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.apache.logging.log4j.core.util.JsonUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

    public HashMap<String,String> getValues() {
        HashMap<String,String> iptcList = new HashMap<>();
        iptcList.put("Photographer",photographer.get());
        iptcList.put("Copyright",copyright.get());
        iptcList.put("Tags",tagList.get());
        return iptcList;
    }
}
