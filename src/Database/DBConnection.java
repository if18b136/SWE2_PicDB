package Database;
import Models.EXIF;
import Models.IPTC;
import Models.Picture;
import PresentationModels.Picture_PM;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class DBConnection {
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
        return picNameID;   //TODO handle picture not found
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
        PreparedStatement getNames = con.prepareStatement("select ID, NAME from picdb.metadata");
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
            switch(input) {
                case "copyright":
                    iptc.setCopyrightID(result.getInt(1));
                    iptc.setCopyright(result.getString(3));
                    break;
                case "photographer":
                    iptc.setPhotographerID(result.getInt(1));
                    iptc.setPhotographer(result.getString(3));
                    break;
                case "taglist":
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
}
