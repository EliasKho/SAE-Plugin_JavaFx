package projet.controleur;

import javafx.event.EventHandler;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import projet.Modele;
import projet.arborescence.Dossier;
import projet.arborescence.Fichier;
import projet.arborescence.FileComposite;

import java.io.File;

public class ControlerClic implements EventHandler<MouseEvent> {
    private Modele modele;
    private String nomClasse;

    public ControlerClic(Modele modele) {
        this.modele = modele;
    }

    public void handle(MouseEvent event) {

        if (event.getSource() instanceof TreeView) {
            TreeView<FileComposite> item = (TreeView<FileComposite>) event.getSource();
            TreeItem<FileComposite> selectedItem = item.getSelectionModel().getSelectedItem();

            if (selectedItem != null) {
                FileComposite file = selectedItem.getValue();
                // vérifier si le fichier est un fichier java
                if (file.getName().endsWith(".java")) {
                    // récupérer le package de la classe du fichier
                    String packageName = file.getPath().replace(File.separator, ".");
                    // on retire le .java
                    packageName = packageName.substring(0, packageName.length() - 5);
                    // on retire tous les fichiers avant le /java/ compris
                    packageName = packageName.substring(packageName.indexOf("java.") + 5);

                    if (file instanceof Fichier) {
                        this.nomClasse = packageName;
                    }
                }
            }
        }

        if (event.getSource() instanceof ScrollPane) {
            if (nomClasse != null) {
                // on récupère les coordonnées du clic
                double x = event.getX();
                double y = event.getY();

                // on récupère le fichier sélectionné
                modele.saveIntrospection(nomClasse, x, y);
            }
        }
    }
}
