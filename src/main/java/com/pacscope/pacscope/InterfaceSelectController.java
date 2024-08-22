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
import javafx.scene.control.ToolBar;
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
    private ToolBar toolbar;
    @FXML
    private ListView<String> interfaceDisplayField;
    private int count;
    private Stage primaryStage;
    private static PcapNetworkInterface pcapNetworkInterface;
    private List<PcapNetworkInterface> allDevs;


    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }
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
            interfaceDisplayField.getItems().clear();
            ObservableList<String> items = FXCollections.observableArrayList();
            count = 1;
            for(PcapNetworkInterface dev : findAllDevs) {
                    if(dev.getDescription() != null && !dev.getDescription().trim().isEmpty()) {
                        items.add(count + " : " + dev.getDescription());
                        count++;
                    }
            }
            interfaceDisplayField.setItems(items);
        });
    }
    public void setBounds(){
        toolbar.prefHeightProperty().bind(vbox.heightProperty().multiply(0.1));
        toolbar.prefWidthProperty().bind(vbox.widthProperty());
        interfaceDisplayField.prefWidthProperty().bind(vbox.widthProperty());
        interfaceDisplayField.prefHeightProperty().bind(vbox.heightProperty().multiply(0.9));
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setBounds();
        interfaceSelect();
    }
    @FXML
    public void onInterfaceSelected() throws IOException {
        int index = interfaceDisplayField.getSelectionModel().getSelectedIndex();
        if(index>=0 && index<count){
            pcapNetworkInterface = allDevs.get(index);
        }
        else{
            return;
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("packet-capture.fxml"));
        Parent root = loader.load();
        PacketCaptureController controller = loader.getController();
        controller.setPrimaryStage(primaryStage);
        Scene scene = new Scene(root, primaryStage.getScene().getWidth(), primaryStage.getScene().getHeight());
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    static PcapNetworkInterface getSelectedInterface(){
        return pcapNetworkInterface;
    }
    @FXML
    public void goBack(){
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

}
