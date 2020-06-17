package main.Models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
public class IPTC {
    // 3 IDs because of the database format
    private int copyrightID = -1;
//    private int photographerID = -1;
//    private int tagListID = -1;
//    private String photographer = "";
    private String copyright = "";
//    private String tagList = ""; //TODO change to List and implement proper listing in gui
    private List<String> tagList = new ArrayList<>();

    public IPTC() {}

    public IPTC(/*String photographer, */int copyrightID, String copyright,int tagListID, String tagList) {
//        this.photographer = photographer;
        this.copyright = copyright;
//        this.tagList = tagList;
    }

    public IPTC(int copyrightID, String copyright,int tagListID, List<String> tagList) {
        this.copyrightID = copyrightID;
        this.copyright = copyright;
//        this.tagListID = tagListID;
        this.tagList = tagList;
    }

    public int getCopyrightID() { return copyrightID; }
    public void setCopyrightID(int ID) { this.copyrightID = ID; }

//    public int getPhotographerID() { return photographerID; }
//    public void setPhotographerID(int ID) { this.photographerID = ID; }

//    public int getTagListID() { return tagListID; }
//    public void setTagListID(int ID) { this.tagListID = ID; }

//    public String getPhotographer() { return photographer; }
//    public void setPhotographer(String photographer) { this.photographer = photographer; }

    public String getCopyright() { return copyright; }
    public void setCopyright(String copyright) { this.copyright = copyright; }

    public String getTagListString() { return listToString(this.tagList); }
    public List<String> getTagList() { return tagList; }
    public void setTagList(String tagList) { this.tagList = stringToList(tagList); }
    public void setTagList(List<String> tagList) { this.tagList = tagList; }

    public List<String> stringToList(String tagList) {
        String[] split = tagList.split("[.,:;()\\[\\]'\\\\/!?\\s\"]+"); // Master of all RegEx splits
        return new ArrayList<>(Arrays.asList(split));
    }

    //https://stackoverflow.com/questions/47605/string-concatenation-concat-vs-operator
    //https://stackoverflow.com/questions/599161/best-way-to-convert-an-arraylist-to-a-string
    public String listToString(List<String> list) {
        return String.join(", ", list);
    }
}
