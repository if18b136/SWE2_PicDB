package Database;

import Models.Picture;
import PresentationModels.PictureList_PM;
import PresentationModels.Picture_PM;

import java.sql.SQLException;
import java.util.HashMap;

public class DataAccessLayer {
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
}
