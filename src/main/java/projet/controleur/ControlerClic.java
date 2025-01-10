package projet.controleur;

import javafx.application.Platform;
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
import projet.classes.Fleche;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

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

        // si clic gauche
        if (event.getButton() == MouseButton.PRIMARY) {
            //cacher le menu contextuel
            contextMenu.hide();

            // si clic sur un fichier
            if (event.getSource() instanceof TreeView) {
                // on récupère le fichier sélectionné
                TreeView<FileComposite> item = (TreeView<FileComposite>) event.getSource();
                TreeItem<FileComposite> selectedItem = item.getSelectionModel().getSelectedItem();

                // on verifie qu'un fichier est bien sélectionné
                if (selectedItem != null) {
                    // on récupère le fichier
                    FileComposite file = selectedItem.getValue();
                    // si c'est un fichier
                    if (file instanceof Fichier) {
                        // on récupère le chemin du fichier
                        this.nomClasse = file.getAbsolutePath();
                        // on met un drag detected pour ajouter le fichier
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
            // on vide le menu contextuel
            contextMenu.getItems().clear();
            ControlerImage controlerImage = new ControlerImage(modele);

            // si clic sur l'arborescence
            if (event.getSource() instanceof TreeView) {
                // on récupère l'arborescence et l'objet sélectionné
                TreeView<FileComposite> item = (TreeView<FileComposite>) event.getSource();
                TreeItem<FileComposite> selectedItem = item.getSelectionModel().getSelectedItem();

                // si un objet est sélectionné
                if (selectedItem != null) {
                    // on récupère le fichier
                    FileComposite file = selectedItem.getValue();
                    // si c'est un répertoire
                    if (file.isDirectory()) {
                        nomClasse = null;
                        List<String> liste = new ArrayList<>();
                        // on récupère les classes du répertoire
                        for (FileComposite fc : file.getChildren()) {
                            if (!fc.isDirectory()) {
                                liste.add(fc.getAbsolutePath());
                            }
                        }
                        // on ajoute toutes les classes sur le diagramme
                        modele.ajouterListClasses(liste);
                    }
                }
            } else {
                // création de sous menus
                Menu sousMenuExport = new Menu("Exporter");
                Menu sousMenuAjouter = new Menu("Ajouter");
                Menu classesExternes = null;
                // si on a cliqué sur une classe
                if (event.getSource() instanceof VBox box) {
                    // on récupère le nom de la classe
                    String nom = box.getId();

                    // option pour supprimer la classe
                    MenuItem item = new MenuItem("Supprimer");
                    item.setOnAction(_ -> modele.supprimerClasse(nom));
                    contextMenu.getItems().add(item);

                    // options pour ajouter une méthode ou un attribut à la classe
                    MenuItem ajouterMethode = itemAjouterMethode(nom);
                    MenuItem ajouterAttribut = itemAjouterAttribut(nom);
                    sousMenuAjouter.getItems().addAll(ajouterMethode, ajouterAttribut);

                    // on récupère les classes externes de la classe
                    classesExternes = new Menu("Classes externes");
                    for (String classe : modele.getClasses().get(nom).getClassesExternes()) {
                        // on ajoute les classes externes dans le menu
                        MenuItem itemClasse = new MenuItem(classe);
                        itemClasse.setOnAction(e -> modele.ajouterClasse(classe, 10, 10));
                        classesExternes.getItems().add(itemClasse);
                    }
                    // si aucune classe externe
                    if (classesExternes.getItems().isEmpty()) {
                        // on ajoute un item désactivé
                        MenuItem itemClasse = new MenuItem("Aucune classe externe");
                        itemClasse.setDisable(true);
                        classesExternes.getItems().add(itemClasse);
                    }
                    contextMenu.getItems().add(classesExternes);
                }
                // si on a cliqué sur le diagramme
                if (event.getSource() instanceof Pane) {
                    // option pour supprimer toutes les classes
                    MenuItem item2 = new MenuItem("Supprimer tout");
                    item2.setOnAction(_ -> modele.viderClasses());
                    contextMenu.getItems().add(item2);

                    // options pour ajouter une classe ou une relation entre classes
                    MenuItem ajouterClasse = itemAjouterClasse();
                    MenuItem ajouterRelation = itemAjouterRelation();
                    sousMenuAjouter.getItems().addAll(ajouterClasse, ajouterRelation);

                    // option pour exporter le diagramme en image
                    MenuItem item3 = new MenuItem("Exporter en image");
                    sousMenuExport.getItems().add(item3);
                    item3.setOnAction(_ -> controlerImage.captureImage());
                }
                if (event.getSource() instanceof ImageView || event.getSource() instanceof Pane) {
                    // option pour exporter le diagramme en image PlantUML
                    MenuItem item4 = new MenuItem("Exporter en image diagramme PlantUML");
                    item4.setOnAction(_ -> controlerImage.captureImageUML());

                    // option pour générer le code source correspondant au diagramme
                    MenuItem item5 = itemCodeSource();
                    sousMenuExport.getItems().addAll(item4, item5);
                }
                // on ajoute les sous menus
                if (classesExternes != null) {
                    contextMenu.getItems().add(classesExternes);
                }
                contextMenu.getItems().addAll(sousMenuAjouter, sousMenuExport);

                contextMenu.show((Node) event.getSource(), event.getScreenX(), event.getScreenY());
            }
        }
    }

    /**
     * Créer un item pour générer le code source correspondant au diagramme
     * @return
     */
    private MenuItem itemCodeSource() {
        MenuItem item5 = new MenuItem("Générer le code source correspondant au diagramme");
        item5.setOnAction(_ -> {
            // Générer le code source
            boolean res = modele.genererCodeSource();
            if (res) {
                // Afficher un popup pour indiquer que le code source a été généré
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

    /**
     * Créer un item pour ajouter une classe
     * @return
     */
    private MenuItem itemAjouterClasse() {
        MenuItem item = new MenuItem("Ajouter une classe");
        item.setOnAction(e -> {
            // Créer une nouvelle fenêtre popup
            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);

            popupStage.setTitle("Ajouter une classe");

            VBox layout = new VBox(10);
            layout.setPadding(new Insets(20));

            // choix des options
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
                // récupérer le type de la classe
                String type = "class";
                if (interfaceButton.isSelected()) {
                    type = "interface";
                } else if (abstractButton.isSelected()) {
                    type = "abstract";
                }
                String classe = classNameField.getText();
                // si le nom de la classe est vide
                if (classe.isEmpty()) {
                    // afficher un message d'erreur
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Erreur");
                    alert.setHeaderText("Nom de la classe non renseigné");
                    alert.setContentText("Veuillez renseigner le nom de la classe.");
                    alert.showAndWait();
                    return;
                }
                // ajouter la classe
                modele.ajouterClasseInexistante(classe, type,10, 10, new ArrayList<>(), new ArrayList<>());
                popupStage.hide();
            });

            Scene scene = new Scene(layout);
            popupStage.setScene(scene);

            popupStage.show();
        });
        return item;
    }

    /**
     * Créer un item pour ajouter une methode
     * @return
     */
    private MenuItem itemAjouterMethode(String nomClasse) {
        MenuItem item = new MenuItem("Ajouter une méthode");
        item.setOnAction(e -> {
            // Créer une nouvelle fenêtre popup
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
                // si c'est un constructeur, on désactive les champs nom et type
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
                // récupérer le modifier de la méthode
                int modifier = getModifier(visibility.getValue(), staticBox.isSelected(), abstractBox.isSelected());
                // si le nom de la méthode est vide et que ce n'est pas un constructeur
                if (methodNameField.getText().isEmpty() && !constructorButton.isSelected()) {
                    // afficher un message d'erreur
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Erreur");
                    alert.setHeaderText("Nom de la méthode non renseigné");
                    alert.setContentText("Veuillez renseigner le nom de la méthode.");
                    alert.showAndWait();
                    return;
                }
                // ajouter la méthode
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

    /**
     * Créer un item pour ajouter un attribut à une classe
     * @param nomClasse
     * @return
     */
    private MenuItem itemAjouterAttribut(String nomClasse) {
        MenuItem item = new MenuItem("Ajouter un attribut");
        item.setOnAction(e -> {
            // Créer une nouvelle fenêtre popup
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
                // récupérer le modifier de l'attribut
                int modifier = getModifier(visibility.getValue(), staticBox.isSelected(), abstractBox.isSelected());
                // si le nom ou le type de l'attribut est vide
                if (attributNameField.getText().isEmpty() || attributTypeField.getText().isEmpty()) {
                    // afficher un message d'erreur
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Erreur");
                    alert.setHeaderText("Nom ou type de l'attribut non renseigné");
                    alert.setContentText("Veuillez renseigner le nom et le type de l'attribut.");
                    alert.showAndWait();
                    return;
                }
                // ajouter l'attribut
                modele.ajouterAttribut(nomClasse, attributNameField.getText(), attributTypeField.getText(), modifier);
                popupStage.hide();
            });

            Scene scene = new Scene(layout);
            popupStage.setScene(scene);

            popupStage.show();
        });
        return item;
    }

    /**
     * récupérer le modifier correspondant à la visibilité, si l'attribut est static et si l'attribut est abstract
     * @param visibilite
     * @param isStatic
     * @param isAbstract
     * @return
     */
    public int getModifier(String visibilite, boolean isStatic, boolean isAbstract) {
        int modifier = 0;
        switch (visibilite) {
            case "public":
                modifier = 1;
                break;
            case "protected":
                modifier = 4;
                break;
            case "private":
                modifier = 2;
                break;
        }
        if (isStatic) {
            modifier = modifier | 8;
        }
        if (isAbstract) {
            modifier = modifier | 1024;
        }
        return modifier;
    }

    /**
     * Créer un item pour ajouter une relation entre deux classes
     * @return
     */
    private MenuItem itemAjouterRelation(){
        MenuItem item = new MenuItem("Ajouter une relation");
        item.setOnAction(e -> {
            // Créer une nouvelle fenêtre popup
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

            // choisir la classe cible
            ComboBox<String> classeSource = new ComboBox<>();
            classeSource.getItems().addAll(nomsClasses.keySet());

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
                // récupérer les informations de la relation
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
                // ajouter la relation
                modele.ajouterRelation(cible, source, type, cardinaliteCible, cardinaliteSource, nomRelation);
                popupStage.hide();
            });

            typeRelation.setOnAction(e2 -> {
                // on change les options en fonction du type de relation choisi
                String type = typesRelation.get(typeRelation.getValue());
                if (type.equals(Fleche.IMPLEMENTS)) {
                    labelCibleClasse.setText("Classe implémentée:");
                    labelSourceClasse.setText("Interface implémentant:");
                    cardinaliteCibleField.setDisable(true);
                    cardinaliteSourceField.setDisable(true);
                    nomRelationField.setDisable(true);
                    cardinaliteCibleField.clear();
                    cardinaliteSourceField.clear();
                    nomRelationField.setText("\"\"");
                } else if (type.equals(Fleche.EXTENDS)) {
                    labelCibleClasse.setText("Classe mère:");
                    labelSourceClasse.setText("Classe fille:");
                    cardinaliteCibleField.setDisable(true);
                    cardinaliteSourceField.setDisable(true);
                    nomRelationField.setDisable(true);
                    cardinaliteCibleField.clear();
                    cardinaliteSourceField.clear();
                    nomRelationField.setText("\"\"");
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

    // Getters et Setters
    public String getNomClasse() {
        return nomClasse;
    }
}
