package com.pacscope.pacscope;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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
import java.util.ArrayList;
import java.util.List;

public class PacketCaptureController {
    private Stage primaryStage;
    private static PcapNetworkInterface selectedInterface;
    private static Packet selectedPacket;
    private volatile boolean capturing;
    private Thread thread;
    @FXML
    private ListView<String> packetCaptureField;
    private static final List<Packet> packetList = new ArrayList<>();
    private static final ObservableList<String> items = FXCollections.observableArrayList();

    public PacketCaptureController(){
        capturing = true;
        selectedInterface = InterfaceSelectController.getSelectedInterface();
        if(capturing){
            thread = new Thread(this::startLiveCapture);
            thread.start();
        }
    }

    public void endCapture(){
        capturing = false;
        if (thread != null && thread.isAlive()) {
            System.out.println("Thread closed");
            thread.interrupt();
            thread = null;
        }
        saveCaptureToFile();
        goBackToMainScreen();
    }

    public void saveFile(KeyEvent event){
        if(event.getCode().equals(KeyCode.S) && event.isControlDown()) {
            capturing = false;
            if (thread != null && thread.isAlive()) {
                System.out.println("Thread closed");
                thread.interrupt();
                thread = null;
            }
            saveCaptureToFile();
            goBackToMainScreen();
        }
    }

    private synchronized void saveCaptureToFile() {
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
                synchronized (packetList) {
                    for (Packet packet : packetList) {
                        dumper.dump(packet);
                    }
                }
            } catch (PcapNativeException | NotOpenException e) {
                e.printStackTrace();
            } finally {
                if (handle != null && handle.isOpen()) {
                    handle.close();
                }
                if (dumper != null) {
                    dumper.close();
                }
            }
        }
    }

    private void goBackToMainScreen() {
        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/pacscope/pacscope/main-screen.fxml"));
                Parent root = loader.load();
                MainController mainController = loader.getController();
                mainController.setPrimaryStage(primaryStage);
                Scene scene = new Scene(root, 600, 600);
                primaryStage.setScene(scene);
                primaryStage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void startLiveCapture() {
        int snapLength = 65536;
        PcapNetworkInterface.PromiscuousMode mode = PcapNetworkInterface.PromiscuousMode.PROMISCUOUS;
        int timeout = 10;

        PcapHandle handle = null;

        try {
            handle = selectedInterface.openLive(snapLength, mode, timeout);
            Packet packet = null;
            while (capturing && !Thread.currentThread().isInterrupted()) {
                try {
                    packet = handle.getNextPacket();
                } catch (NotOpenException e) {
                    e.printStackTrace();
                }
                if (packet != null) {
                    Packet finalPacket = packet;
                    synchronized (packetList) {
                        packetList.add(packet);
                    }
                    Platform.runLater(() -> items.add(finalPacket.getHeader().toString()));
                    packetCaptureField.setItems(items);
                }
            }
        } catch (PcapNativeException e) {
            throw new RuntimeException(e);
        } finally {
            if (handle != null && handle.isOpen()) {
                handle.close();
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
        Scene scene = new Scene(root, 600, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public static Packet getSelectedPacket(){
        return selectedPacket;
    }
}