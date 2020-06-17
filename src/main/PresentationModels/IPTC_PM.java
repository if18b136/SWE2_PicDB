package main.PresentationModels;

import javafx.beans.property.*;
import main.Models.IPTC;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class IPTC_PM {
//    private IntegerProperty photographerID = new SimpleIntegerProperty();
    private IntegerProperty copyrightID = new SimpleIntegerProperty();
    private IntegerProperty tagListID = new SimpleIntegerProperty();
//    private StringProperty photographer = new SimpleStringProperty();
    private StringProperty copyright = new SimpleStringProperty();
    private ObjectProperty<List<String>> tagList = new SimpleObjectProperty<>();

    public IPTC_PM (IPTC model) {
        if(model == null){ model = new IPTC();}
        copyrightID.set(model.getCopyrightID());
//        photographerID.set(model.getPhotographerID());
//        tagListID.set(model.getTagListID());
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
    public ObjectProperty<List<String>> tagListProperty() {
        return tagList;
    }

//    public int getPhotographerID() { return photographerID.get(); }
    public int getCopyrightID() { return copyrightID.get(); }
    public int getTagListID() { return tagListID.get(); }

//    public String getPhotographer() { return photographer.get(); }
    public String getCopyright() { return copyright.get(); }
    public List<String> getTagList() { return tagList.get(); }
    public String getTagListString() { return listToString(tagList.get()); }

//    public void setPhotographer(String photographer) { this.photographer.set(photographer); }
    public void setCopyright(String copyright) { this.copyright.set(copyright); }
    public void setTagList(String tagList) { this.tagList.set(stringToList(tagList)); }

    public HashMap<String,String> getValues() {
        HashMap<String,String> iptcList = new HashMap<>();
//        iptcList.put("Photographer",photographer.get());
        iptcList.put("Copyright",copyright.get());
        iptcList.put("Tags",listToString(tagList.get()));
        return iptcList;
    }

    public void saveNewIptc(IPTC iptc) {
//        iptc.setPhotographer(photographer.get());
        iptc.setCopyright(copyright.get());
        iptc.setTagList(tagList.get());
    }

    public List<String> stringToList(String tagList) {
        String[] split = tagList.split("[.,:;()\\[\\]'\\\\/!?\\s\"]+"); // Master of all RegEx splits
        return new ArrayList<>(Arrays.asList(split));
    }

    public String listToString(List<String> list) {
        return String.join(", ", list);
    }

}
