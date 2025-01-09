package projet.controleur;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import projet.Modele;
import projet.arborescence.Fichier;
import projet.arborescence.FileComposite;

import java.util.ArrayList;
import java.util.List;
@SuppressWarnings("unchecked")
public class ControlerClic implements EventHandler<MouseEvent> {
    private final Modele modele;
    private String nomClasse;
    private final ContextMenu contextMenu;

    public ControlerClic(Modele modele) {
        this.modele = modele;
        this.contextMenu = new ContextMenu();
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

                        item.setOnDragDetected(_ -> {
                            Dragboard db = item.startDragAndDrop(TransferMode.MOVE);
                            ClipboardContent content = new ClipboardContent();
                            content.putString(getNomClasse());
                            db.setContent(content);
                            event.consume();
                        });
                    }

                }
            }
        }

        //si clic droit
        if (event.getButton() == MouseButton.SECONDARY) {
            System.out.println("clic droit");
            contextMenu.getItems().clear();
            ControlerImage controlerImage = new ControlerImage(modele);

            if (event.getSource() instanceof TreeView) {
                TreeView<FileComposite> item = (TreeView<FileComposite>) event.getSource();
                TreeItem<FileComposite> selectedItem = item.getSelectionModel().getSelectedItem();
                item.setOnDragDetected(this);


                if (selectedItem != null) {
                    FileComposite file = selectedItem.getValue();
                    if (file.isDirectory()) {
                        nomClasse = null;
                        List<String> liste = new ArrayList<>();
                        for (FileComposite fc : file.getChildren()) {
                            if (!fc.isDirectory()) {
                                liste.add(fc.getAbsolutePath());
                            }
                        }
                        modele.ajouterListClasses(liste);
                    }
                }
            }

            if (event.getSource() instanceof VBox box) {
                String nom = box.getId();

                MenuItem item = new MenuItem("Supprimer");
                contextMenu.getItems().add(item);
                item.setOnAction(_ -> modele.supprimerClasse(nom));
            }
            if (event.getSource() instanceof Pane) {
                MenuItem item2 = new MenuItem("Supprimer tout");
                item2.setOnAction(_ -> modele.viderClasses());
                MenuItem item3 = new MenuItem("Exporter en image");
                item3.setOnAction(_ -> controlerImage.captureImage());
                contextMenu.getItems().addAll(item2, item3);
            }
            if (event.getSource() instanceof ImageView || event.getSource() instanceof Pane) {
                MenuItem item4 = new MenuItem("Exporter en image diagramme PlantUML");
                item4.setOnAction(_ -> controlerImage.captureImageUML());
                MenuItem item5 = getItem5();
                contextMenu.getItems().addAll(item4, item5);
            }

            contextMenu.show((Node) event.getSource(), event.getScreenX(), event.getScreenY());
        }
    }

    private MenuItem getItem5() {
        MenuItem item5 = new MenuItem("Générer le code source correspondant au diagramme");
        item5.setOnAction(_ -> {
            boolean res = modele.genererCodeSource();
            if (res) {
                Popup popup = new Popup();
                popup.setX(600);
                popup.setY(100);
                VBox popupVBox = new VBox();
                popupVBox.getChildren().add(new Label("Le code source a été généré avec succès !"));
                popupVBox.setStyle("-fx-background-color: #2c8cff; -fx-text-fill: white; -fx-padding: 10px;");


                popup.getContent().add(popupVBox);

                popup.show(modele.getScene().getWindow());

                // Fermer 1 seconde après
                new Thread(() -> {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                    // Cacher le popup sur le thread d'application JavaFX
                    Platform.runLater(popup::hide);
                }).start();
            }
        });
        return item5;
    }

    public String getNomClasse() {
        return nomClasse;
    }
}
