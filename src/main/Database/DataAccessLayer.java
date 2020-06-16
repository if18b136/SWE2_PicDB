package main.Database;

import main.Models.Photographer;
import main.Models.Picture;
import main.PresentationModels.Photographer_PM;
import main.PresentationModels.Picture_PM;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataAccessLayer implements DAL {
    final Logger DALLogger = LogManager.getLogger("Data Access Layer");
    private static DataAccessLayer dal = new DataAccessLayer();

    private DataAccessLayer() {
        initialize();
    }

    public static DataAccessLayer getInstance() {
        return dal;
    }

    @Override
    public void initialize() {
        //init DBConnection with properties from config here?
    }

    //access db, get model from picture name - was more practical at picture creation than search by id which has to be extracted extra.
    @Override
    public Picture getPicture(String name) throws Exception{
        try{
            DBConnection con = DBConnection.getInstance();
            int id = con.getPicID(name);
            if(id == -1 ) {
                throw new Exception("DALException at DAL.addPicture: Receiving ID to picture named " + name + " failed");
            }
            Picture pic = con.createPicModel(id,name);
            if(pic.getID() != -1) {
                return  pic;
            } else {
                throw new Exception("DALException at DAL.getPicture: Picture Model could not be created.");
            }
        } catch(SQLException sql) {
            throw new Exception("DALException at DAL.getPicture: "+ sql.getMessage());
        }
    }

    @Override
    public List<Photographer_PM> retrievePhotographers() throws Exception {
        try{
            List<Photographer_PM> photographerList = new ArrayList<>();
            DBConnection con = DBConnection.getInstance();
            for(Photographer photographer : con.retrievePhotographers()) {
                photographerList.add(new Photographer_PM(photographer));
            }
            return photographerList;
        } catch (SQLException sql) {
            throw new Exception("DALException at DAL.retrievePhotographers: "+ sql.getMessage());
        }
    }

    @Override
    public Photographer addNewPhotographer(String firstName, String lastName, LocalDate birthday, String notes) throws Exception {
        try{
            DBConnection con = DBConnection.getInstance();
            int ID = con.addNewPhotographer(firstName,lastName,birthday,notes);
            Photographer photographer = new Photographer();
            photographer.setID(ID);
            photographer.setFirstName(firstName);
            photographer.setLastName(lastName);
            photographer.setBirthDay(birthday);
            photographer.setNotes(notes);
            return photographer;
        }  catch (SQLException sql) {
            throw new Exception("DALException at DAL.addNewPhotographer: " + sql.getMessage());
        }
    }

    @Override
    public Photographer editPhotographer(int ID, String firstName, String lastName, LocalDate birthday, String notes) throws Exception {
        try{
            DBConnection con = DBConnection.getInstance();
            con.editPhotographer(ID,firstName,lastName,birthday,notes);
            Photographer photographer = new Photographer();
            photographer.setID(ID);
            photographer.setFirstName(firstName);
            photographer.setLastName(lastName);
            photographer.setBirthDay(birthday);
            photographer.setNotes(notes);
            return photographer;
        }  catch (SQLException sql) {
            throw new Exception("DALException at DAL.editPhotographer: "+ sql.getMessage());
        }
    }

    @Override
    public void deletePhotographer(int ID) throws Exception {
        try{
            DBConnection con = DBConnection.getInstance();
            con.deletePhotographer(ID);
        }  catch (SQLException sql) {
            throw new Exception("DALException at DAL.editPhotographer: "+ sql.getMessage());
        }
    }

    @Override
    public Picture addNewPicture(String name/*, Date date*/, String expTime, String maker, String model) throws Exception{
        try{
            DBConnection con = DBConnection.getInstance();
            con.uploadPic(name,/*date,*/expTime,maker,model);
            return getPicture(name);
        } catch(SQLException sql) {
            throw new Exception("DALException at DAL.addNewPicture: "+ sql.getMessage());
        }
    }

    @Override
    public HashMap<Integer, String> getAllPictureNames() throws Exception {
        try{
            DBConnection con = DBConnection.getInstance();
            return con.getAllPictureNames();
        } catch (SQLException sql) {
            throw new Exception("DALException at DAL.getAllPictureNames: " + sql.getMessage());
        }
    }

    @Override
    public Picture_PM createPictureModel(int id, String name) throws Exception{
        try{
            DBConnection con = DBConnection.getInstance();
            return new Picture_PM(con.createPicModel(id, name));
        } catch(SQLException sql) {
            throw new Exception("DALException at DAL.createPictureModel: " + sql.getMessage());
        }
    }

    @Override
    public void addIptc(int picID, String type, String value) throws Exception{
        try{
            DBConnection con = DBConnection.getInstance();
            con.addIptc(picID,type,value);
        } catch(SQLException sql) {
            DALLogger.error(sql.getMessage());
            throw new Exception("DALException at DAL.addIptc: " + sql.getMessage());
        }
    }

    @Override
    public void updateIptc(int iptcID, String value) throws Exception {
        try{
            DBConnection con = DBConnection.getInstance();
            con.updateIptc(iptcID,value);
        } catch(SQLException sql) {
            DALLogger.error(sql.getMessage());
            throw new Exception("DALException at DAL.updateIptc: " + sql.getMessage());
        }
    }

    @Override
    public void assignPhotographer(int picID, String lastName) throws Exception {
        try{
            DBConnection con = DBConnection.getInstance();
            // evaluate if there is already a photographer with the name
            int photographerID = con.checkPhotographer(lastName);   //returns either the ID or 0 for no entry found
            if(photographerID == 0) {   // name does not exist, create new photographer
                photographerID = con.addNewPhotographer(lastName); // return new ID
            }
            con.newPicPhotographer(picID,photographerID);
        } catch(SQLException sql) {
            DALLogger.error(sql.getMessage());
            throw new Exception("DALException at DAL.assignPhotographer: " + sql.getMessage());
        }
    }

    @Override
    public boolean assignPhotographer(int picID, String firstName, String lastName) throws Exception {
        try{
            DBConnection con = DBConnection.getInstance();
            // evaluate if there is already a photographer with the name
            int photographerID = con.checkPhotographer(firstName, lastName);   //returns either the ID or 0 for no entry found
            if(photographerID > 0) {
                con.newPicPhotographer(picID,photographerID);
                return true;
            }
            return false;
        } catch(SQLException sql) {
            DALLogger.error(sql.getMessage());
            throw new Exception("DALException at DAL.assignPhotographer: " + sql.getMessage());
        }
    }
}
