package main.Database;

import main.Models.Photographer;
import main.Models.Picture;
import main.PresentationModels.Photographer_PM;
import main.PresentationModels.Picture_PM;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

/**
 * The Data Access Layer Interface.
 * Function descriptions can be found in it's subclass DataAccessLayer.
 */
public interface DAL {
    void initialize();
    Picture getPicture(String name) throws Exception;
    List<Photographer_PM> retrievePhotographers() throws Exception;
    Photographer addNewPhotographer(String firstName, String lastName, LocalDate birthday, String notes) throws Exception;
    Photographer editPhotographer(int ID, String firstName, String lastName, LocalDate birthday, String notes) throws Exception;
    void deletePhotographer(int ID) throws Exception;
    Picture addNewPicture(String name/*, Date date*/, String expTime, String maker, String model) throws Exception;
    HashMap<Integer, String> getAllPictureNames() throws Exception;
    Picture_PM createPictureModel(int id, String name) throws Exception;
    void addIptc(int picID, String type, String value) throws Exception;
    void updateIptc(int iptcID, String value) throws Exception;
    void assignTagsToPic(int ID,List<String> tagList) throws Exception;
    boolean assignPhotographer(int picID, String firstName, String lastName) throws Exception;
    HashMap<String,Integer> getTagMap() throws Exception;
}
