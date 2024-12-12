module com.example.saeplugin_javafx {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;


    opens com.example.saeplugin_javafx to javafx.fxml;

    exports projet;
}