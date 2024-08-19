package com.pacscope.pacscope;

import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

public class SplashController {
    @FXML
    private ImageView splashImage;
    @FXML
    private StackPane splashPane;

    public void initialize(){
        splashImage.fitHeightProperty().bind(splashPane.heightProperty().multiply(0.7));
        splashImage.fitWidthProperty().bind(splashPane.widthProperty().multiply(0.7));
    }
}
