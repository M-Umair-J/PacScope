package com.pacscope.pacscope;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToolBar;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import java.io.IOException;

public class HelpController {
    @FXML
    private VBox vbox;
    @FXML
    private ImageView logo;
    @FXML
    private ToolBar toolbar;
    @FXML
    private ImageView back;
    @FXML
    private ScrollPane scrollpane;
    @FXML
    private TextFlow textflow;

    private Stage primaryStage;
    public void setPrimaryStage(Stage primaryStage){
        this.primaryStage = primaryStage;
        setDynamicComponents();
    }

    private void setDynamicComponents(){
        vbox.prefHeightProperty().bind(primaryStage.getScene().heightProperty());
        vbox.prefWidthProperty().bind(primaryStage.getScene().widthProperty());
        toolbar.prefHeightProperty().bind(vbox.heightProperty().multiply(0.1));
        scrollpane.prefHeightProperty().bind(vbox.heightProperty().multiply(0.75));
        logo.fitHeightProperty().bind(vbox.heightProperty().multiply(0.15));
        back.fitHeightProperty().bind(toolbar.prefHeightProperty().multiply(0.9));
        back.fitWidthProperty().bind(back.fitHeightProperty());
        textflow.prefHeightProperty().bind(scrollpane.heightProperty());
        textflow.prefWidthProperty().bind(scrollpane.widthProperty());
        showContent();
    }
    private void showContent(){
        Text heading = new Text("\nHelp: PacScaope\n\n");
        heading.setStyle("-fx-font-size: 25px; -fx-text-fill: #2c2e33; -fx-font-family: Consolas; -fx-font-weight: bold;");
        Text avaialbeFilters = new Text("Filters Availabe\n\n");
        avaialbeFilters.setStyle("-fx-font-size: 20px; -fx-text-fill: #000000; -fx-font-family: Consolas; -fx-font-weight: bold;");
        Text filters = new Text("""
                HTTP, HTTPS, TCP, UDP, ICMP
                ICMPv6, ARP, IPv4, IPv6, FTP
                FTPS, IMAP, IMAPS, SMTP, POP3
                POP3S, SSH, Telnet, SNMP, NTP
                VoIP, DHCP, mDNS, DNS
                
                
                
                """);
        filters.setStyle("-fx-font-size: 16px; -fx-text-fill: #000000; -fx-font-family: Consolas;");
        Text note = new Text("Note:");
        note.setStyle("-fx-font-size: 20px; -fx-text-fill: #000000; -fx-font-family: Consolas; -fx-font-weight: bold;");
        Text filtersNote = new Text("The packet filtering is in the order of layer 7 to layer 1\nFor example, all the layer 7 traffic like HTTPS will be displayed as layer 7 instead of TCP traffic and must be filtered the same way.");
        filtersNote.setStyle("-fx-font-size: 16px; -fx-text-fill: #000000; -fx-font-family: Consolas;");
        Text headingFileFormat = new Text("\n\n\nFile Formats\n\n");
        headingFileFormat.setStyle("-fx-font-size: 20px; -fx-text-fill: #000000; -fx-font-family: Consolas; -fx-font-weight: bold;");
        Text formats = new Text("Pcap files (.pcap)\nPcapNG files (.pcapng)\n\n");
        formats.setStyle("-fx-font-size: 16px; -fx-text-fill: #000000; -fx-font-family: Consolas;");
        Text importantNote = new Text("Once live capture has been paused it can't be resumed since the packets during pause time have been lost. So the user must start another capture after saving or cancelling that paused capture.");
        importantNote.setStyle("-fx-font-size: 16px; -fx-text-fill: #000000; -fx-font-family: Consolas; -fx-font-weight: bold;");
        textflow.getChildren().addAll(heading,avaialbeFilters,filters,note,filtersNote,headingFileFormat,formats,importantNote);

    }
    @FXML
    private void goBack() {
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/pacscope/pacscope/main-screen.fxml"));
            Parent root = loader.load();
            MainController mainController = loader.getController();
            mainController.setPrimaryStage(primaryStage);
            Scene scene = new Scene(root, primaryStage.getScene().getWidth(), primaryStage.getScene().getHeight());
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (
                IOException e) {
            System.out.println("Failed to load the main screen");
        }
    }
}
