package Database;
import Models.EXIF;
import Models.IPTC;
import Models.Picture;
import PresentationModels.Picture_PM;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class DBConnection {
    final Logger DBConLogger = LogManager.getLogger("Database Connection");
    //TODO change complete EXIF access into fixed data instead of variable strings
    private static DBConnection jdbc;
    private Connection con;
    private String url = "jdbc:mysql://127.0.0.1:3306/picdb";
    private String username = "root";
    private String password = "";
    //private int picNameID = 1;    now gets auto incremented
    //private int picMetaID = 1;    now gets auto incremented

    //Static Singleton initialisation
    private DBConnection() throws SQLException{
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.con = DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException cnf) {
            System.out.println("Database Connection Creation Failed : " + cnf.getMessage());
            cnf.printStackTrace();
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
                    System.out.println("Database Connection created successfully.");
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
        //insertPic.setInt(1,picNameID);    id now gets auto incremented
        insertPic.setString(1,name);
        insertPic.execute();
        System.out.println("Successfully inserted pic:" + name);
        PreparedStatement insertMeta = con.prepareStatement("insert into picdb.metadata(TYPE,NAME,DESCRIPTION,FK_MD_PICTURE_ID) values(?,?,?,?)");
        /*insertMeta.setInt(1, picMetaID);
        insertMeta.setString(2,"EXIF");
        insertMeta.setString(3, "Date");
        insertMeta.setDate(4, (java.sql.Date) date);
        insertMeta.setInt(5,picNameID);
        insertMeta.execute();
        picMetaID++;
        insertMeta = con.prepareStatement("insert into picdb.metadata values(?,?,?,?,?)");*/

        int picNameID = getPicID(name);

        //insertMeta.setInt(1, picMetaID); ID now gets auto incremented
        insertMeta.setString(1,"EXIF");
        insertMeta.setString(2, "Exposure");
        insertMeta.setString(3, expTime);
        insertMeta.setInt(4,picNameID);
        insertMeta.execute();
        System.out.println("Added metadata ExpTime for pic" + name);
        //picMetaID++;  auto increment
        insertMeta = con.prepareStatement("insert into picdb.metadata(TYPE,NAME,DESCRIPTION,FK_MD_PICTURE_ID) values(?,?,?,?)");
        //insertMeta.setInt(1, picMetaID); ID now gets auto incremented
        insertMeta.setString(1,"EXIF");
        insertMeta.setString(2, "Maker");
        insertMeta.setString(3, maker);
        insertMeta.setInt(4,picNameID);
        insertMeta.execute();
        System.out.println("Added metadata Maker for pic" + name);
        //picMetaID++;  auto increment
        insertMeta = con.prepareStatement("insert into picdb.metadata(TYPE,NAME,DESCRIPTION,FK_MD_PICTURE_ID) values(?,?,?,?)");
        //insertMeta.setInt(1, picMetaID); ID now gets auto incremented
        insertMeta.setString(1,"EXIF");
        insertMeta.setString(2, "Model");
        insertMeta.setString(3, model);
        insertMeta.setInt(4,picNameID);
        insertMeta.execute();
        System.out.println("Added metadata Model for pic" + name);
        //picMetaID++;  auto increment
        //picNameID++;  auto increment

        //con.close();
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

    public Picture createPicModel(Integer id, String name) throws SQLException {
        Picture pic = new Picture();
        pic.setID(id);
        pic.setName(name);

        PreparedStatement getEXIF = con.prepareStatement("select ID,NAME,DESCRIPTION from picdb.metadata where FK_MD_PICTURE_ID=(select id from picture where name=?) AND TYPE='EXIF'");
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
        PreparedStatement getIPTC = con.prepareStatement("select ID,NAME,DESCRIPTION from picdb.metadata where FK_MD_PICTURE_ID=(select id from picture where name=?) & TYPE='IPTC'");
        getIPTC.setString(1, name);
        result = getIPTC.executeQuery();
        IPTC iptc = new IPTC();
        while (result.next()) {
            String input = result.getString(2);
            System.out.println("load IPTC " + input);
            switch(input) {
                case "Copyright":
                    iptc.setCopyrightID(result.getInt(1));
                    iptc.setCopyright(result.getString(3));
                    System.out.println("Copyright set to: " + iptc.getCopyright());
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
        System.out.println("Copyright in Picture Model: " + pic.getIPTC().getCopyright());
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

    // this currently gives the newest entry - not suitable for anything else than getting the just created personID.
    public int newPhotographer(String lastName) throws SQLException {
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
    public int newPhotographer(String firstName, String lastName) throws SQLException {
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
        System.out.println("connected IDs: pic " + picID + " - person " + personID);
    }
}
