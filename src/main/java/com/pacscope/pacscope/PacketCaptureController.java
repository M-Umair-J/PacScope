package com.pacscope.pacscope;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.PcapNetworkInterface;
import org.pcap4j.packet.Packet;

import java.net.URL;
import java.util.ResourceBundle;

public class PacketCaptureController implements Initializable {
    private Stage primaryStage;

    private static PcapNetworkInterface selectedInterface;

    @FXML
    private ListView<String> packetCaptureField;
    private ObservableList<String> items = FXCollections.observableArrayList();
    public void startLiveCapture() {
        int snapLength = 65536;
        PcapNetworkInterface.PromiscuousMode mode = PcapNetworkInterface.PromiscuousMode.PROMISCUOUS;
        int timeout = 10;

        PcapHandle handle = null;

        try {
            handle = selectedInterface.openLive(snapLength, mode, timeout);
        } catch (Exception e) {
            e.printStackTrace();
        }
            Packet packet = null;
        while(true){
           try{
               assert handle != null;
               packet = handle.getNextPacket();
              }catch (Exception e){
                e.printStackTrace();
           }
           if(packet!=null){
               Packet finalPacket = packet;

               Platform.runLater(()->{
                     items.add(finalPacket.getHeader().toString());
                });
               packetCaptureField.setItems(items);
           }
       }
    }
    public void displayPacket(){}

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        selectedInterface = InterfaceSelectController.getSelectedInterface();
        new Thread(this::startLiveCapture).start();
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }
}
