package Service;

import Database.DBConnection;
import Database.DataAccessLayer;
import Models.Picture;
import PresentationModels.Picture_PM;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BusinessLayer {
    private HashMap<Integer,String> picList = new HashMap<>(); // currently loading pictures and then creating the models afterwards seems simpler than already creating an PM list in the DatabaseAccess-layer
    private List<Picture_PM> picPmList = new ArrayList<>();
    private static BusinessLayer bl = new BusinessLayer();

    private BusinessLayer() {

    }

    public static BusinessLayer getInstance() {
        return bl;
    }

    public void initPicNameList() throws Exception {
        DataAccessLayer dal = DataAccessLayer.getInstance();
        picList = dal.getAllPictureNames(); // return list of finished Picture_PMs would be the most direct way
    }

    // complete list of currently existing pictures - turned into PresentationModels
    public void createPicList() throws Exception {
        DataAccessLayer dal = DataAccessLayer.getInstance();
        for(Map.Entry<Integer,String> pic : picList.entrySet()) {
            Picture_PM picture_pm = dal.createPictureModel(pic.getKey(),pic.getValue());
            picPmList.add(picture_pm);
        }
    }

    public List<Picture_PM> getPicList() {
        return picPmList;
    }

    // call DAL, add new Picture, return presentation model of new picture
    public Picture_PM addNewPicture(String name/*, Date date*/, String expTime, String maker, String model) throws Exception {
        DataAccessLayer dal = DataAccessLayer.getInstance();
        // give DAL the db data, return a picture presentation model containing the data
        Picture newPic = dal.addNewPicture(name,/*date,*/expTime,maker,model);
        return new Picture_PM(newPic);
    }

    //TODO add load picture into view-function from mainController here
}
