package Service;

import Database.DBConnection;
import PresentationModels.Picture_PM;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BusinessLayer {
    private DBConnection con;
    private HashMap<Integer,String> picList; // currently loading pictures and then creating the models afterwards seems simpler than already creating an PM list in the DatabaseAccess-layer
    private List<Picture_PM> picPmList;

    public void initPicNameList() throws SQLException {
        picList = con.getAllPictureNames(); // return list of finished Picture_PMs would be the most direct way
    }

    // complete list of currently existing pictures - turned into PresentationModels
    public void createPicList() throws SQLException {
        for(Map.Entry<Integer,String> pic : picList.entrySet()) {
            Picture_PM picturePresentationModel = (new Picture_PM(con.createPicModel(pic.getKey(),pic.getValue())));
            picPmList.add(picturePresentationModel);
        }
    }

    public List<Picture_PM> getPicList() {
        return picPmList;
    }

    //TODO add load picture into view-function from mainController here
}
