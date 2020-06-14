package main.Database;

import main.Models.Picture;

import java.util.List;

public interface DAL {
    void initialize();
    List<Picture> getPictures();
    //List<Photographer> getPhotographers(); TODO add photographer class in Models

    Picture getPicture(String name) throws Exception;
    Picture getPicture(int ID);
    void save(Picture p);
    void delete(Picture p);
}
