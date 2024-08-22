package com.pacscope.pacscope;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.Pcaps;
import org.pcap4j.packet.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static javafx.collections.FXCollections.observableArrayList;

public class OpenCaptureFile{
    @FXML
    private ImageView back;
    @FXML
    private Label fileName;
    @FXML
    private ToolBar toolBar;
    @FXML
    private VBox vbox;
    @FXML
    private TableColumn<ListedPackets, String> number;
    @FXML
    private TableColumn<ListedPackets, String> srcIp;
    @FXML
    private TableColumn<ListedPackets, String> dstIp;
    @FXML
    private TableColumn<ListedPackets, String> protocol;
    @FXML
    private TableColumn<ListedPackets, String> length;
    @FXML
    private TableColumn<ListedPackets, String> info;

    private static final List<EthernetPacket> packetList = new ArrayList<>();
    public static boolean opening = true;
    Stage primaryStage;
    private Thread thread;
    private File file;
    private ObservableList<ListedPackets> listedPackets;
    @FXML
    private TableView<ListedPackets> packetCaptureField;
    private static Packet selectedPacket;

    public void setFile(File file){
        this.file = file;
        setDynamicComponents();
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
        if(index >= 0){
            selectedPacket = packetList.get(index);
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("packet-display.fxml"));
        Parent root = loader.load();
        PacketDisplayController packetDisplayController = loader.getController();
        packetDisplayController.setPacket(selectedPacket);
        Stage newStage = new Stage();
        Scene scene = new Scene(root, 600, 600);
        newStage.setScene(scene);
        newStage.setTitle("Packet");
        newStage.show();
        newStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/pacscope/pacscope/icon.png"))));
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }
    public void openCaptureFilef() {
        Platform.runLater(() -> {
            if (file != null) {
                fileName.setText(file.getName());
                try {
                    PcapHandle handle = Pcaps.openOffline(file.getAbsolutePath());
                    Packet packet;
                    listedPackets = observableArrayList();
                    int i = 0;
                    String srcAddr;
                    String dstAddr;
                    String protocolName;
                    while ((packet = handle.getNextPacket()) != null) {
                        EthernetPacket ethernetPacket = packet.get(EthernetPacket.class);
                        srcAddr = DisplayingPacketsInTable.getSourceAddress(ethernetPacket);
                        dstAddr = DisplayingPacketsInTable.getDestinationAddress(ethernetPacket);
                        protocolName = DisplayingPacketsInTable.getProtocolName(ethernetPacket);
                        listedPackets.add(new ListedPackets(String.valueOf(++i), srcAddr, dstAddr,  protocolName, String.valueOf(packet.length()), packet.getPayload().getHeader().toString()));
                        packetList.add(ethernetPacket);
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
        number.setCellValueFactory(new PropertyValueFactory<>("number"));
        srcIp.setCellValueFactory(new PropertyValueFactory<>("source"));
        dstIp.setCellValueFactory(new PropertyValueFactory<>("destination"));
        protocol.setCellValueFactory(new PropertyValueFactory<>("protocol"));
        length.setCellValueFactory(new PropertyValueFactory<>("length"));
        info.setCellValueFactory(new PropertyValueFactory<>("info"));
        Platform.runLater(() -> packetCaptureField.setItems(listedPackets));


    }

    private void setDynamicComponents(){
        vbox.setPrefHeight(primaryStage.getScene().getHeight());
        vbox.setPrefWidth(primaryStage.getScene().getWidth());
        number.prefWidthProperty().bind(packetCaptureField.widthProperty().multiply(0.05)); // 10% of the TableView width
        srcIp.prefWidthProperty().bind(packetCaptureField.widthProperty().multiply(0.15)); // 20% of the TableView width
        dstIp.prefWidthProperty().bind(packetCaptureField.widthProperty().multiply(0.15)); // 20% of the TableView width
        protocol.prefWidthProperty().bind(packetCaptureField.widthProperty().multiply(0.05)); // 15% of the TableView width
        length.prefWidthProperty().bind(packetCaptureField.widthProperty().multiply(0.1)); // 15% of the TableView width
        info.prefWidthProperty().bind(packetCaptureField.widthProperty().multiply(0.5)); // 20% of the TableView width
        packetCaptureField.prefHeightProperty().bind(vbox.heightProperty().multiply(0.9));
        toolBar.prefHeightProperty().bind(vbox.heightProperty().multiply(0.1));
        back.setFitHeight(toolBar.getHeight());
    }
    }