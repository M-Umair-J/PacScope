package com.pacscope.pacscope;

import javafx.application.Application;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import java.io.IOException;
import java.util.Objects;

public class MainScreen extends Application {
    private static HostServices hostServices;
    private Stage primaryStage;

    @Override
    public void start(Stage stage) throws IOException {
        this.primaryStage = stage;
        Parent splashScreen = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/pacscope/pacscope/splash-screen.fxml")));
        Scene splashScene = new Scene(splashScreen, 600, 600);
        stage.setScene(splashScene);
        stage.setMinHeight(500);
        stage.setMinWidth(500);
        stage.centerOnScreen();
        stage.setResizable(true);
        stage.setMaximized(true);
        stage.setTitle("PacScope");
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
                        mainController.setPrimaryStage(primaryStage);
                        mainController.setHostServices(hostServices);
                        Scene mainScene = new Scene(main,primaryStage.getScene().getWidth(),primaryStage.getScene().getHeight());
                        stage.setScene(mainScene);
                        stage.show();

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });

            } catch (InterruptedException e) {
                e.getLocalizedMessage();
            }
        }).start();
    }

    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void init() {
        hostServices = getHostServices();
    }
}