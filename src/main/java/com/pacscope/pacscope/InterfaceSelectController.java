package com.pacscope.pacscope;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.core.PcapNetworkInterface;
import org.pcap4j.core.Pcaps;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class InterfaceSelectController implements Initializable {
    @FXML
    private VBox vbox;
    @FXML
    private ImageView imageView;
    @FXML
    private Button button;


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
        List<PcapNetworkInterface> findAllDevs = allDevs;
        Platform.runLater(()->{
            packetDisplayField.getItems().clear();
            ObservableList<String> items = FXCollections.observableArrayList();
            int i = 1;
            for(PcapNetworkInterface dev : findAllDevs) {
                    if(dev.getDescription() != null && !dev.getDescription().trim().isEmpty()) {
                        items.add(i + " : " + dev.getDescription());
                        i++;
                    }
            }
            packetDisplayField.setItems(items);
        });
    }
    public void initialize(){
        // Bind ImageView to VBox size
        imageView.fitWidthProperty().bind(vbox.widthProperty().multiply(0.4)); // 80% of VBox width
        imageView.fitHeightProperty().bind(vbox.heightProperty().multiply(0.4)); // 40% of VBox height

        // Optionally, bind Button size to VBox size or ImageView size
        button.prefWidthProperty().bind(vbox.widthProperty().multiply(0.1)); // 20% of VBox width
        button.prefHeightProperty().bind(vbox.heightProperty().multiply(0.1)); // 10% of VBox height

        // Optionally, adjust ListView size to fill remaining space
        packetDisplayField.prefWidthProperty().bind(vbox.widthProperty().multiply(0.8)); // 80% of VBox width
        packetDisplayField.prefHeightProperty().bind(vbox.heightProperty().multiply(0.5)); // 50% of VBox height
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initialize();
        new Thread(this::interfaceSelect).start();
    }
    @FXML
    public void onInterfaceSelected() throws IOException {
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
