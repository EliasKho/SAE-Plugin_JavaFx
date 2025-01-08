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
import java.util.ArrayList;
import java.util.List;

public class ControlerClic implements EventHandler<MouseEvent> {
    private Modele modele;
    private String nomClasse;
    private ContextMenu contextMenu;

    public ControlerClic(Modele modele) {
        this.modele = modele;
        this.contextMenu=new ContextMenu();
    }

    private String getString(FileComposite fc){
        // récupérer le package de la classe du fichier
        String packageName = fc.getPath().replace(File.separator, ".");
        // on retire le .java
        packageName = packageName.substring(0, packageName.length() - 5);
        // on retire tous les fichiers avant le /java/ compris
        packageName = packageName.substring(packageName.indexOf("java.") + 5);

        return packageName;
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
                        String packageName = getString(file);

                        if (file instanceof Fichier) {
                            this.nomClasse = packageName;

                            item.setOnDragDetected(new EventHandler<MouseEvent>() {
                                @Override
                                public void handle(MouseEvent mouseEvent) {
                                    Dragboard db = item.startDragAndDrop(TransferMode.MOVE);
                                    ClipboardContent content = new ClipboardContent();
                                    content.putString(getNomClasse());
                                    db.setContent(content);
                                    event.consume();
                                }
                            });
                        }
                    }
                }
            }
        }

        //si clic droit
        if (event.getButton() == MouseButton.SECONDARY) {
            contextMenu.getItems().clear();

            if(event.getSource() instanceof TreeView){
                TreeView<FileComposite> item = (TreeView<FileComposite>) event.getSource();
                TreeItem<FileComposite> selectedItem = item.getSelectionModel().getSelectedItem();
                item.setOnDragDetected(this);

                if (selectedItem != null) {
                    FileComposite file = selectedItem.getValue();
                    if(file.isDirectory()) {
                        nomClasse = null;
                        List<String> liste = new ArrayList<>();
                        for(FileComposite fc: file.getChildren()){
                            if(!fc.isDirectory()){
                                String packageName = getString(fc);
                                liste.add(packageName);
                            }
                        }
                        modele.ajouterListClasses(liste);
                    }
                }
            }

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
                ControlerImage controlerImage = new ControlerImage(modele);
                item3.setOnAction(e -> {
                    controlerImage.captureImage();
                });
                MenuItem item4 = new MenuItem("Exporter en image diagramme PlantUML");
                item4.setOnAction(e -> {
                    // Capture de l'image, comment faire pour récupérer la scene et la vueClasse
                    controlerImage.captureImageUML();
                });
                contextMenu.getItems().addAll(item2, item3, item4);
            }
            contextMenu.show((Node) event.getSource(), event.getScreenX(), event.getScreenY());
        }
    }

    public String getNomClasse() {
        return nomClasse;
    }
}
