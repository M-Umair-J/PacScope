package com.pacscope.pacscope;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import org.pcap4j.packet.Packet;

public class PacketDisplayController{
    @FXML
    private VBox vbox;
    @FXML
    private TextArea packetArea;
    public static Packet packet;
    public void setPacket(Packet packet1){
        packet = packet1;
        displayPacket();
    }
    public  void displayPacket(){
        packetArea.prefHeightProperty().bind(vbox.heightProperty());
        packetArea.prefWidthProperty().bind(vbox.widthProperty());
        packetArea.setStyle("-fx-text-fill: #2C2E33;");
        packetArea.setText(packet.toString());
    }
}
