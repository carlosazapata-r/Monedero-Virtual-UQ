module com.monedero {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.monedero to javafx.fxml;
    opens com.monedero.controller to javafx.fxml;

    exports com.monedero;
    exports com.monedero.controller;
}
