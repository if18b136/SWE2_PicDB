package PresentationModels;

import Database.DBConnection;

import java.sql.SQLException;
import java.util.HashMap;

public class PictureModel {
    // TODO get dbConnection for the whole class not individually for every function
    private String name;
    private int ID;
    private HashMap<String,String> EXIF;
    private HashMap<String,String> IPTC;

    public void setName(String name) {
        this.name = name;
    }
    String getName() {
        return this.name;
    }

    //  set ID is only useful if name is set and returns the actual db ID of the picture if it exists in the db
    public void setID() throws SQLException {
        DBConnection jdbc = DBConnection.getInstance();
        this.ID = jdbc.getPicID(name);
    }
    int getID() {
        return this.ID;
    }

    // gets all EXIF data of the picture in a hashmap
    void setEXIF() throws SQLException {
        DBConnection jdbc = DBConnection.getInstance();
        this.EXIF = jdbc.getMetaData(this.ID,"EXIF");
    }
    public HashMap<String, String> getEXIF() {
        return EXIF;
    }

    // gets all IPTC data of the picture in a hashmap
    void setIPTC() throws SQLException {
        DBConnection jdbc = DBConnection.getInstance();
        this.IPTC = jdbc.getMetaData(this.ID,"IPTC");
    }
    public HashMap<String, String> getIPTC() {
        return IPTC;
    }
}
