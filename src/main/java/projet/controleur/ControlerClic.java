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
                    
                        if (file instanceof Fichier) {
                            this.nomClasse = file.getAbsolutePath();
                            System.out.println("Nom de la classe : " + this.nomClasse);

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
