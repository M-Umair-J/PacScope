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
import javafx.stage.Stage;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.core.PcapNetworkInterface;
import org.pcap4j.core.Pcaps;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class InterfaceSelectController implements Initializable {
    private Stage primaryStage;
    private static PcapNetworkInterface pcapNetworkInterface;
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }
    private List<PcapNetworkInterface> allDevs;
    @FXML
    private ListView<String> packetDisplayField;
    public void interfaceSelect() {

        try {
            allDevs = Pcaps.findAllDevs();
        } catch (PcapNativeException e) {
            throw new RuntimeException(e);
        }
        if(allDevs.isEmpty()){
            System.out.println("No network interfaces found.");
            return;
        }
        ObservableList<String> items = FXCollections.observableArrayList();
        System.out.println("Available Network Interfaces:");
        List<PcapNetworkInterface> findAllDevs = allDevs;
        Platform.runLater(()->{
            items.add("Available Network Interfaces:\n");
            int i = 1;
            for(PcapNetworkInterface dev : findAllDevs){

                items.add(i + " : " + dev.getDescription()+"\n");
                i++;
            }
            packetDisplayField.setItems(items);
        });
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        new Thread(this::interfaceSelect).start();
    }
    @FXML
    public void handleInterfaceClick() throws IOException {
        int index = packetDisplayField.getSelectionModel().getSelectedIndex();
        if(index>0){
            pcapNetworkInterface = allDevs.get(index-1);
        }
        FXMLLoader loader = new FXMLLoader(getClass().getResource("packet-capture.fxml"));
        Parent root = loader.load();

        // Pass the primaryStage to the PacketCaptureController if needed
        PacketCaptureController controller = loader.getController();
        controller.setPrimaryStage(primaryStage);

        Scene scene = new Scene(root, 600, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    static PcapNetworkInterface getSelectedInterface(){
        return pcapNetworkInterface;
    }
}
