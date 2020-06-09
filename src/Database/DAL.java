package Database;

import Models.Picture;

import java.util.List;

public interface DAL {
    void initialize();
    List<Picture> getPictures();
    //List<Photographer> getPhotographers(); TODO add photographer class in Models

}
