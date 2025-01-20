module org.example.javafxdemo {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires java.sql;

    opens org.example.javafxdemo to javafx.fxml;
    exports org.example.javafxdemo;
}