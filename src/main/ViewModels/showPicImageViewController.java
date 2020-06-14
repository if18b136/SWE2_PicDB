package main.ViewModels;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class showPicImageViewController {

    @FXML
    private ImageView imageView;

    public void setImage(String name) {
        imageView.setImage(new Image(name, 300, 300, true, true, true));
    }

    @FXML
    public void initialize() {

    }
}
