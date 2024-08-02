package com.pacscope.pacscope;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.Pcaps;
import org.pcap4j.packet.Packet;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class MainController {
    private Stage primaryStage;
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }
    @FXML
    protected void onFileButtonClick() {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open pcap File");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("PCAP Files", "*.pcap"),
                new FileChooser.ExtensionFilter("PCAPNG", "*.pcapng*"),
                new FileChooser.ExtensionFilter("All Files", "*.*"));
        File file = fileChooser.showOpenDialog(null);
        if(file!=null){
            System.out.println("File selected: " + file.getName());
            try{
                PcapHandle handle = Pcaps.openOffline(file.getAbsolutePath());
                System.out.println("File opened successfully.");
                Packet packet;
                while((packet = handle.getNextPacket()) != null){
                    System.out.println(packet);
                }
                handle.close();
            }

            catch(Exception e){
                System.out.println("Error opening file.");
            }
        }
        else{
            System.out.println("No file selected.");
        }


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
        Scene mainScene = new Scene(main, 600, 600);
        primaryStage.setScene(mainScene);
        primaryStage.show();
    }


}