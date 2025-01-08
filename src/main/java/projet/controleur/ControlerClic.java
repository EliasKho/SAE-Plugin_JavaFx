package projet.controleur;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import projet.Modele;
import projet.arborescence.Fichier;
import projet.arborescence.FileComposite;
import projet.classes.Classe;

import java.io.File;
import java.util.Iterator;

public class ControlerClic implements EventHandler<MouseEvent> {
    private Modele modele;
    private String nomClasse;
    private ContextMenu contextMenu;

    public ControlerClic(Modele modele) {
        this.modele = modele;
        this.contextMenu=new ContextMenu();
    }

    public void handle(MouseEvent event) {
        System.out.println("CLIC");
        System.out.println("Source : " + event.getSource().getClass());
        System.out.println("Target : " + event.getTarget().getClass()+"\n");

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

        if (event.getSource() instanceof Pane) {
            if (nomClasse != null) {
                // on récupère les coordonnées du clic
                double x = event.getX();
                double y = event.getY();

                // on récupère le fichier sélectionné
                modele.ajouterClasse(nomClasse, x, y);
            }
            //si clic droit
            if (event.getButton() == MouseButton.SECONDARY) {
                // on récupère les coordonnées du clic
                double x = event.getX();
                double y = event.getY();
                contextMenu.getItems().clear();
                if(!modele.getClasses().isEmpty()){
                    MenuItem item = new MenuItem("Supprimer");
                    contextMenu.getItems().add(item);
                    item.setOnAction(e -> {
                        Iterator<Classe> it = modele.getClasses().values().iterator();
                        while(it.hasNext()){
                            Classe c = it.next();
                            if (c.getX() <= x && c.getY() <= y && c.getX() + c.getLargeur() >= x && c.getY() + c.getLongueur() >= y) {
                                modele.getClasses().remove(c.getNomPackage()+"."+c.getNom());
                                modele.updateRelations();
                                modele.notifierObservateur();
                                System.out.println("Suppression");
                            }
                        }
                    });
                }
                MenuItem item2 = new MenuItem("Supprimer tout");
                item2.setOnAction(e -> {
                    modele.getClasses().clear();
                    modele.updateRelations();
                });
                MenuItem item3 = new MenuItem("Exporter en image");
                item3.setOnAction(e -> {
                    // Capture de l'image, comment faire pour récupérer la scene et la vueClasse
                    ControlerImage.captureImage(modele.getScene(), modele.getVueClasse());
                });
                contextMenu.getItems().addAll(item2, item3);
                contextMenu.show((Node) event.getSource(), event.getScreenX(), event.getScreenY());
//                contextMenu.setAutoHide(true);

                // on récupère le fichier sélectionné
                System.out.println("Clic droit");
            }
        }
        if(event.getButton() == MouseButton.PRIMARY){
            //cacher le menu contextuel
            contextMenu.hide();
        }
    }
}
