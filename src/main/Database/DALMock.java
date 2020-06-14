package main.Database;

import main.Models.EXIF;
import main.Models.Picture;
import main.PresentationModels.Picture_PM;

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
    public Picture addNewPicture(String name, String expTime, String maker, String model) throws Exception {
        Picture pic = new Picture();
        pic.setName(name);
        pic.setID(1);
        return null;

    }

    @Override
    public HashMap<Integer, String> getAllPictureNames() throws Exception {
        return null;
    }

    @Override
    public Picture_PM createPictureModel(int id, String name) throws Exception {
        return null;
    }

    @Override
    public void addIptc(int picID, String type, String value) throws Exception {

    }

    @Override
    public void updateIptc(int iptcID, String value) throws Exception {

    }

    @Override
    public void assignPhotographer(int picID, String lastName) throws Exception {

    }

    @Override
    public void assignPhotographer(int picID, String firstName, String lastName) throws Exception {

    }
}
