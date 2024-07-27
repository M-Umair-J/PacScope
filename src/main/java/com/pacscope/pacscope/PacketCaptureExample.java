package com.pacscope.pacscope;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.pcap4j.core.*;
import org.pcap4j.core.PcapNetworkInterface.PromiscuousMode;
import org.pcap4j.packet.Packet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class PacketCaptureExample extends Application {

    private static final Logger logger = LoggerFactory.getLogger(PacketCaptureExample.class);
    private TextArea textArea;

    @Override
    public void start(Stage primaryStage) {
        textArea = new TextArea();
        VBox root = new VBox(textArea);
        Scene scene = new Scene(root, 600, 400);

        primaryStage.setTitle("Network Analyzer");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Start packet capture in a new thread
        new Thread(this::startPacketCapture).start();
    }

    private void startPacketCapture() {
        List<PcapNetworkInterface> allDevs = null;
        try {
            allDevs = Pcaps.findAllDevs();
        } catch (PcapNativeException e) {
            throw new RuntimeException(e);
        }
        if (allDevs.isEmpty()) {
            appendText("No network interfaces found.");
            return;
        }

        List<PcapNetworkInterface> finalAllDevs = allDevs;
        Platform.runLater(() -> {
            appendText("Available Network Interfaces:");
            for (PcapNetworkInterface dev : finalAllDevs) {
                appendText(dev.getName() + " : " + dev.getDescription());
            }
        });

        PcapNetworkInterface nif = allDevs.get(4); // or let user choose
        int snaplen = 65536;
        PromiscuousMode mode = PromiscuousMode.PROMISCUOUS;
        int timeout = 10;
        PcapHandle handle = null;
        try {
            handle = nif.openLive(snaplen, mode, timeout);
        } catch (PcapNativeException e) {
            throw new RuntimeException(e);
        }

        Platform.runLater(() -> appendText("Starting packet capture on: " + nif.getName()));

        while (true) {
            Packet packet = null;
            try {
                packet = handle.getNextPacket();
            } catch (NotOpenException e) {
                throw new RuntimeException(e);
            }
            if (packet != null) {
                Packet finalPacket = packet;
                Platform.runLater(() -> appendText(finalPacket.toString()));
            }
        }

    }

    private void appendText(String text) {
        textArea.appendText(text + "\n");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
