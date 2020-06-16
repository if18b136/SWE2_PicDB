package main.PresentationModels;

import main.Models.IPTC;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.HashMap;

public class IPTC_PM {
//    private IntegerProperty photographerID = new SimpleIntegerProperty();
    private IntegerProperty copyrightID = new SimpleIntegerProperty();
    private IntegerProperty tagListID = new SimpleIntegerProperty();
//    private StringProperty photographer = new SimpleStringProperty();
    private StringProperty copyright = new SimpleStringProperty();
    private StringProperty tagList = new SimpleStringProperty();

    public IPTC_PM (IPTC model) {
        if(model == null){ model = new IPTC();}
        copyrightID.set(model.getCopyrightID());
//        photographerID.set(model.getPhotographerID());
        tagListID.set(model.getTagListID());
//        photographer.set(model.getPhotographer());
        copyright.set(model.getCopyright());
        tagList.set(model.getTagList());
    }

    public void refreshIptc(IPTC model) {
//        photographer.set(model.getPhotographer());
        copyright.set(model.getCopyright());
        tagList.set(model.getTagList());
    }

//    public StringProperty photographerProperty() {
//        return photographer;
//    }
    public StringProperty copyrightProperty() {
        return copyright;
    }
    public StringProperty tagListProperty() {
        return tagList;
    }

//    public int getPhotographerID() { return photographerID.get(); }
    public int getCopyrightID() { return copyrightID.get(); }
    public int getTagListID() { return tagListID.get(); }

//    public String getPhotographer() { return photographer.get(); }
    public String getCopyright() { return copyright.get(); }
    public String getTagList() { return tagList.get(); }

//    public void setPhotographer(String photographer) { this.photographer.set(photographer); }
    public void setCopyright(String copyright) { this.copyright.set(copyright); }
    public void setTagList(String tagList) { this.tagList.set(tagList); }

    public HashMap<String,String> getValues() {
        HashMap<String,String> iptcList = new HashMap<>();
//        iptcList.put("Photographer",photographer.get());
        iptcList.put("Copyright",copyright.get());
        iptcList.put("Tags",tagList.get());
        return iptcList;
    }

    public void saveNewIptc(IPTC iptc) {
//        iptc.setPhotographer(photographer.get());
        iptc.setCopyright(copyright.get());
        iptc.setTagList(tagList.get());
    }

}
