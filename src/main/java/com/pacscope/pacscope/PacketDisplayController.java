package com.pacscope.pacscope;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import org.pcap4j.packet.Packet;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class PacketDisplayController implements Initializable {
    private Stage primaryStage;
    private Packet packet;
    public static Object reference;
    @FXML
    private TextArea packetArea;
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }
    public  void displayPacket(){
        packetArea.setText(packet.toString());
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if(reference instanceof PacketCaptureController){
            packet = PacketCaptureController.getSelectedPacket();
        }
        else if(reference instanceof OpenCaptureFile){
            packet = OpenCaptureFile.getSelectedPacket();
        }
        displayPacket();
    }
    @FXML
    public void goBack() throws IOException {
        if(reference instanceof PacketCaptureController) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("packet-capture.fxml"));
            Parent root = loader.load();
            PacketCaptureController packetCaptureController = loader.getController();
            packetCaptureController.setPrimaryStage(primaryStage);
            Scene scene = new Scene(root, 600, 600);
            primaryStage.setScene(scene);primaryStage.setScene(scene);
        }
        else if (reference instanceof OpenCaptureFile){
            OpenCaptureFile.opening = false;
            FXMLLoader loader = new FXMLLoader(getClass().getResource("open-capture-file.fxml"));
            Parent root = loader.load();
            OpenCaptureFile openCaptureFile = loader.getController();
            openCaptureFile.setPrimaryStage(primaryStage);
            Scene scene = new Scene(root, 600, 600);
            primaryStage.setScene(scene);
        }
        primaryStage.show();
    }
}
