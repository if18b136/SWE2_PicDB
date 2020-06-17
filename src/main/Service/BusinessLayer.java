package main.Service;

import main.Database.DAL;
import main.Database.DALFactory;
import main.Database.DataAccessLayer;
import main.Models.Picture;
import main.PresentationModels.Photographer_PM;
import main.PresentationModels.Picture_PM;
import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;

public class BusinessLayer {
    final Logger BLLogger = LogManager.getLogger("Business Layer");
    private HashMap<Integer,String> picList = new HashMap<>(); // currently loading pictures and then creating the models afterwards seems simpler than already creating an PM list in the DatabaseAccess-layer
    private List<Picture_PM> picPmList = new ArrayList<>();
    private static BusinessLayer bl = new BusinessLayer();
    private String path = "";

    private BusinessLayer() {
        //get path from config
        try{
            Config config = Config.getInstance();
            this.path = config.getProperties().getProperty("path");
            System.out.println(config.getProperties().getProperty("path"));
            Locale.setDefault(Locale.GERMANY);  //datepicker in american format because of my local language
        } catch (Exception e) {
            BLLogger.error(e.getMessage());
        }
    }

    public String getPath() { return path; }

    public static BusinessLayer getInstance() {
        return bl;
    }

    public void initPicNameList() {
        try{
            DAL dal =  DALFactory.getDAL();
            this.picList = new HashMap<>();
            picList = dal.getAllPictureNames(); // return list of finished Picture_PMs would be the most direct way
        } catch (Exception e) {
            BLLogger.error(e.getMessage());
        }
    }

    public List<Photographer_PM> createPhotographerList() {
        try{
            DAL dal =  DALFactory.getDAL();
            return dal.retrievePhotographers();
        } catch (Exception e) {
            BLLogger.error(e.getMessage());
        }
        BLLogger.info("Something went wrong with PhotographerList creation.");
        return null;
    }

    public boolean validatePhotographer(String firstName, String lastName, LocalDate birthday, String notes) {
        if(firstName != null && lastName != null && birthday != null && notes != null) {    // first validation layer
            return firstName.length() <= 100 && lastName.length() > 0 && lastName.length() <= 50 && birthday.compareTo(LocalDate.now()) < 0;    //simplified by Intellij
        }
        return false;
    }

    //TODO - wouldn't it be better to create a Photographer model from the info and use it instead of 4 values?
    public boolean addNewPhotographer(String firstName, String lastName, LocalDate birthday, String notes) {
        try{
           if(validatePhotographer(firstName,lastName,birthday,notes)) {
                DAL dal = DALFactory.getDAL();
                dal.addNewPhotographer(firstName,lastName,birthday,notes);
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            BLLogger.error(e.getMessage());
        }
        return false;
    }

    public boolean editPhotographer(int ID, String firstName, String lastName, LocalDate birthday, String notes) {
        try{
            if(validatePhotographer(firstName,lastName,birthday,notes)) {
                DAL dal = DALFactory.getDAL();
                dal.editPhotographer(ID,firstName,lastName,birthday,notes);
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            BLLogger.error(e.getMessage());
        }
        return false;
    }

    public void deletePhotographer(int ID) {
        try {
            DAL dal = DALFactory.getDAL();
            dal.deletePhotographer(ID);
        }catch (Exception e) {
            BLLogger.error(e.getMessage());
        }
    }

    // complete list of currently existing pictures - turned into PresentationModels
    public void createPicList() {
        try{
            DAL dal = DALFactory.getDAL();
            this.picPmList = new ArrayList<>(); // delete old entries - create new ones - probably should not make a private callable list in business layer
            for(Map.Entry<Integer,String> pic : picList.entrySet()) {
                Picture_PM picture_pm = dal.createPictureModel(pic.getKey(),pic.getValue());
                picPmList.add(picture_pm);
            }
        } catch (Exception e) {
            BLLogger.error(e.getMessage());
        }
    }

    public List<Picture_PM> getPicList() {
        return picPmList;
    }

    // call DAL, add new Picture, return presentation model of new picture
    public Picture_PM addNewPicture(String name/*, Date date*/, String expTime, String maker, String model) throws Exception {
        DAL dal =  DALFactory.getDAL();
        // give DAL the db data, return a picture presentation model containing the data
        Picture newPic = dal.addNewPicture(name,/*date,*/expTime,maker,model);
        // picture PM also needs metadata models from picture
        Picture_PM newPicPM = new Picture_PM(newPic);
        picPmList.add(newPicPM);
        return newPicPM;
    }

    public Picture_PM extractMetadata(File selectedFile, Path src) {
        try{
            String picName = selectedFile.getName();
            Path dest = Paths.get(this.path + picName);
            Files.copy(src,dest);
            Metadata metadata = ImageMetadataReader.readMetadata(selectedFile);

            // obtain the Exif directory
            ExifSubIFDDirectory subIFDDirectory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
            ExifIFD0Directory IFD0Directory = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
//            ExifImageDirectory ImageDirectory = metadata.getFirstDirectoryOfType(ExifImageDirectory.class);

            // create a descriptor
            ExifSubIFDDescriptor subIFDDescriptor = new ExifSubIFDDescriptor(subIFDDirectory);
            ExifIFD0Descriptor IFD0Descriptor = new ExifIFD0Descriptor(IFD0Directory);

            // Date only works if there is a date within the EXIF data, otherwise it will result in a NullPointerException
            // TODO Add Date as EXIF value
//            LocalDate date = subIFDDirectory.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();;
            String expTime = subIFDDescriptor.getExposureTimeDescription();
            String maker = IFD0Descriptor.getDescription(271);
            String model = IFD0Descriptor.getDescription(272);
//            System.out.println(date);
//            System.out.println(expTime);
//            System.out.println(maker);
//            System.out.println(model);

            // set picture presentation model to added picture
            return bl.addNewPicture(picName,/*date,*/expTime,maker,model);
        } catch(Exception e) {
            BLLogger.error(e.getMessage());
        }
        // should never occur
        return null;
    }

    public void updateIptc(String copyright, String tagList, Picture_PM curPicPm) {
        //first evaluate changes and set them
//        if(!photographer.equals(curPicPm.getIptc().getPhotographer())) {
//            curPicPm.getIptc().setPhotographer(photographer);
//        }
        if(!copyright.equals(curPicPm.getIptc().getCopyright())) {
            curPicPm.getIptc().setCopyright(copyright);
        }
        if(!tagList.equals(curPicPm.getIptc().getTagList())) {
            curPicPm.getIptc().setTagList(tagList);
        }
        changeIptcData(curPicPm);
    }

    public void changeIptcData(Picture_PM picPM) {
        try{
            //TODO rules for invalid updates
            //look  for existing iptc database entries
            DAL dal =  DALFactory.getDAL();
//            if(picPM.getIptc().getPhotographerID() == -1) {
//                if(validPhotographer(picPM.getID(),picPM.getIptc().getPhotographer())) {
//                    dal.addIptc(picPM.getID(),"Photographer",picPM.getIptc().getPhotographer());
//                }
//            } else {
//                dal.updateIptc(picPM.getIptc().getPhotographerID(),picPM.getIptc().getPhotographer());
//            }
            if(picPM.getIptc().getCopyrightID() == -1) {
                dal.addIptc(picPM.getID(),"Copyright",picPM.getIptc().getCopyright());
            } else {
                dal.updateIptc(picPM.getIptc().getCopyrightID(),picPM.getIptc().getCopyright());
            }
//            TagListID is not needed anymore because we create the list from single values from tag table in database
            dal.assignTagsToPic(picPM.getID(),picPM.getIptc().getTagList());

        } catch (Exception e) {
            BLLogger.error(e.getMessage());
        }
    }

    public boolean updateAssignment(String firstName, String lastName, Picture_PM curPicPm) {
        try{
            if(validatePhotographer(firstName,lastName,curPicPm.getPhotographer().getBirthDay(), curPicPm.getPhotographer().getNotes())){
                DAL dal =  DALFactory.getDAL();
                return dal.assignPhotographer(curPicPm.getID(), firstName, lastName);
            }
            return false;
       } catch (Exception e) {
            BLLogger.error(e.getMessage());
        }
        return false;
    }

    public HashMap<String,Integer> getTagMap() {
        try{
            DAL dal = DALFactory.getDAL();
            return dal.getTagMap();
        } catch (Exception e) {
            BLLogger.error(e.getMessage());
        }
        return null;
    }
}
