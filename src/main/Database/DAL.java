package main.Database;

import main.Models.Picture;
import main.PresentationModels.Photographer_PM;
import main.PresentationModels.Picture_PM;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

public interface DAL {
    void initialize();
    //List<Photographer> getPhotographers(); TODO add photographer class in Models
    Picture getPicture(String name) throws Exception;
    List<Photographer_PM> retrievePhotographers() throws Exception;
    void addNewPhotographer(String firstName, String lastName, LocalDate birthday, String notes) throws Exception;
    void editPhotographer(int ID, String firstName, String lastName, LocalDate birthday, String notes) throws Exception;
    void deletePhotographer(int ID) throws Exception;
    Picture addNewPicture(String name/*, Date date*/, String expTime, String maker, String model) throws Exception;
    HashMap<Integer, String> getAllPictureNames() throws Exception;
    Picture_PM createPictureModel(int id, String name) throws Exception;
    void addIptc(int picID, String type, String value) throws Exception;
    void updateIptc(int iptcID, String value) throws Exception;
    void assignPhotographer(int picID, String lastName) throws Exception;
    void assignPhotographer(int picID, String firstName, String lastName) throws Exception;
}
