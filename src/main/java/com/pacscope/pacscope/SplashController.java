package com.pacscope.pacscope;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Objects;

public class SplashController {
    @FXML
    ImageView splashImage;

    public void initialize(){
        Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/pacscope/pacscope/Splash.png")));
        splashImage.setImage(image);
    }
}
