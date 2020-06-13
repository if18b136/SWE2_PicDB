package Models;

import java.util.List;
public class IPTC {
    // 3 IDs because of the database format
    //TODO Constructor for default value initialisation?
    private int copyrightID = -1;
    private int photographerID = -1;
    private int tagListID = -1;
    private String photographer = "";
    private String copyright = "";
    private String tagList = ""; //TODO change to List and implement proper listing in gui

    public int getCopyrightID() { return copyrightID; }
    public void setCopyrightID(int ID) { this.copyrightID = ID; }

    public int getPhotographerID() { return photographerID; }
    public void setPhotographerID(int ID) { this.photographerID = ID; }

    public int getTagListID() { return tagListID; }
    public void setTagListID(int ID) { this.tagListID = ID; }

    public String getPhotographer() { return photographer; }
    public void setPhotographer(String photographer) { this.photographer = photographer; }

    public String getCopyright() { return copyright; }
    public void setCopyright(String copyright) { this.copyright = copyright; }

    public String getTagList() { return tagList; }
    public void setTagList(String tagList) { this.tagList = tagList; }
}
