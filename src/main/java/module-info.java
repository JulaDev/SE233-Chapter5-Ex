module com.example.chapter5 {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.swing;

    opens se233.Chapter5 to javafx.fxml;
    exports se233.Chapter5;
}