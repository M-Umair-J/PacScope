package com.pacscope.pacscope;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import java.io.IOException;
import java.util.Objects;

public class MainScreen extends Application {
    private Stage primaryStage;
    @Override
    public void start(Stage stage) throws IOException {
        this.primaryStage = stage;
        Parent splashScreen = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/pacscope/pacscope/splash-screen.fxml")));
        Scene splashScene = new Scene(splashScreen, 600, 600);
        stage.setScene(splashScene);
        stage.setMinHeight(400);
        stage.setMinWidth(400);
        stage.setResizable(true);
        stage.show();
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/pacscope/pacscope/icon.png"))));
        new Thread(() -> {
            try {
                Thread.sleep(3000);
                Platform.runLater(() ->{
                    try {
                        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/com/pacscope/pacscope/main-screen.fxml")));
                        Parent main = loader.load();
                        MainController mainController = loader.getController();
                        mainController.setPrimaryStage(stage);
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