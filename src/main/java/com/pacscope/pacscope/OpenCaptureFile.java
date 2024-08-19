package com.pacscope.pacscope;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.Pcaps;
import org.pcap4j.packet.Packet;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OpenCaptureFile{
    private static final List<Packet> packetList = new ArrayList<>();
    public static boolean opening = true;
    Stage primaryStage;
    private Thread thread;
    private File file;
    @FXML
    private ListView<String> packetCaptureField;
    private static Packet selectedPacket;

    public void setFile(File file){
        this.file = file;
        if(opening) {
            thread = new Thread(this::openCaptureFilef);
            thread.start();
        }
        else{
            showPackets();
        }
    }
    public void getBackToMainScreen(){
        opening = true;
        if(thread != null && thread.isAlive()){
            thread.interrupt();
            thread = null;
        }
        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/pacscope/pacscope/main-screen.fxml"));
                Parent root = loader.load();
                MainController mainController = loader.getController();
                mainController.setPrimaryStage(primaryStage);
                Scene scene = new Scene(root, primaryStage.getScene().getWidth(), primaryStage.getScene().getHeight());
                primaryStage.setScene(scene);
                primaryStage.show();
            } catch (IOException e) {
                System.out.println("Failed to load the main screen");
            }
        });
    }
    public void displayPacket() throws IOException {
        int index = packetCaptureField.getSelectionModel().getSelectedIndex();
        if(index > 0){
            selectedPacket = packetList.get(index-1);
        }
        PacketDisplayController.reference = this;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("packet-display.fxml"));
        Parent root = loader.load();
        PacketDisplayController packetDisplayController = loader.getController();
        packetDisplayController.setPrimaryStage(primaryStage);
        Scene scene = new Scene(root, 600, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }
    public void openCaptureFilef() {
        Platform.runLater(() -> {

            if (file != null) {
                System.out.println("File selected: " + file.getName());
                try {
                    PcapHandle handle = Pcaps.openOffline(file.getAbsolutePath());
                    System.out.println("File opened successfully.");
                    Packet packet;
                    while ((packet = handle.getNextPacket()) != null) {
                        packetList.add(packet);
                    }
                    handle.close();
                } catch (Exception e) {
                    System.out.println("Error opening file.");
                }
            } else {
                getBackToMainScreen();
            }
            showPackets();
        });
    }
    public void showPackets(){
        Platform.runLater(() -> {
            for(Packet packet: packetList){
                packetCaptureField.getItems().add(packet.getHeader().toString());
            }
        });
    }
    public static Packet getSelectedPacket(){
        return selectedPacket;
    }
}
