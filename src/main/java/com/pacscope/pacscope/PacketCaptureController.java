package com.pacscope.pacscope;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.pcap4j.core.*;
import org.pcap4j.packet.Packet;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class PacketCaptureController implements Initializable {
    private Stage primaryStage;
    private static PcapNetworkInterface selectedInterface;
    private static Packet selectedPacket;
    private volatile boolean capturing;
    @FXML
    private ListView<String> packetCaptureField;
    private static List<Packet> packetList = new ArrayList<>();
    private static ObservableList<String> items = FXCollections.observableArrayList();
    public void saveFile(KeyEvent event){
        if(event.getCode().equals(KeyCode.S) && event.isControlDown()) {
            capturing = false;
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save PCAP File");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("PCAP Files", "*.pcap"),
                    new FileChooser.ExtensionFilter("PCAPNG Files", "*.pcapng")
            );
            fileChooser.setInitialFileName("capture.pcap");
            File file = fileChooser.showSaveDialog(primaryStage);
            if (file != null) {
                PcapHandle handle = null;
                PcapDumper dumper = null;
                try {
                    handle = selectedInterface.openLive(65536, PcapNetworkInterface.PromiscuousMode.PROMISCUOUS, 10);
                    dumper = handle.dumpOpen(file.getAbsolutePath());
                        for (Packet packet : packetList) {
                            dumper.dump(packet);
                        }
                    } catch (PcapNativeException |NotOpenException e) {
                        e.printStackTrace();
                }finally {
                    if (handle != null && handle.isOpen()) {
                        handle.close();
                    }
                    if (dumper != null) {
                        dumper.close();
                    }
                }
            }
        }
    }
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
        while(capturing){
           try{
               assert handle != null;
               packet = handle.getNextPacket();
              }catch (Exception e){
                e.printStackTrace();
           }
           if(packet!=null){
               Packet finalPacket = packet;
               packetList.add(packet);
               Platform.runLater(()->{
                     items.add(finalPacket.getHeader().toString());
                });
               packetCaptureField.setItems(items);
           }
       }
    }
    @FXML
    public void displayPacket() throws IOException {
        int index = packetCaptureField.getSelectionModel().getSelectedIndex();
        if(index > 0){
            selectedPacket = packetList.get(index-1);
        }
        FXMLLoader loader = new FXMLLoader(getClass().getResource("packet-display.fxml"));
        Parent root = loader.load();
        PacketDisplayController packetDisplayController = loader.getController();
        packetDisplayController.setPrimaryStage(primaryStage);
        Scene scene = new Scene(root, 600, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        capturing = true;
        selectedInterface = InterfaceSelectController.getSelectedInterface();
        new Thread(this::startLiveCapture).start();
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }
    public static Packet getSelectedPacket(){
        return selectedPacket;
    }
}
