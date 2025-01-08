package projet.controleur;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import projet.Modele;
import projet.arborescence.Fichier;
import projet.arborescence.FileComposite;

import java.io.File;

public class ControlerClic implements EventHandler<MouseEvent> {
    private Modele modele;
    private String nomClasse;
    private ContextMenu contextMenu;

    public ControlerClic(Modele modele) {
        this.modele = modele;
        this.contextMenu=new ContextMenu();
    }

    public void handle(MouseEvent event) {
        event.consume();

        if (event.getButton() == MouseButton.PRIMARY) {
            //cacher le menu contextuel
            contextMenu.hide();

            if (event.getSource() instanceof TreeView) {
                TreeView<FileComposite> item = (TreeView<FileComposite>) event.getSource();
                TreeItem<FileComposite> selectedItem = item.getSelectionModel().getSelectedItem();
                item.setOnDragDetected(this);

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

                        if (file instanceof Fichier) {
                            this.nomClasse = packageName;
                            Dragboard db = item.startDragAndDrop(TransferMode.MOVE);
                            ClipboardContent content = new ClipboardContent();
                            content.putString(this.nomClasse);
                            db.setContent(content);
                            event.consume();
                        }
                    }
                }

                if (event.getSource() instanceof Pane) {
                    if (nomClasse != null) {
                        // on récupère les coordonnées du clic
                        double x = event.getX();
                        double y = event.getY();

                        // on récupère le fichier sélectionné
                        modele.ajouterClasse(nomClasse, x, y);
                    }
                }
            }
            //si clic droit
            if (event.getButton() == MouseButton.SECONDARY) {
                contextMenu.getItems().clear();

                if (event.getSource() instanceof VBox) {
                    VBox box = (VBox) event.getSource();
                    String nom = box.getId();

                    MenuItem item = new MenuItem("Supprimer");
                    contextMenu.getItems().add(item);
                    item.setOnAction(e -> {
                        modele.supprimerClasse(nom);
                    });
                }

                if (event.getSource() instanceof Pane) {
                    MenuItem item2 = new MenuItem("Supprimer tout");
                    item2.setOnAction(e -> {
                        modele.viderClasses();
                    });
                    MenuItem item3 = new MenuItem("Exporter en image");
                    item3.setOnAction(e -> {
                        // Capture de l'image, comment faire pour récupérer la scene et la vueClasse
                        ControlerImage.captureImage(modele.getScene(), modele.getVueClasse());
                    });
                    MenuItem item4 = new MenuItem("Exporter en image diagramme PlantUML");
                    item4.setOnAction(e -> {
                        // Capture de l'image, comment faire pour récupérer la scene et la vueClasse
                        ControlerImage.captureImageUML();
                    });
                    contextMenu.getItems().addAll(item2, item3, item4);
                }
                contextMenu.show((Node) event.getSource(), event.getScreenX(), event.getScreenY());
            }
        }
    }
}
