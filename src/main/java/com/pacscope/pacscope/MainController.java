package com.pacscope.pacscope;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Objects;

public class MainController {
    private Stage primaryStage;
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }
    @FXML
    protected void onFileButtonClick() throws IOException {
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("open-capture-file.fxml")));
        Parent main  = loader.load();
        OpenCaptureFile openCaptureFile = loader.getController();
        openCaptureFile.setPrimaryStage(primaryStage);
        Scene mainScene = new Scene(main, 600, 600);
        primaryStage.setScene(mainScene);
        primaryStage.show();
    }
    @FXML
    protected void onAboutButtonClick() {
        System.out.println("Pacscope is a network packet capture and analysis tool.");
    }
    @FXML
    protected void onHelpButtonClick() {
        System.out.println("Please contact Pacscope for help.");
    }
    @FXML
    protected void liveCaptureButtonClick() throws IOException {
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("interface-select.fxml")));
        Parent main = loader.load();
        InterfaceSelectController interfaceSelectController = loader.getController();
        interfaceSelectController.setPrimaryStage(primaryStage);
        Scene mainScene = new Scene(main, 600, 600);
        primaryStage.setScene(mainScene);
        primaryStage.show();
    }


}