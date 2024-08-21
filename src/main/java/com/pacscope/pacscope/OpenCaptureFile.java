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
import org.pcap4j.packet.namednumber.EtherType;
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
        if(index > 0){
            selectedPacket = packetList.get(index);
        }
        PacketDisplayController.packet = selectedPacket;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("packet-display.fxml"));
        Parent root = loader.load();
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
                        if(ethernetPacket.getHeader().getType() == EtherType.IPV4){
                            IpV4Packet ipv4Packet = ethernetPacket.get(IpV4Packet.class);
                            srcAddr = ipv4Packet.getHeader().getSrcAddr().getHostAddress();
                            dstAddr = ipv4Packet.getHeader().getDstAddr().getHostAddress();
                            if(Objects.equals(ipv4Packet.getHeader().getProtocol().toString(), "6 (TCP)")){
                                TcpPacket tcpPacket = ipv4Packet.get(TcpPacket.class);
                                protocolName = identifyTCPProtocol(tcpPacket);
                            }
                            else if(Objects.equals(ipv4Packet.getHeader().getProtocol().toString(), "17 (UDP)")){
                                    UdpPacket udpPacket = ipv4Packet.get(UdpPacket.class);
                                    protocolName = identifyUDPProtocol(udpPacket);

                            }
                            else if(Objects.equals(ipv4Packet.getHeader().getProtocol().toString(), "1 (ICMP")){
                                    protocolName = "ICMP";
                            }
                            else{
                                protocolName = "IPv4";
                            }
                        }
                        else if(ethernetPacket.getHeader().getType() == EtherType.IPV6) {
                            IpV6Packet ipV6Packet = ethernetPacket.get(IpV6Packet.class);
                            srcAddr = ipV6Packet.getHeader().getSrcAddr().getHostAddress();
                            dstAddr = ipV6Packet.getHeader().getDstAddr().getHostAddress();
                            if(ipV6Packet.getHeader().getProtocol().toString().equals("6 (TCP)")){
                                TcpPacket tcpPacket = ipV6Packet.get(TcpPacket.class);
                                protocolName = identifyTCPProtocol(tcpPacket);
                            }
                            else if (Objects.equals(ipV6Packet.getHeader().getProtocol().toString(), "17 (UDP)")) {
                                UdpPacket udpPacket = ipV6Packet.get(UdpPacket.class);
                                protocolName = identifyUDPProtocol(udpPacket);
                            }
                            else if (Objects.equals(ipV6Packet.getHeader().getProtocol().toString(), "1 (ICMP")) {
                                protocolName = "ICMPv6";
                            }
                            else{
                                protocolName = "IPv6";
                            }
                        }
                        else{
                            srcAddr = ethernetPacket.getHeader().getSrcAddr().toString();
                            dstAddr = ethernetPacket.getHeader().getDstAddr().toString();
                            protocolName = ethernetPacket.getHeader().getType().name();
                        }
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
        setDynamicComponents();
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
    private String identifyTCPProtocol(TcpPacket tcpPacket){
        String protocolName;
        if(Objects.equals(tcpPacket.getHeader().getSrcPort().toString(), "443 (HTTPS)") || Objects.equals(tcpPacket.getHeader().getDstPort().toString(), "443 (HTTPS)")){
            protocolName = "HTTPS";
        }
        else if(Objects.equals(tcpPacket.getHeader().getSrcPort().toString(), "80 (HTTP)") || Objects.equals(tcpPacket.getHeader().getDstPort().toString(), "80 (HTTP)")){
            protocolName = "HTTP";
        }
        else if(Objects.equals(tcpPacket.getHeader().getSrcPort().toString(), "25 (SMTP)") || Objects.equals(tcpPacket.getHeader().getDstPort().toString(),"25 (SMTP)")){
            protocolName = "SMTP";
        }
        else if(Objects.equals(tcpPacket.getHeader().getSrcPort().toString(), "143 (IMAP)") || Objects.equals(tcpPacket.getHeader().getDstPort().toString(),"143 (IMAP)")){
            protocolName = "IMAP";
        }
        else if(Objects.equals(tcpPacket.getHeader().getSrcPort().toString(), "993 (IMAPS)") || Objects.equals(tcpPacket.getHeader().getDstPort().toString(),"993 (IMAPS)")){
            protocolName = "IMAPS";
        }
        else if(Objects.equals(tcpPacket.getHeader().getSrcPort().toString(), "110 (POP3)") || Objects.equals(tcpPacket.getHeader().getDstPort().toString(),"110 (POP3)")){
            protocolName = "POP3";
        }
        else if(Objects.equals(tcpPacket.getHeader().getSrcPort().toString(), "995 (POP3S)") || Objects.equals(tcpPacket.getHeader().getDstPort().toString(),"995 (POP3S)")){
            protocolName = "POP3S";
        }
        else if(Objects.equals(tcpPacket.getHeader().getSrcPort().toString(), "21 (FTP)") || Objects.equals(tcpPacket.getHeader().getDstPort().toString(),"21 (FTP)") || Objects.equals(tcpPacket.getHeader().getSrcPort().toString(), "20 (FTP)") || Objects.equals(tcpPacket.getHeader().getDstPort().toString(),"20 (FTP)")){
            protocolName = "FTP";
        }
        else if(Objects.equals(tcpPacket.getHeader().getSrcPort().toString(), "990 (FTPS)") || Objects.equals(tcpPacket.getHeader().getDstPort().toString(),"990 (FTPS)")){
            protocolName = "FTPS";
        }
        else if(Objects.equals(tcpPacket.getHeader().getSrcPort().toString(), "22 (SSH)") || Objects.equals(tcpPacket.getHeader().getDstPort().toString(),"22 (SSH)")){
            protocolName = "SSH";
        }
        else if(Objects.equals(tcpPacket.getHeader().getSrcPort().toString(), "23 (Telnet)") || Objects.equals(tcpPacket.getHeader().getDstPort().toString(),"23 (Telnet)")){
            protocolName = "Telnet";
        }
        else {
            protocolName = "TCP";
        }
        return protocolName;
    }
    public String identifyUDPProtocol(UdpPacket udpPacket){
        String protocolName;
        if(Objects.equals(udpPacket.getHeader().getSrcPort().toString(), "53 (DNS)") || Objects.equals(udpPacket.getHeader().getDstPort().toString(), "53 (DNS)")){
            protocolName = "DNS";
        }
        else if(Objects.equals(udpPacket.getHeader().getSrcPort().toString(), "161 (SNMP)") ||Objects.equals(udpPacket.getHeader().getDstPort().toString(), "161 (SNMP)") || Objects.equals(udpPacket.getHeader().getSrcPort().toString(), "162") || Objects.equals(udpPacket.getHeader().getDstPort().toString(), "162")){
            protocolName = "SNMP";
        }
        else if(Objects.equals(udpPacket.getHeader().getSrcPort().toString(), "123 (NTP)") || Objects.equals(udpPacket.getHeader().getDstPort().toString(), "123 (NTP)")){
            protocolName = "NTP";
        }
        else if(Objects.equals(udpPacket.getHeader().getSrcPort().toString(), "5060 (VoIP)") || Objects.equals(udpPacket.getHeader().getSrcPort().toString(), "5060 (VoIP)") || Objects.equals(udpPacket.getHeader().getDstPort().toString(), "5061 (VoIP)") || Objects.equals(udpPacket.getHeader().getDstPort().toString(), "5061 (VoIP)")){
            protocolName = "VoIP";
        }
        else if(Objects.equals(udpPacket.getHeader().getSrcPort().toString(), "68 (DHCP)") || Objects.equals(udpPacket.getHeader().getSrcPort().toString(), "67 (DHCP)") || Objects.equals(udpPacket.getHeader().getDstPort().toString(), "68 (DHCP)") || Objects.equals(udpPacket.getHeader().getDstPort().toString(), "67 (DHCP)")){
            protocolName = "DHCP";
        }
        else if(Objects.equals(udpPacket.getHeader().getSrcPort().toString(), "5353 (mDNS)") || Objects.equals(udpPacket.getHeader().getDstPort().toString(), "5353 (mDNS)")){
            protocolName = "mDNS";
        }
        else{
            protocolName = "UDP";
        }
        return protocolName;
    }

}
