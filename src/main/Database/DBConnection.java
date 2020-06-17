package main.Database;
import main.Models.EXIF;
import main.Models.IPTC;
import main.Models.Photographer;
import main.Models.Picture;
import main.Service.Config;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DBConnection {
    final static Logger DBConLogger = LogManager.getLogger("Database Connection");
    private static DBConnection jdbc;
    private Connection con;
    private String url = "";
    private String username = "";
    private String password = "";

    //Static Singleton initialisation
    private DBConnection() throws SQLException{
        try{
            Class.forName(Config.getInstance().getProperties().getProperty("driver"));
            this.url = Config.getInstance().getProperties().getProperty("url");
            this.username = Config.getInstance().getProperties().getProperty("username");
            this.con = DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException cnf) {
            DBConLogger.error("Database Connection Creation Failed : " + cnf.getMessage());
        }
    }

    public Connection getConnection(){
        return this.con;
    }

    // Synchronizing the whole class creates huge thread overhead,
    // because only one thread can access the getInstance at a time
    // By making a second instance check, which we synchronize, we minimize that overhead
    public static DBConnection getInstance() throws SQLException{
        if(jdbc == null){
            synchronized (DBConnection.class){
                if(jdbc == null){
                    jdbc = new DBConnection();
                    DBConLogger.info("Database Connection created successfully.");
                }
            }
        }
        else if(jdbc.getConnection().isClosed()){
            jdbc = new DBConnection();
        }
        return jdbc;
    }

    public int getPicID(String name) throws SQLException {
        PreparedStatement getID = con.prepareStatement("select ID from picdb.picture where name=?");
        getID.setString(1,name);
        ResultSet result = getID.executeQuery();
        int picNameID = -1;
        while (result.next()) {     //throwing this into a while loop guarantees to get the latest picture ID, even if there are more pictures with the same name
            picNameID = result.getInt(1);
        }
        result.close();
        return picNameID;
    }

    public void uploadPic(String name/*, Date date*/, String expTime, String maker, String model) throws SQLException {
        PreparedStatement insertPic = con.prepareStatement("insert into picdb.picture(name) values(?)");
        insertPic.setString(1,name);
        insertPic.execute();
        PreparedStatement insertMeta = con.prepareStatement("insert into picdb.metadata(TYPE,NAME,DESCRIPTION,FK_MD_PICTURE_ID) values(?,?,?,?)");
        /*insertMeta.setInt(1, picMetaID);
        insertMeta.setString(2,"EXIF");
        insertMeta.setString(3, "Date");
        insertMeta.setDate(4, (java.sql.Date) date);
        insertMeta.setInt(5,picNameID);
        insertMeta.execute();
        insertMeta = con.prepareStatement("insert into picdb.metadata values(?,?,?,?,?)");*/
        int picNameID = getPicID(name);
        insertMeta.setString(1,"EXIF");
        insertMeta.setString(2, "Exposure");
        insertMeta.setString(3, expTime);
        insertMeta.setInt(4,picNameID);
        insertMeta.execute();
        insertMeta = con.prepareStatement("insert into picdb.metadata(TYPE,NAME,DESCRIPTION,FK_MD_PICTURE_ID) values(?,?,?,?)");
        insertMeta.setString(1,"EXIF");
        insertMeta.setString(2, "Maker");
        insertMeta.setString(3, maker);
        insertMeta.setInt(4,picNameID);
        insertMeta.execute();
        insertMeta = con.prepareStatement("insert into picdb.metadata(TYPE,NAME,DESCRIPTION,FK_MD_PICTURE_ID) values(?,?,?,?)");
        insertMeta.setString(1,"EXIF");
        insertMeta.setString(2, "Model");
        insertMeta.setString(3, model);
        insertMeta.setInt(4,picNameID);
        insertMeta.execute();
    }

    public HashMap<String, String> getMetaData(int ID, String type) throws SQLException {
        HashMap<String, String> metaData = new HashMap<>();
        PreparedStatement retrieveEXIF = con.prepareStatement("select NAME,DESCRIPTION from picdb.metadata where ID=? and TYPE=?");
        retrieveEXIF.setInt(1, ID);
        retrieveEXIF.setString(2, type);
        ResultSet result = retrieveEXIF.executeQuery();
        while (result.next()) {
            String name =  result.getString(1);
            String desc =  result.getString(2);
            metaData.put(name,desc);
        }
        return metaData;
    }

    public HashMap<Integer,String> getAllPictureNames() throws SQLException {
        PreparedStatement getNames = con.prepareStatement("select ID, NAME from picdb.picture");
        ResultSet result = getNames.executeQuery();
        HashMap<Integer,String> pictureList = new HashMap<>();
        while(result.next()) {
            pictureList.put(result.getInt(1),result.getString(2));
        }
        return pictureList;
    }

    public List<Photographer> retrievePhotographers() throws SQLException {
        PreparedStatement retrievePhotographers = con.prepareStatement("select ID, VORNAME, NACHNAME, GEBURTSTAG, NOTIZEN from picdb.person");
        ResultSet result = retrievePhotographers.executeQuery();
        List<Photographer> photographerList = new ArrayList<>();
        while (result.next()) {
            Photographer photographer = new Photographer();
            photographer.setID(result.getInt(1));
            photographer.setFirstName(result.getString(2));
            photographer.setLastName(result.getString(3));
            if (result.getDate(4) == null) {
                photographer.setBirthDay(LocalDate.of(1, 1, 1));
            } else {
                photographer.setBirthDay(result.getDate(4).toLocalDate());
            }
            photographer.setNotes(result.getString(5));
            photographerList.add(photographer);
        }
        return photographerList;
    }

    public int addNewPhotographer(String firstName, String lastName, LocalDate birthday, String notes) throws SQLException {
        PreparedStatement addNewPhotographer = con.prepareStatement("insert into picdb.person(VORNAME,NACHNAME,GEBURTSTAG,NOTIZEN) values(?,?,?,?)");
        addNewPhotographer.setString(1,firstName);
        addNewPhotographer.setString(2,lastName);
        addNewPhotographer.setDate(3, Date.valueOf(birthday));
        addNewPhotographer.setString(4,notes);
        addNewPhotographer.execute();
        addNewPhotographer = con.prepareStatement("select ID from picdb.person where NACHNAME=?");
        addNewPhotographer.setString(1,lastName);
        ResultSet result = addNewPhotographer.executeQuery();
        int id = 0;
        while (result.next()) {
            id = result.getInt(1);
        }
        return id;
    }

    public void editPhotographer(int ID, String firstName, String lastName, LocalDate birthday, String notes) throws SQLException {
        PreparedStatement editPhotographer = con.prepareStatement("update picdb.person set VORNAME=?,NACHNAME=?,GEBURTSTAG=?,NOTIZEN=? where ID=? ");
        editPhotographer.setString(1,firstName);
        editPhotographer.setString(2,lastName);
        editPhotographer.setDate(3, Date.valueOf(birthday));
        editPhotographer.setString(4,notes);
        editPhotographer.setInt(5,ID);
        editPhotographer.execute();
    }

    public void deletePhotographer(int ID) throws SQLException {
        PreparedStatement delete = con.prepareStatement("delete from picdb.person where ID=?");
        delete.setInt(1,ID);
        delete.execute();
    }

    public void newPicPhotographer(int picID, int personID) throws SQLException {
        PreparedStatement assignment = con.prepareStatement("select count(*) from picdb.picture_person where FK_PICTURE_ID=?");//only one assignment per picture allowed
        assignment.setInt(1,picID);
        ResultSet result = assignment.executeQuery();
        if(result.next()) {
            if(result.getInt(1) != 0) {
                assignment = con.prepareStatement("update picdb.picture_person set FK_PERSON_ID=? where FK_PICTURE_ID=?");
                assignment.setInt(1,personID);
                assignment.setInt(2,picID);
                assignment.execute();
            } else {
                PreparedStatement addPicturePerson = con.prepareStatement("insert into picdb.picture_person(FK_PICTURE_ID,FK_PERSON_ID) values(?,?)");
                addPicturePerson.setInt(1,picID);
                addPicturePerson.setInt(2,personID);
                addPicturePerson.execute();
            }
        }
    }

    public int checkPhotographer(String firstName, String lastName) throws SQLException {
        PreparedStatement getPhotographer = con.prepareStatement("select ID from picdb.person where NACHNAME=? and VORNAME=?");
        getPhotographer.setString(1,lastName);
        getPhotographer.setString(2,firstName);
        ResultSet result = getPhotographer.executeQuery();
        return result.next() ? result.getInt(1) : 0 ;
    }

    //TODO extract EXIF and IPTC retrieve to own functions
    public Picture createPicModel(Integer ID, String name) throws SQLException {
        Picture pic = new Picture();
        pic.setID(ID);
        pic.setName(name);

        PreparedStatement getEXIF = con.prepareStatement("select ID,NAME,DESCRIPTION from picdb.metadata where FK_MD_PICTURE_ID=(select id from picture where name=?) and TYPE='EXIF'");
        getEXIF.setString(1, name);
        ResultSet result = getEXIF.executeQuery();
        while (result.next()) {
            EXIF exif = new EXIF();
            exif.setID(result.getInt(1));
            exif.setName(result.getString(2));
            exif.setDescription(result.getString(3));
            pic.addEXIF(exif);
        }
        PreparedStatement getIPTC = con.prepareStatement("select ID,NAME,DESCRIPTION from picdb.metadata where FK_MD_PICTURE_ID=(select id from picture where name=?) and TYPE='IPTC'");
        getIPTC.setString(1, name);
        result = getIPTC.executeQuery();
        IPTC iptc = new IPTC();
        while (result.next()) {
//            String input = result.getString(2);
//            switch(input) {
//                case "Copyright":
            iptc.setCopyrightID(result.getInt(1));
            iptc.setCopyright(result.getString(3));
//                    break;
//      Taken out and replaced with proper photographer model
//                case "Photographer":
//                    iptc.setPhotographerID(result.getInt(1));
//                    iptc.setPhotographer(result.getString(3));
//                    break;

//      Replaced with new Database model that makes accessing and querying tags more easy
//                case "Tags":
//                    iptc.setTagListID(result.getInt(1));
//                    iptc.setTagList(result.getString(3));
//                    break;
//                default:
//                    DBConLogger.warn("Warning at DBConnection.createPicModel: Non-Standard EXIF Type found.");
//            }
        }
        // Join tag and tag_picture table and get all tag names that are associated with the pic ID
        iptc.setTagList(getAllTags(ID));
        pic.setIPTC(iptc);

        Photographer photographer = new Photographer();
        PreparedStatement getPhotographer = con.prepareStatement("select FK_PERSON_ID from picdb.picture_person where FK_PICTURE_ID=?");
        getPhotographer.setInt(1,ID);
        result = getPhotographer.executeQuery();
        if(result.next()) {
            int photographerID = result.getInt(1);
            getPhotographer = con.prepareStatement("select * from picdb.person where ID=?");
            getPhotographer.setInt(1,photographerID);
            result = getPhotographer.executeQuery();
            if(result.next()) {
                photographer.setID(result.getInt(1));
                photographer.setFirstName(result.getString(2));
                photographer.setLastName(result.getString(3));
                photographer.setBirthDay(result.getDate(4).toLocalDate());
                photographer.setNotes(result.getString(5));
            }
        }
        pic.setPhotographer(photographer);
        return pic;
    }

    public void addIptc(int picID, String type, String value) throws SQLException {
        PreparedStatement addIptc = con.prepareStatement("insert into picdb.metadata(TYPE,NAME,DESCRIPTION,FK_MD_PICTURE_ID) values(?,?,?,?)");
        addIptc.setString(1,"IPTC");
        addIptc.setString(2, type);
        addIptc.setString(3, value);
        addIptc.setInt(4,picID);
        addIptc.execute();
        DBConLogger.info("IPTC entry created.");
    }

    public void updateIptc(int iptcID, String value) throws SQLException {
        PreparedStatement addIptc = con.prepareStatement("update picdb.metadata set DESCRIPTION=? where ID=?");
        addIptc.setString(1,value);
        addIptc.setInt(2,iptcID);
        addIptc.execute();
        DBConLogger.info("IPTC entry updated.");
    }

    public List<String> getAllTags(int ID) throws SQLException {
        PreparedStatement getTags = con.prepareStatement("select tag.tag from tag_picture JOIN tag on(tag.ID=tag_picture.FK_TAG_ID) where tag_picture.FK_TAG_PIC_ID=?");
        getTags.setInt(1,ID);
        ResultSet result = getTags.executeQuery();
        List<String> tagList = new ArrayList<>();
        while (result.next()) {
            tagList.add(result.getString(1));
        }
        return tagList;
    }

    public void deleteTagPicAssignment(int picID, String tag) throws SQLException {
         PreparedStatement delTagPic = con.prepareStatement("delete from picdb.tag_picture where FK_TAG_ID=(SELECT ID from tag where TAG=?) and FK_TAG_PIC_ID=?");
         delTagPic.setString(1,tag);
         delTagPic.setInt(2,picID);
         delTagPic.execute();
    }

    public int checkTag(String tag) throws SQLException {
        PreparedStatement checkTag = con.prepareStatement("select ID from picdb.tag where TAG=?");
        checkTag.setString(1,tag);
        ResultSet result = checkTag.executeQuery();
        if (result.next()) {
            return result.getInt(1);
        }
        return 0;
    }

    public void addTag(String tag) throws SQLException {
        PreparedStatement addTag = con.prepareStatement("insert into picdb.tag(TAG) values(?)");
        addTag.setString(1,tag);
        addTag.execute();
    }

    public void assignTagToPic(int picID, int tagID) throws SQLException {
        PreparedStatement assign = con.prepareStatement("insert into picdb.tag_picture(FK_TAG_ID,FK_TAG_PIC_ID) values(?,?)");
        assign.setInt(1,tagID);
        assign.setInt(2,picID);
        assign.execute();
    }

    public HashMap<String,Integer> getTagMap() throws SQLException {
        PreparedStatement getTagMap = con.prepareStatement("SELECT tag,COUNT(*) as count FROM picdb.tag_picture JOIN picdb.tag ON(tag_picture.FK_TAG_ID=tag.ID) GROUP BY tag");
        ResultSet result = getTagMap.executeQuery();
        HashMap<String,Integer> tagMap = new HashMap<>();
        while (result.next()) {
            tagMap.put(result.getString(1),result.getInt(2));
        }
        return tagMap;
    }
}
