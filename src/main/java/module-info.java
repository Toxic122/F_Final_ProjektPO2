module com.example.projektpo {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;
    requires com.dlsc.formsfx;
    requires org.junit.jupiter.api;
    requires org.mockito;
    requires org.junit.platform.commons;

    opens com.example.projektpo to javafx.fxml, org.mockito, org.junit.jupiter.api, org.junit.platform.commons;
    exports com.example.projektpo;
}
