module com.lucandia {
    requires javafx.controls;
    requires javafx.fxml;
    //requires transitive javafx.graphics;

    opens com.lucandia to javafx.fxml;
    exports com.lucandia;
}