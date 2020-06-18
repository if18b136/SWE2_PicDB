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

/**
 * The Data Access Layer manages all Access to the Database that the Business Layer wants to do.
 * It interprets calls from Business Layer and utilizes the DBConnection class to return needed database data.
 */
public class DataAccessLayer implements DAL {
    final Logger DALLogger = LogManager.getLogger("Data Access Layer");
    private static DataAccessLayer dal;

    /**
     * Data Access Layer Constructor.
     * Calls initialize Function.
     */
    private DataAccessLayer() {
        initialize();
    }

    /**
     * Function to make first Data Access Layer object so it can be called as Singleton afterwards.
     */
    @Override
    public void initialize() {
        dal = new DataAccessLayer();
    }

    /**
     * Instead of creating a new class object each time dal returns the same (singleton) object every time.
     * If it has not been initialized yet it returns a new object, which will be initialized through the call.
     * @return  dal object
     */
    public static DataAccessLayer getInstance() {
        return dal == null ? new DataAccessLayer() : dal ;
    }

    /**
     * Access db, get model from picture name - was more practical at picture creation than search by id which has to be extracted extra.
     * Checks if the picture is in the database first, then calls DBConnection class for model creation.
     * @param name  The name of the picture as string
     * @return  A newly created picture model with the existing picture data from the database
     * @throws Exception    Used instead of writing an own DALException
     */
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

    /**
     * Gets a list of Photographer models from the DBConnection class and turns it into a Photographer Presentation model list
     * @return  Photographer Presentation model list
     * @throws Exception    Used instead of writing an own DALException
     */
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

    /**
     * Calls DBConnection class to create a new photographer database entry.
     * Takes the returned new ID and creates a photographer model with the input data, which is then returned.
     * @param firstName Photographer firstname
     * @param lastName  Photographer lastname
     * @param birthday  Photographer birth date
     * @param notes     additional notes about the photographer
     * @return          A Photographer model with the new data
     * @throws Exception    Used instead of writing an own DALException
     */
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

    /**
     * Edit a photographer database entry via DBConnection class
     * @param ID        Photographer database ID
     * @param firstName Photographer firstname
     * @param lastName  Photographer lastname
     * @param birthday  Photographer birth date
     * @param notes     additional notes about the photographer
     * @return          A Photographer model with the modified data
     * @throws Exception    Used instead of writing an own DALException
     */
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

    /**
     * Call the DBConnection class to delete a Photographer database entry.
     * @param ID    The photographer's database ID
     * @throws Exception    Used instead of writing an own DALException
     */
    @Override
    public void deletePhotographer(int ID) throws Exception {
        try{
            DBConnection con = DBConnection.getInstance();
            con.deletePhotographer(ID);
        }  catch (SQLException sql) {
            throw new Exception("DALException at DAL.editPhotographer: "+ sql.getMessage());
        }
    }

    /**
     * Call DBConnection class to add information about a newly added picture into the database
     * @param name      picture name
     * @param expTime   EXIF info - Exposition time
     * @param maker     EXIF info - camera maker
     * @param model     EXIF info - camera model
     * @return          a Picture model with the new data
     * @throws Exception    Used instead of writing an own DALException
     */
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

    /**
     * Get all picture names as a iterable Hashmap
     * @return  The Hashmap containing id and name of every picture
     * @throws Exception    Used instead of writing an own DALException
     */
    @Override
    public HashMap<Integer, String> getAllPictureNames() throws Exception {
        try{
            DBConnection con = DBConnection.getInstance();
            return con.getAllPictureNames();
        } catch (SQLException sql) {
            throw new Exception("DALException at DAL.getAllPictureNames: " + sql.getMessage());
        }
    }

    /**
     * Get a certain Picture as Picture Presentation model.
     * @param id    The picture's database ID
     * @param name  The picture name
     * @return      A new Picture Presentation model containing the picture data
     * @throws Exception    Used instead of writing an own DALException
     */
    @Override
    public Picture_PM createPictureModel(int id, String name) throws Exception{
        try{
            DBConnection con = DBConnection.getInstance();
            return new Picture_PM(con.createPicModel(id, name));
        } catch(SQLException sql) {
            throw new Exception("DALException at DAL.createPictureModel: " + sql.getMessage());
        }
    }

    /**
     * Add any IPTC information about a picture into the database.
     * IPTC type can theoretically be any string input - this is not getting checked anywhere.
     * @param picID     The picture ID which will receive a new IPTC entry
     * @param type      The type of IPTC that will be added.
     * @param value     The value that will be associated to the picture together with the certain type
     * @throws Exception    Used instead of writing an own DALException
     */
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

    /**
     * Update IPTC information in the database
     * @param iptcID    the picture's database ID
     * @param value     the new IPTC value
     * @throws Exception    Used instead of writing an own DALException
     */
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



    /**
     *  get all tags from db that are associated to the picID
     *  for every missing DB entry assign(+create) new tag to pic
     *  for every entry missing in input tagList, delete tag assignment to pic
     * @param ID    Picture ID
     * @param tagList   List of Tags that will be assigned to the picture
     * @throws Exception    Used instead of writing an own DALException
     */
    @Override
    public void assignTagsToPic(int ID,List<String> tagList) throws Exception {
        try{
            DBConnection con = DBConnection.getInstance();
            List<String> dbTagList = con.getAllTags(ID);
            dbTagList.removeAll(tagList);
            for(String dbTag : dbTagList) {
                con.deleteTagPicAssignment(ID,dbTag);
            }
            dbTagList = con.getAllTags(ID);
            tagList.removeAll(dbTagList);
            for( String tag : tagList) {
                if(con.checkTag(tag) == 0) {
                    con.addTag(tag);
                }
                con.assignTagToPic(ID, con.checkTag(tag));
            }
        } catch (SQLException sql) {
            throw new Exception("DALException at DAL.assignTagsToPic: " + sql.getMessage());
        }
    }

    /**
     * Assign a photographer to a picture, if the photographer exists in the database
     * evaluate if there is already a photographer with the name
     * CheckPhotographer returns either the ID or 0 for no entry found
     * @param picID         Picture ID
     * @param firstName     Photographer firstname
     * @param lastName      Photographer lastname
     * @return              <code>true</code> if photographer exists in database
     *                      <code>false</code> if photographer does not exist.
     * @throws Exception    Used instead of writing an own DALException
     */
    @Override
    public boolean assignPhotographer(int picID, String firstName, String lastName) throws Exception {
        try{
            DBConnection con = DBConnection.getInstance();

            int photographerID = con.checkPhotographer(firstName, lastName);
            if(photographerID > 0) {
                con.newPicPhotographer(picID,photographerID);
                return true;
            }
            return false;
        } catch(SQLException sql) {
            throw new Exception("DALException at DAL.assignPhotographer: " + sql.getMessage());
        }
    }

    /**
     * Create a Hashmap of all Tag names and the number of their assignments to pictures for the Tag Report
     * @return  Hashmap with tag names and number of assignment entries in database
     * @throws Exception    Used instead of writing an own DALException
     */
    @Override
    public HashMap<String,Integer> getTagMap() throws Exception{
        try{
            DBConnection con = DBConnection.getInstance();
            return con.getTagMap();
        } catch(SQLException sql) {
            throw new Exception("DALException at DAL.getTagMap: " + sql.getMessage());
        }
    }
}
