package main.Database;

import main.Models.EXIF;
import main.Models.IPTC;
import main.Models.Photographer;
import main.Models.Picture;
import main.PresentationModels.Photographer_PM;
import main.PresentationModels.Picture_PM;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DALMock implements DAL{
    @Override
    public void initialize() {}

    @Override
    public Picture getPicture(String name) {
        Picture pic = new Picture();
        pic.setName(name);
        pic.setID(1);   // set to 1 so it is not -1
        return pic;
    }

    @Override
    public List<Photographer_PM> retrievePhotographers() {
        List<Photographer_PM> photographerPmList = new ArrayList<>();
        Photographer_PM photographerPm1 = new Photographer_PM(new Photographer());
        Photographer_PM photographerPm2 = new Photographer_PM(new Photographer());
        Photographer_PM photographerPm3 = new Photographer_PM(new Photographer());
        photographerPmList.add(photographerPm1);
        photographerPmList.add(photographerPm2);
        photographerPmList.add(photographerPm3);
        return photographerPmList;
    }

    @Override
    public Photographer addNewPhotographer(String firstName, String lastName, LocalDate birthday, String notes) {
        Photographer photographer = new Photographer();
        photographer.setID(1);
        photographer.setFirstName(firstName);
        photographer.setLastName(lastName);
        photographer.setBirthDay(birthday);
        photographer.setNotes(notes);
        return photographer;
    }

    @Override
    public Photographer editPhotographer(int ID, String firstName, String lastName, LocalDate birthday, String notes) {
        Photographer photographer = new Photographer();
        photographer.setID(ID);
        photographer.setFirstName(firstName);
        photographer.setLastName(lastName);
        photographer.setBirthDay(birthday);
        photographer.setNotes(notes);
        return photographer;
    }

    @Override
    public void deletePhotographer(int ID) { }

    @Override
    public Picture addNewPicture(String name, String expTime, String maker, String model) {
        Picture pic = new Picture();
        pic.setName(name);
        pic.setID(1);
        pic.setName(name);
        pic.setIPTC(new IPTC());
        EXIF exif1 = new EXIF();
        EXIF exif2 = new EXIF();
        EXIF exif3 = new EXIF();
        exif1.setID(1);
        exif1.setName("expTime");
        exif1.setDescription(expTime);
        exif2.setID(2);
        exif2.setName("maker");
        exif2.setDescription(maker);
        exif3.setID(3);
        exif3.setName("model");
        exif3.setDescription(model);
        List<EXIF> exifList = new ArrayList<>();
        exifList.add(exif1);
        exifList.add(exif2);
        exifList.add(exif3);
        pic.setExifList(exifList);
        return pic;
    }

    @Override
    public HashMap<Integer, String> getAllPictureNames() {
        HashMap<Integer, String> pictureNames = new HashMap<>();
        pictureNames.put(1,"Cat");
        pictureNames.put(2,"Dog");
        pictureNames.put(3,"Chicken");
        return pictureNames;
    }

    @Override
    public Picture_PM createPictureModel(int id, String name) {
        Picture picture = addNewPicture(name, "1/100","CANON","XPS 100");
        picture.setID(id);
        return new Picture_PM(picture);
    }

    @Override
    public void addIptc(int picID, String type, String value) { }

    @Override
    public void updateIptc(int iptcID, String value) { }

    @Override
    public void assignPhotographer(int picID, String lastName) { }

    @Override
    public void assignPhotographer(int picID, String firstName, String lastName) { }
}
