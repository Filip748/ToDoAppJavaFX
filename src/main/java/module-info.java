module com.example.todoappjavafx {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.todoappjavafx to javafx.fxml;
    exports com.example.todoappjavafx;
}