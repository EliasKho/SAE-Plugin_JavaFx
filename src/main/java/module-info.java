module com.example.saeplugin_javafx {
    requires javafx.controls;
    requires javafx.graphics;
    requires java.desktop;
    requires javafx.swing;
    requires plantuml;
    requires java.scripting;

//    opens java.desktop;/com.sun.imageio.plugins.png to javafx.graphics;

    exports projet;
    exports projet.arborescence;
    exports projet.classes;
}