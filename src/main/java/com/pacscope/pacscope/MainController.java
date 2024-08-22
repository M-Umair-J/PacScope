package com.pacscope.pacscope;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.*;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class MainController implements Initializable {
    @FXML
    private VBox vbox;
    @FXML
    private ImageView title;

    private Stage primaryStage;
    public void setPrimaryStage(Stage primaryStage) {

        this.primaryStage = primaryStage;
    }
    @FXML
    protected void onFileButtonClick() throws IOException{
        File file = fileOpener();
        if(file == null){
            return;
        }

        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("open-capture-file.fxml")));
        Parent main  = loader.load();
        OpenCaptureFile openCaptureFile = loader.getController();
        openCaptureFile.setPrimaryStage(primaryStage);
        openCaptureFile.setFile(file);
        Scene mainScene = new Scene(main, primaryStage.getScene().getWidth(), primaryStage.getScene().getHeight());
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
        Scene mainScene = new Scene(main, primaryStage.getScene().getWidth(), primaryStage.getScene().getHeight());
        primaryStage.setScene(mainScene);
        primaryStage.show();
    }

    @Override
    public void initialize(java.net.URL location, java.util.ResourceBundle resources) {
        title.fitHeightProperty().bind(vbox.heightProperty().multiply(0.4));
        title.fitWidthProperty().bind(vbox.widthProperty().multiply(0.4));
    }

    private File fileOpener(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open pcap File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PCAP Files", "*.pcap"),
                new FileChooser.ExtensionFilter("PCAPNG", "*.pcapng*"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );
        return fileChooser.showOpenDialog(primaryStage);
    }
}