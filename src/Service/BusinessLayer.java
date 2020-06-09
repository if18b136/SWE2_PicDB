package Service;

import Database.DBConnection;
import Database.DataAccessLayer;
import Models.Picture;
import PresentationModels.Picture_PM;
import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.*;
import javafx.scene.image.Image;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BusinessLayer {
    final Logger BLLogger = LogManager.getLogger("Business Layer");
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
        picPmList.add(new Picture_PM(newPic));
        return new Picture_PM(newPic);
    }

    public Picture_PM extractMetadata(File selectedFile, Path src, String namePath) throws Exception{
        try{
            Path dest = Paths.get(namePath);
            String picName = selectedFile.getName();
            Files.copy(src,dest);
            Metadata metadata = ImageMetadataReader.readMetadata(selectedFile);

            // obtain the Exif directory
            ExifSubIFDDirectory subIFDDirectory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
            ExifIFD0Directory IFD0Directory = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
            ExifImageDirectory ImageDirectory = metadata.getFirstDirectoryOfType(ExifImageDirectory.class);

            // create a descriptor
            ExifSubIFDDescriptor subIFDDescriptor = new ExifSubIFDDescriptor(subIFDDirectory);
            ExifIFD0Descriptor IFD0Descriptor = new ExifIFD0Descriptor(IFD0Directory);

            // Date only works if there is a date within the EXIF data, otherwise we will get a NullPointerException
            // TODO convert date from JAVA.util to JAVA.sql
            //Date date = subIFDDirectory.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);
            String expTime = subIFDDescriptor.getExposureTimeDescription();
            String maker = IFD0Descriptor.getDescription(271);
            String model = IFD0Descriptor.getDescription(272);

            //System.out.println(date);
            System.out.println(expTime);
            System.out.println(maker);
            System.out.println(model);

            // set picture presentation model to added picture
            Picture_PM picture_pm = bl.addNewPicture(picName,/*date,*/expTime,maker,model);
            return picture_pm;
        } catch(IOException ioe) { //TODO - change Exception to custom created DALException
            BLLogger.error(ioe.getMessage());
        }
        // should never occur
        return null;
    }

    //TODO add load picture into view-function from mainController here
}
