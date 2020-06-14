package Database;

import Models.Picture;
import PresentationModels.PictureList_PM;
import PresentationModels.Picture_PM;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.sql.SQLException;
import java.util.HashMap;

public class DataAccessLayer {
    final Logger DALLogger = LogManager.getLogger("Data Access Layer");
    private static DataAccessLayer dal = new DataAccessLayer();

    private DataAccessLayer() {}

    public static DataAccessLayer getInstance() {
        return dal;
    }

    //access db, get model from picture name
    Picture getPicture(String name) throws Exception{
        try{
            DBConnection con = DBConnection.getInstance();
            int id = con.getPicID(name);
            if(id == -1 ) {
                throw new Exception("DALException at DAL.addPicture: Receiving ID to picture named " + name + " failed");
            }
            return con.createPicModel(id,name); // TODO add Exception for empty model when pic not found
        } catch(SQLException sql) {
            throw new Exception("DALException at DAL.getPicture: "+ sql.getMessage()); //TODO implement true DALException with own class
        }
    }

    public Picture addNewPicture(String name/*, Date date*/, String expTime, String maker, String model) throws Exception{
        try{
            DBConnection con = DBConnection.getInstance();
            con.uploadPic(name,/*date,*/expTime,maker,model);
            return getPicture(name);
        } catch(SQLException sql) {
            throw new Exception("DALException at DAL.addPicture: "+ sql.getMessage());
        }
    }

    public HashMap<Integer, String> getAllPictureNames() throws Exception {
        try{
            DBConnection con = DBConnection.getInstance();
            return con.getAllPictureNames();
        } catch (SQLException sql) {
            throw new Exception("DALException at DAL.getAllPictureNames: " + sql.getMessage());
        }
    }

    public Picture_PM createPictureModel(int id, String name) throws Exception{
        try{
            DBConnection con = DBConnection.getInstance();
            return new Picture_PM(con.createPicModel(id, name));
        } catch(SQLException sql) {
            throw new Exception("DALException at DAL.createPictureModel: " + sql.getMessage());
        }
    }

    public void addIptc(int picID, String type, String value) throws Exception{
        try{
            DBConnection con = DBConnection.getInstance();
            con.addIptc(picID,type,value);
        } catch(SQLException sql) {
            DALLogger.error(sql.getMessage());
            throw new Exception("DALException at DAL.addIptc: " + sql.getMessage());
        }
    }

    public void updateIptc(int iptcID, String value) throws Exception {
        try{
            DBConnection con = DBConnection.getInstance();
            con.updateIptc(iptcID,value);
        } catch(SQLException sql) {
            DALLogger.error(sql.getMessage());
            throw new Exception("DALException at DAL.updateIptc: " + sql.getMessage());
        }
    }

    public void assignPhotographer(int picID, String lastName) throws Exception {
        try{
            DBConnection con = DBConnection.getInstance();
            // evaluate if there is already a photographer with the name
            int photographerID = con.checkPhotographer(lastName);   //returns either the ID or 0 for no entry found
            if(photographerID == 0) {   // name does not exist, create new photographer
                photographerID = con.newPhotographer(lastName); // return new ID
            }
            con.newPicPhotographer(picID,photographerID);
        } catch(SQLException sql) {
            DALLogger.error(sql.getMessage());
            throw new Exception("DALException at DAL.assignPhotographer: " + sql.getMessage());
        }
    }

    public void assignPhotographer(int picID, String firstName, String lastName) throws Exception {
        try{
            DBConnection con = DBConnection.getInstance();
            // evaluate if there is already a photographer with the name
            int photographerID = con.checkPhotographer(firstName, lastName);   //returns either the ID or 0 for no entry found
            if(photographerID == 0) {   // name does not exist, create new photographer
                photographerID = con.newPhotographer(firstName, lastName); // return new ID
            }
            con.newPicPhotographer(picID,photographerID);
        } catch(SQLException sql) {
            DALLogger.error(sql.getMessage());
            throw new Exception("DALException at DAL.assignPhotographer: " + sql.getMessage());
        }
    }
}
