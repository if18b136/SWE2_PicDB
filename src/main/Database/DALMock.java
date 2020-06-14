package main.Database;

import main.Models.Picture;

import java.util.List;

public class DALMock implements DAL{
    @Override
    public void initialize() {

    }

    @Override
    public List<Picture> getPictures() {
        return null;
    }

    @Override
    public Picture getPicture(String name) {
        return null;
    }

    @Override
    public Picture getPicture(int ID) {
        return null;
    }

    @Override
    public void save(Picture p) {

    }

    @Override
    public void delete(Picture p) {

    }
}
