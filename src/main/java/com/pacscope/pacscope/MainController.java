package com.pacscope.pacscope;

import javafx.fxml.FXML;
import javafx.stage.FileChooser;
import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.Pcaps;
import org.pcap4j.packet.Packet;

import java.io.File;

public class MainController {

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
    protected void liveCaptureButtonClick() {
        System.out.println("Live Capture is not available yet.");
    }
}