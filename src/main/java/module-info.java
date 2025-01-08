module com.example.saeplugin_javafx {
    requires javafx.controls;
    requires javafx.graphics;
    requires java.desktop;
    requires javafx.swing;
    requires plantuml;
    requires java.scripting;

    exports projet;
    exports projet.arborescence;
    exports projet.classes;
}