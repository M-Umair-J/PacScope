package com.pacscope.pacscope;

import javafx.application.HostServices;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToolBar;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import java.io.IOException;

public class AboutController {
    @FXML
    private VBox vbox;
    @FXML
    private ImageView logo;
    @FXML
    private ToolBar toolbar;
    @FXML
    private ImageView back;
    @FXML
    private ScrollPane scrollpane;
    @FXML
    private TextFlow textflow;

    private HostServices hostServices;
    private Stage primaryStage;
    public void setPrimaryStage(Stage primaryStage){
        this.primaryStage = primaryStage;
        setDynamicComponents();
    }
    public void setHostServices(HostServices hostServices){
        this.hostServices = hostServices;
    }

    private void setDynamicComponents(){
        vbox.prefHeightProperty().bind(primaryStage.getScene().heightProperty());
        vbox.prefWidthProperty().bind(primaryStage.getScene().widthProperty());
        toolbar.prefHeightProperty().bind(vbox.heightProperty().multiply(0.1));
        scrollpane.prefHeightProperty().bind(vbox.heightProperty().multiply(0.75));
        logo.fitHeightProperty().bind(vbox.heightProperty().multiply(0.15));
        back.fitHeightProperty().bind(toolbar.prefHeightProperty().multiply(0.9));
        back.fitWidthProperty().bind(back.fitHeightProperty());
        textflow.prefHeightProperty().bind(scrollpane.heightProperty());
        textflow.prefWidthProperty().bind(scrollpane.widthProperty());
        setContent();
    }

    private void setContent(){
        Text mainHeading = new Text("\nAbout this Application\n");
        mainHeading.setStyle("-fx-font-size: 25px; -fx-text-fill: #2c2e33; -fx-font-family: Consolas; -fx-font-weight: bold;");
        Text pacscope = new Text("PacScope");
        pacscope.setStyle("-fx-font-size: 16px; -fx-text-fill: #000000; -fx-font-family: Consolas; -fx-font-weight: bold;");
        Text briefIntro = new Text(" is a Network Analyzing tool developed using JavaFx that can capture live traffic and read previously captured traffic.\n\n");
        briefIntro.setStyle("-fx-font-size: 16px; -fx-text-fill: #000000; -fx-font-family: Consolas;");
        Text featuresHeading = new Text("\nFeatures\n");
        featuresHeading.setStyle("-fx-font-size: 20px; -fx-text-fill: #000000; -fx-font-family: Consolas; -fx-font-weight: bold;");
        Text features = new Text("""
                Live Traffic Capture on Various Interfaces
                Filtering Traffic Based on Protocols
                Reading and Writing to Packet Capture files
                User Friendly Interface
                
                
                """);
        features.setStyle("-fx-font-size: 16px; -fx-text-fill: #000000; -fx-font-family: Consolas;");
        Text technologiesUsedHeading = new Text("Technologies Used\n");
        technologiesUsedHeading.setStyle("-fx-font-size: 20px; -fx-text-fill: #000000; -fx-font-family: Consolas; -fx-font-weight: bold;");
        Text javafx = new Text("JavaFx");
        javafx.setStyle("-fx-font-size: 16px; -fx-text-fill: #000000; -fx-font-family: Consolas; -fx-font-weight: bold;");
        Text userInterface = new Text(" and Css for User Interface\n");
        userInterface.setStyle("-fx-font-size: 16px; -fx-text-fill: #000000; -fx-font-family: Consolas;");
        Text pcap4j = new Text("Pcap4J");
        pcap4j.setStyle("-fx-font-size: 16px; -fx-text-fill: #000000; -fx-font-family: Consolas; -fx-font-weight: bold;");
        Text packetCapture = new Text(" Library for Packet Capture\n\n\n");
        packetCapture.setStyle("-fx-font-size: 16px; -fx-text-fill: #000000; -fx-font-family: Consolas;");
        Text developedBy = new Text("Developed By\n");
        developedBy.setStyle("-fx-font-size: 20px; -fx-text-fill: #000000; -fx-font-family: Consolas; -fx-font-weight: bold;");
        Text name = new Text("Muhammad Umair Javed Khawaja\n\n");
        name.setStyle("-fx-font-size: 16px; -fx-text-fill: #000000; -fx-font-family: Consolas;");
        Text github = new Text("Github ");
        github.setStyle("-fx-font-size: 16px; -fx-text-fill: #000000; -fx-font-family: Consolas; -fx-font-weight: bold;");
        Hyperlink linkGithub = new Hyperlink("Link");
        linkGithub.setOnAction(event -> {
            if(hostServices!=null){
                hostServices.showDocument("https://github.com/M-Umair-J");
            }
        });
        linkGithub.setStyle("fx-text-fill: blue; -fx-font-size: 18px;");
        Text email = new Text("\nEmail ");
        email.setStyle("-fx-font-size: 16px; -fx-text-fill: #000000; -fx-font-family: Consolas; -fx-font-weight: bold;");
        Text mail = new Text("umairkh2185@gmail.com\n\n");
        mail.setStyle("-fx-font-size: 16px; -fx-text-fill: #000000; -fx-font-family: Consolas;");
        textflow.getChildren().addAll(mainHeading,pacscope,briefIntro,featuresHeading,features,technologiesUsedHeading,javafx,userInterface,pcap4j,packetCapture,developedBy,name,github,linkGithub,email,mail);
    }
    @FXML
    private void goBack() {
        try{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/pacscope/pacscope/main-screen.fxml"));
        Parent root = loader.load();
        MainController mainController = loader.getController();
        mainController.setPrimaryStage(primaryStage);
        Scene scene = new Scene(root, primaryStage.getScene().getWidth(), primaryStage.getScene().getHeight());
        primaryStage.setScene(scene);
        primaryStage.show();
    } catch (
    IOException e) {
        System.out.println("Failed to load the main screen");
    }
    }
}
