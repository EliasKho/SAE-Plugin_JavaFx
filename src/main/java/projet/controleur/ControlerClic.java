package projet.controleur;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Popup;
import javafx.stage.Stage;
import projet.Modele;
import projet.arborescence.Fichier;
import projet.arborescence.FileComposite;
import projet.classes.Attribut;
import projet.classes.Fleche;
import projet.classes.Methode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

@SuppressWarnings("unchecked")
public class ControlerClic implements EventHandler<MouseEvent> {
    private Modele modele;
    private String nomClasse;
    private ContextMenu contextMenu;

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
                MenuItem ajouterMethode = itemAjouterMethode(nom);
                MenuItem ajouterAttribut = itemAjouterAttribut(nom);
                contextMenu.getItems().addAll(item, ajouterMethode, ajouterAttribut);
                item.setOnAction(_ -> modele.supprimerClasse(nom));
            }
            if (event.getSource() instanceof Pane) {
                MenuItem item2 = new MenuItem("Supprimer tout");
                item2.setOnAction(_ -> modele.viderClasses());
                MenuItem item3 = new MenuItem("Exporter en image");
                item3.setOnAction(_ -> controlerImage.captureImage());
                MenuItem ajouterClasse = itemAjouterClasse();
                MenuItem ajouterRelation = itemAjouterRelation();
                contextMenu.getItems().addAll(item2, item3, ajouterClasse, ajouterRelation);
            }
            if (event.getSource() instanceof ImageView || event.getSource() instanceof Pane) {
                MenuItem item4 = new MenuItem("Exporter en image diagramme PlantUML");
                item4.setOnAction(_ -> controlerImage.captureImageUML());
                MenuItem item5 = itemCodeSource();
                contextMenu.getItems().addAll(item4, item5);
            }

            contextMenu.show((Node) event.getSource(), event.getScreenX(), event.getScreenY());
        }
    }

    private MenuItem itemCodeSource() {
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

    private MenuItem itemAjouterClasse() {
        MenuItem item = new MenuItem("Ajouter une classe");
        item.setOnAction(e -> {
            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);

            popupStage.setTitle("Ajouter une classe");

            VBox layout = new VBox(10);
            layout.setPadding(new Insets(20));

            RadioButton interfaceButton = new RadioButton("Interface");
            RadioButton abstractButton = new RadioButton("Abstract");

            ToggleGroup group = new ToggleGroup();
            interfaceButton.setToggleGroup(group);
            abstractButton.setToggleGroup(group);

            Button addButton = new Button("Ajouter");

            HBox buttons = new HBox(10);
            buttons.getChildren().addAll(interfaceButton, abstractButton);

            TextField classNameField = new TextField();

            layout.getChildren().addAll(buttons,
                    new Label("Nom de la classe:"), classNameField,
                    addButton);

            addButton.setOnAction(e1 -> {
                String type = "class";
                if (interfaceButton.isSelected()) {
                    type = "interface";
                } else if (abstractButton.isSelected()) {
                    type = "abstract";
                }
                String classe = classNameField.getText();
                if (classe.isEmpty()) {
                    // afficher un message d'erreur
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Erreur");
                    alert.setHeaderText("Nom de la classe non renseigné");
                    alert.setContentText("Veuillez renseigner le nom de la classe.");
                    alert.showAndWait();
                    return;
                }
                modele.ajouterClasseInexistante(classe, type,0, 0, new ArrayList<Attribut>(), new ArrayList<Methode>());
                popupStage.hide();
            });

            Scene scene = new Scene(layout);
            popupStage.setScene(scene);

            popupStage.show();
        });
        return item;
    }

    private MenuItem itemAjouterMethode(String nomClasse) {
        MenuItem item = new MenuItem("Ajouter une méthode");
        item.setOnAction(e -> {
            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);

            popupStage.setTitle("Ajouter une méthode");

            VBox layout = new VBox(10);
            layout.setPadding(new Insets(20));

            // bouton pour savoir si la méthode est un constructeur
            RadioButton constructorButton = new RadioButton("Constructeur");

            // choisir la visibilité de la méthode
            ComboBox<String> visibility = new ComboBox<>();
            visibility.getItems().addAll("public", "protected", "private");
            visibility.setValue("public");

            // choisir si la méthode est static
            CheckBox staticBox = new CheckBox("static");
            // choisir si la méthode est abstract
            CheckBox abstractBox = new CheckBox("abstract");

            TextField methodNameField = new TextField();
            TextField methodTypeField = new TextField();
            TextField methodParamsField = new TextField();

            Button addButton = new Button("Ajouter");

            layout.getChildren().addAll(
                    constructorButton,
                    new Label("Nom de la méthode:"), methodNameField,
                    new Label("Type de retour:"), methodTypeField,
                    new Label("Paramètres:"), methodParamsField,
                    visibility,
                    staticBox,
                    abstractBox,
                    addButton);

            constructorButton.setOnAction(e1 -> {
                if (constructorButton.isSelected()) {
                    methodNameField.clear();
                    methodNameField.setDisable(true);
                    methodTypeField.clear();
                    methodTypeField.setDisable(true);
                }
                else {
                    methodNameField.setDisable(false);
                    methodTypeField.setDisable(false);
                }
            });

            addButton.setOnAction(e1 -> {
                int modifier = getModifier(visibility.getValue(), staticBox.isSelected(), abstractBox.isSelected());
                if (methodNameField.getText().isEmpty() && !constructorButton.isSelected()) {
                    // afficher un message d'erreur
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Erreur");
                    alert.setHeaderText("Nom de la méthode non renseigné");
                    alert.setContentText("Veuillez renseigner le nom de la méthode.");
                    alert.showAndWait();
                    return;
                }
                if (constructorButton.isSelected()) {
                    modele.ajouterConstructeur(nomClasse, methodParamsField.getText(), modifier);
                } else {
                    modele.ajouterMethode(nomClasse, methodNameField.getText(), methodTypeField.getText(), methodParamsField.getText(), modifier);
                }
                popupStage.hide();
            });

            Scene scene = new Scene(layout);
            popupStage.setScene(scene);

            popupStage.show();
        });
        return item;
    }

    private MenuItem itemAjouterAttribut(String nomClasse) {
        MenuItem item = new MenuItem("Ajouter un attribut");
        item.setOnAction(e -> {
            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);

            popupStage.setTitle("Ajouter un attribut");

            VBox layout = new VBox(10);
            layout.setPadding(new Insets(20));

            // choisir la visibilité de l'attribut
            ComboBox<String> visibility = new ComboBox<>();
            visibility.getItems().addAll("public", "protected", "private");
            visibility.setValue("public");
            // choisir si l'attribut est static
            CheckBox staticBox = new CheckBox("static");
            // choisir si l'attribut est abstract
            CheckBox abstractBox = new CheckBox("abstract");

            TextField attributNameField = new TextField();
            TextField attributTypeField = new TextField();

            Button addButton = new Button("Ajouter");

            layout.getChildren().addAll(
                    new Label("Nom de l'attribut:"), attributNameField,
                    new Label("Type de l'attribut:"), attributTypeField,
                    visibility,
                    staticBox,
                    abstractBox,
                    addButton);

            addButton.setOnAction(e1 -> {
                int modifier = getModifier(visibility.getValue(), staticBox.isSelected(), abstractBox.isSelected());
                if (attributNameField.getText().isEmpty() || attributTypeField.getText().isEmpty()) {
                    // afficher un message d'erreur
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Erreur");
                    alert.setHeaderText("Nom ou type de l'attribut non renseigné");
                    alert.setContentText("Veuillez renseigner le nom et le type de l'attribut.");
                    alert.showAndWait();
                    return;
                }
                modele.ajouterAttribut(nomClasse, attributNameField.getText(), attributTypeField.getText(), modifier);
                popupStage.hide();
            });

            Scene scene = new Scene(layout);
            popupStage.setScene(scene);

            popupStage.show();
        });
        return item;
    }

    public int getModifier(String visibilite, boolean isStatic, boolean isAbstract) {
        int modifier = 0;
        if (visibilite.equals("public")) {
            modifier = modifier | 1;
        } else if (visibilite.equals("protected")) {
            modifier = modifier | 4;
        } else if (visibilite.equals("private")) {
            modifier = modifier | 2;
        }
        if (isStatic) {
            modifier = modifier | 8;
        }
        if (isAbstract) {
            modifier = modifier | 1024;
        }
        return modifier;
    }

    private MenuItem itemAjouterRelation(){
        MenuItem item = new MenuItem("Ajouter une relation");
        item.setOnAction(e -> {
            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);

            popupStage.setTitle("Ajouter une relation");

            VBox layout = new VBox(10);
            layout.setPadding(new Insets(20));

            // choisir le type de relation
            HashMap<String, String> typesRelation = new HashMap<>();
            ComboBox<String> typeRelation = new ComboBox<>();
            typeRelation.getItems().addAll("Héritage", "Association", "Implémentation");
            typeRelation.setValue("Association");
            typesRelation.put("Héritage", Fleche.EXTENDS);
            typesRelation.put("Association", Fleche.DEPENDANCE);
            typesRelation.put("Implémentation", Fleche.IMPLEMENTS);

            // récupérer les noms des classes
            HashMap<String, String> nomsClasses = new HashMap<>();
            Set<String> packagesClasses = modele.getClasses().keySet();
            for (String classe : packagesClasses) {
                String nomClasse = classe.substring(classe.lastIndexOf(".")+1);
                nomsClasses.put(nomClasse, classe);
            }

            // choisir la classe source
            ComboBox<String> classeCible = new ComboBox<>();
            classeCible.getItems().addAll(nomsClasses.keySet());
            classeCible.setValue(nomsClasses.get(0));

            // choisir la classe cible
            ComboBox<String> classeSource = new ComboBox<>();
            classeSource.getItems().addAll(nomsClasses.keySet());
            classeSource.setValue(nomsClasses.get(0));

            Button addButton = new Button("Ajouter");

            Label labelCibleClasse = new Label("Classe cible:");
            Label labelSourceClasse = new Label("Classe source:");

            Label labelCardinaliteCible = new Label("Cardinalité cible:");
            Label labelCardinaliteSource = new Label("Cardinalité source:");
            Label labelNomRelation = new Label("Nom de la relation:");

            TextField cardinaliteCibleField = new TextField();
            TextField cardinaliteSourceField = new TextField();
            TextField nomRelationField = new TextField();

            layout.getChildren().addAll(
                    new Label("Type de relation:"), typeRelation,
                    labelCibleClasse, classeCible,
                    labelSourceClasse, classeSource,
                    labelCardinaliteCible, cardinaliteCibleField,
                    labelCardinaliteSource, cardinaliteSourceField,
                    labelNomRelation, nomRelationField,
                    addButton);

            addButton.setOnAction(e1 -> {
                String type = typesRelation.get(typeRelation.getValue());
                String cible = nomsClasses.get(classeCible.getValue());
                String source = nomsClasses.get(classeSource.getValue());
                String cardinaliteCible = cardinaliteCibleField.getText();
                String cardinaliteSource = cardinaliteSourceField.getText();
                String nomRelation = nomRelationField.getText();
                // si tous les champs sont remplis
                if (source == null || cible == null){
                    // afficher un message d'erreur
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Erreur");
                    alert.setHeaderText("Erreur lors de l'ajout de la relation");
                    alert.setContentText("Veuillez remplir tous les champs");
                    alert.showAndWait();
                    return;
                }
                modele.ajouterRelation(cible, source, type, cardinaliteCible, cardinaliteSource, nomRelation);
                popupStage.hide();
            });

            typeRelation.setOnAction(e2 -> {
                String type = typesRelation.get(typeRelation.getValue());
                if (type.equals(Fleche.IMPLEMENTS)) {
                    labelCibleClasse.setText("Classe implémentée:");
                    labelSourceClasse.setText("Interface implémentant:");
                    cardinaliteCibleField.setDisable(true);
                    cardinaliteSourceField.setDisable(true);
                    nomRelationField.setDisable(true);
                    cardinaliteCibleField.setText("");
                    cardinaliteSourceField.setText("");
                    nomRelationField.setText("");
                } else if (type.equals(Fleche.EXTENDS)) {
                    labelCibleClasse.setText("Classe mère:");
                    labelSourceClasse.setText("Classe fille:");
                    cardinaliteCibleField.setDisable(true);
                    cardinaliteSourceField.setDisable(true);
                    nomRelationField.setDisable(true);
                    cardinaliteCibleField.setText("");
                    cardinaliteSourceField.setText("");
                    nomRelationField.setText("");
                } else {
                    labelCibleClasse.setText("Classe cible:");
                    labelSourceClasse.setText("Classe source:");
                    cardinaliteCibleField.setDisable(false);
                    cardinaliteSourceField.setDisable(false);
                    nomRelationField.setDisable(false);
                }
            });

            Scene scene = new Scene(layout);
            popupStage.setScene(scene);

            popupStage.show();
        });
        return item;
    }

    public String getNomClasse() {
        return nomClasse;
    }
}
