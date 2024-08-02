package com.pacscope.pacscope;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Objects;

public class MainScreen extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        Parent splashScreen = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/pacscope/pacscope/splash-screen.fxml")));
        Scene splashScene = new Scene(splashScreen, 600, 600);
        stage.setScene(splashScene);
        stage.setResizable(false);
        stage.show();
        new Thread(() -> {
            try {
                Thread.sleep(3000);
                Platform.runLater(() ->{
                    try {
                        Parent main = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/pacscope/pacscope/main-screen.fxml")));
                        Scene mainScene = new Scene(main, 600, 600);
                        stage.setScene(mainScene);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}