package main.PresentationModels;

import main.Models.Picture;

public class MainWindowPM {
    private PhotographerList_PM photographerListPm;
    private Picture_PM currPicturePm;
    private PictureList_PM pictureListPm;
    //TODO add missing search viewModel

    public MainWindowPM() {
        this.photographerListPm = new PhotographerList_PM();
        this.currPicturePm = new Picture_PM(new Picture());
        this.pictureListPm = new PictureList_PM();
    }

    public PhotographerList_PM getPhotographerListPm() { return this.photographerListPm; }
    public void setPhotographerListPm(PhotographerList_PM photographerListPm) { this.photographerListPm = photographerListPm; }

    public Picture_PM getCurrPicturePm() { return this.currPicturePm; }
    public void setCurrPicturePm(Picture_PM picturePm) { this.currPicturePm = picturePm; }

    public PictureList_PM getPictureListPm() { return this.pictureListPm; }
    public void setPictureListPm(PictureList_PM pictureListPm) { this.pictureListPm = pictureListPm; }

}
