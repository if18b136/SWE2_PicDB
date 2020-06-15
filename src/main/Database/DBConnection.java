package main.Database;
import main.Models.EXIF;
import main.Models.IPTC;
import main.Models.Photographer;
import main.Models.Picture;
import main.PresentationModels.Picture_PM;
import main.Service.Config;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

public class DBConnection {
    final static Logger DBConLogger = LogManager.getLogger("Database Connection");
    //TODO change complete EXIF access into fixed data instead of variable strings
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
        return picNameID;   //TODO handle picture not found - will be handled in DAL
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
        ResultSet result =retrievePhotographers.executeQuery();
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

    public void addNewPhotographer(String firstName, String lastName, LocalDate birthday, String notes) throws SQLException {
        PreparedStatement addNewPhotographer = con.prepareStatement("insert into picdb.person(VORNAME,NACHNAME,GEBURTSTAG,NOTIZEN) values(?,?,?,?)");
        addNewPhotographer.setString(1,firstName);
        addNewPhotographer.setString(2,lastName);
        addNewPhotographer.setDate(3, Date.valueOf(birthday));
        addNewPhotographer.setString(4,notes);
        addNewPhotographer.execute();
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

    // this currently gives the newest entry - not suitable for anything else than getting the just created personID.
    public int addNewPhotographer(String lastName) throws SQLException {
        PreparedStatement addPerson = con.prepareStatement("insert into picdb.person(NACHNAME) values(?)");
        addPerson.setString(1,lastName);
        addPerson.execute();
        addPerson = con.prepareStatement("select ID from picdb.person where NACHNAME=?");
        addPerson.setString(1,lastName);
        ResultSet result = addPerson.executeQuery();
        int id = 0;
        while (result.next()) {
            id = result.getInt(1);
        }
        return id;
    }

    // this currently gives the newest entry - not suitable for anything else than getting the just created personID.
    public int addNewPhotographer(String firstName, String lastName) throws SQLException {
        PreparedStatement addPerson = con.prepareStatement("insert into picdb.person(VORNAME,NACHNAME) values(?,?)");
        addPerson.setString(1,firstName);
        addPerson.setString(2,lastName);
        addPerson.execute();
        addPerson = con.prepareStatement("select ID from picdb.person where VORNAME=? and NACHNAME=?");
        addPerson.setString(1,firstName);
        addPerson.setString(2,lastName);
        ResultSet result = addPerson.executeQuery();
        int id = 0;
        while (result.next()) {
            id = result.getInt(1);
        }
        return id;
    }

    public void newPicPhotographer(int picID, int personID) throws SQLException {
        PreparedStatement addPicturePerson = con.prepareStatement("insert into picdb.picture_person(FK_PICTURE_ID,FK_PERSON_ID) values(?,?)");
        addPicturePerson.setInt(1,picID);
        addPicturePerson.setInt(2,personID);
        addPicturePerson.execute();
    }

    //TODO person needs uniqueness or else simply checking for lastname is not enough to get a single output eventually
    public int checkPhotographer(String lastName) throws SQLException {
        PreparedStatement getPhotographer = con.prepareStatement("select ID from picdb.person where NACHNAME=?");
        getPhotographer.setString(1,lastName);
        ResultSet result = getPhotographer.executeQuery();
        return result.next() ? result.getInt(1) : 0 ;
    }

    public int checkPhotographer(String firstName, String lastName) throws SQLException {
        PreparedStatement getPhotographer = con.prepareStatement("select ID from picdb.person where NACHNAME=? and VORNAME=?");
        getPhotographer.setString(1,lastName);
        getPhotographer.setString(2,firstName);
        ResultSet result = getPhotographer.executeQuery();
        return result.next() ? result.getInt(1) : 0 ;
    }

    //TODO extract EXIF and IPTC retrieve to own functions
    public Picture createPicModel(Integer id, String name) throws SQLException {
        Picture pic = new Picture();
        pic.setID(id);
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
        //TODO get photographer name from database entry instead of as a written string - probably needs db changes to metadata table
        System.out.println("Picture: " + name);
        PreparedStatement getIPTC = con.prepareStatement("select ID,NAME,DESCRIPTION from picdb.metadata where FK_MD_PICTURE_ID=(select id from picture where name=?) and TYPE='IPTC'");
        getIPTC.setString(1, name);
        result = getIPTC.executeQuery();
        IPTC iptc = new IPTC();
        while (result.next()) {
            String input = result.getString(2);
            switch(input) {
                case "Copyright":
                    iptc.setCopyrightID(result.getInt(1));
                    iptc.setCopyright(result.getString(3));
                    break;
                case "Photographer":
                    iptc.setPhotographerID(result.getInt(1));
                    iptc.setPhotographer(result.getString(3));
                    break;
                case "Tags":
                    iptc.setTagListID(result.getInt(1));
                    iptc.setTagList(result.getString(3));
                    break;
                default:
                    //TODO add error when other type found.
            }
        }
        pic.setIPTC(iptc);
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

}
