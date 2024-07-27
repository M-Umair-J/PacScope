module com.pacscope.pacscope {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires org.pcap4j.core;


    opens com.pacscope.pacscope to javafx.fxml;
    exports com.pacscope.pacscope;
}