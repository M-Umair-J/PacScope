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
        packet = PacketCaptureController.getSelectedPacket();
//        new Thread(this::displayPacket).start();
        displayPacket();
    }
    @FXML
    public void goBackToCapture() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("packet-capture.fxml"));
        Parent root = loader.load();
        PacketCaptureController packetCaptureController = loader.getController();
        packetCaptureController.setPrimaryStage(primaryStage);
        Scene scene = new Scene(root, 600, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

}
