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
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.pcap4j.core.*;
import org.pcap4j.packet.Packet;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static javafx.collections.FXCollections.observableArrayList;

public class PacketCaptureController {
    @FXML
    private TableView<ListedPackets> packetCaptureField;
    @FXML
    private ToolBar toolbar;
    @FXML
    private VBox vbox;
    @FXML
    private ImageView pauseImg;

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
    @FXML
    private Label interfaceLabel;
    @FXML
    private TextField filter;

    private String filterText = "";
    private static final List<Packet> filteredPacketList = new ArrayList<>();
    private ObservableList<ListedPackets> listedPacketsFiltered;
    ObservableList<ListedPackets> listedPackets = observableArrayList();
    private Stage primaryStage;
    private static PcapNetworkInterface selectedInterface;
    private static Packet selectedPacket;
    private volatile boolean capturing;
    private Thread thread;
    private static final List<Packet> packetList = new ArrayList<>();
    private boolean filtered;
    private int j = 0;


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
                Scene scene = new Scene(root, primaryStage.getScene().getWidth(), primaryStage.getScene().getHeight());
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
            listedPacketsFiltered = observableArrayList();
            int i = 0;
            while (capturing && !Thread.currentThread().isInterrupted()) {
                try {
                    packet = handle.getNextPacket();
                } catch (NotOpenException e) {
                    e.printStackTrace();
                }
                String srcAddr;
                String dstAddr;
                String protocolName;
                if (packet != null && capturing) {
                    String headerInfo = packet.getHeader().toString();
                    if(packet.getPayload()!=null && packet.getPayload().getHeader()!=null){
                        headerInfo = packet.getPayload().getHeader().toString();
                    }
                    srcAddr = DisplayingPacketsInTable.getSourceAddress(packet);
                    dstAddr = DisplayingPacketsInTable.getDestinationAddress(packet);
                    protocolName = DisplayingPacketsInTable.getProtocolName(packet);
                    listedPackets.add(new ListedPackets(String.valueOf(++i), srcAddr, dstAddr, protocolName, String.valueOf(packet.length()), headerInfo));
                    synchronized (packetList) {
                        packetList.add(packet);
                    }
                    if(!filtered) {
                        showPackets(listedPackets);
                    }
                    else{
                        if(j == 0){
                            for(Packet packet1: packetList){
                                String headerInfo1 = packet1.getHeader().toString();
                                if(packet1.getPayload()!=null && packet.getPayload().getHeader()!=null){
                                    headerInfo1 = packet1.getPayload().getHeader().toString();
                                }
                                if(Objects.equals(DisplayingPacketsInTable.getProtocolName(packet1), filterText)) {
                                    if(!filteredPacketList.contains(packet1)) {
                                        filteredPacketList.add(packet1);
                                        listedPacketsFiltered.add(new ListedPackets(String.valueOf(packetList.lastIndexOf(packet1)+1), DisplayingPacketsInTable.getSourceAddress(packet1), DisplayingPacketsInTable.getDestinationAddress(packet1), DisplayingPacketsInTable.getProtocolName(packet1), String.valueOf(packet1.length()), headerInfo1));
                                    }
                                }
                            }
                        }
                        else{
                            if(Objects.equals(protocolName, filterText)) {
                                if(!filteredPacketList.contains(packet)) {
                                    filteredPacketList.add(packet);
                                    listedPacketsFiltered.add(new ListedPackets(String.valueOf(i), srcAddr, dstAddr, protocolName, String.valueOf(packet.length()), headerInfo));
                                }
                            }
                        }
                        showPackets(listedPacketsFiltered);
                        j++;
                    }
                    primaryStage.setOnCloseRequest(event -> System.exit(0));

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
    private void filterOut(){
        filter.setOnAction(event -> {
            if(DisplayingPacketsInTable.isValidFilter(filter.getText())){
                if(!Objects.equals(filterText, filter.getText())) {
                    filterText = filter.getText();
                    j = 0;
                    filteredPacketList.clear();
                    listedPacketsFiltered.clear();
                }
                filtered = true;
            }
            else{
                if(!DisplayingPacketsInTable.isValidFilter(filter.getText()) && !filter.getText().isEmpty()){
                    DisplayingPacketsInTable.generateAlertInvalidFilter(filter.getText());
                }
                filterText = "";
                filtered = false;
            }
            if(!capturing){
                pauseCapture();
            }
        });
    }

    public void showPackets(ObservableList<ListedPackets> listedPackets){
        number.setCellValueFactory(new PropertyValueFactory<>("number"));
        srcIp.setCellValueFactory(new PropertyValueFactory<>("source"));
        dstIp.setCellValueFactory(new PropertyValueFactory<>("destination"));
        protocol.setCellValueFactory(new PropertyValueFactory<>("protocol"));
        length.setCellValueFactory(new PropertyValueFactory<>("length"));
        info.setCellValueFactory(new PropertyValueFactory<>("info"));
        Platform.runLater(() -> packetCaptureField.setItems(listedPackets));
    }

    @FXML
    public void displayPacket() throws IOException {
        int index = packetCaptureField.getSelectionModel().getSelectedIndex();
        if(index >= 0){
            if(packetCaptureField.getItems() == listedPacketsFiltered){
                selectedPacket = filteredPacketList.get(index);
            }
            else{
                selectedPacket = packetList.get(index);
            }
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("packet-display.fxml"));
        Parent root = loader.load();
        PacketDisplayController packetDisplayController = loader.getController();
        packetDisplayController.setPacket(selectedPacket);
        Scene scene = new Scene(root, 600, 600);
        Stage newStage = new Stage();
        newStage.setScene(scene);
        newStage.setTitle("Packet");
        newStage.show();
        newStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/pacscope/pacscope/icon.png"))));
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
        setDynamicComponents();
        interfaceLabel.setText(selectedInterface.getDescription());
    }
    @FXML
    public void pauseCapture(){
        capturing = false;
        updateCaptureButtons();
        if(!filtered){
            showPackets(listedPackets);
        }
        else{
            int i = 0;
            for(Packet packet: packetList) {
                i++;
                if (Objects.equals(filterText, DisplayingPacketsInTable.getProtocolName(packet))) {
                    filteredPacketList.add(packet);
                    listedPacketsFiltered.add(new ListedPackets(String.valueOf(i), DisplayingPacketsInTable.getSourceAddress(packet), DisplayingPacketsInTable.getDestinationAddress(packet), DisplayingPacketsInTable.getProtocolName(packet), String.valueOf(packet.length()), packet.getPayload().getHeader().toString()));
                    showPackets(listedPacketsFiltered);
                }
            }
        }

    }
    private void updateCaptureButtons(){
        pauseImg.setDisable(!capturing);
    }

    public void setDynamicComponents(){
        vbox.setPrefHeight(primaryStage.getHeight());
        vbox.setPrefWidth(primaryStage.getWidth());
        toolbar.prefWidthProperty().bind(vbox.widthProperty());
        toolbar.prefHeightProperty().bind(vbox.heightProperty().multiply(0.1));
        packetCaptureField.prefHeightProperty().bind(vbox.heightProperty().multiply(0.9));
        packetCaptureField.prefWidthProperty().bind(vbox.widthProperty());
        number.prefWidthProperty().bind(packetCaptureField.widthProperty().multiply(0.05)); // 10% of the TableView width
        srcIp.prefWidthProperty().bind(packetCaptureField.widthProperty().multiply(0.15)); // 20% of the TableView width
        dstIp.prefWidthProperty().bind(packetCaptureField.widthProperty().multiply(0.15)); // 20% of the TableView width
        protocol.prefWidthProperty().bind(packetCaptureField.widthProperty().multiply(0.05)); // 15% of the TableView width
        length.prefWidthProperty().bind(packetCaptureField.widthProperty().multiply(0.1)); // 15% of the TableView width
        info.prefWidthProperty().bind(packetCaptureField.widthProperty().multiply(0.5)); // 20% of the TableView width
    }
}