package com.pacscope.pacscope;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import org.pcap4j.packet.Packet;
import java.net.URL;
import java.util.ResourceBundle;

public class PacketDisplayController implements Initializable {
    @FXML
    private VBox vbox;
    @FXML
    private TextArea packetArea;
    public static Packet packet;
    public  void displayPacket(){
        packetArea.prefHeightProperty().bind(vbox.heightProperty());
        packetArea.prefWidthProperty().bind(vbox.widthProperty());
        packetArea.setStyle("-fx-text-fill: #2C2E33;");
        packetArea.setText(packet.toString());
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        displayPacket();
    }
}
