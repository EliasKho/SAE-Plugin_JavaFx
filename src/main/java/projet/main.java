package projet;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.*;
import projet.arborescence.Dossier;
import projet.arborescence.VueArborescence;
import projet.classes.VueClasse;
import projet.controleur.*;

import java.io.File;

public class main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {

//        Supprimer le fichier Diag.png
        File file = new File("Diag.png");
        file.delete();
        // création du modèle
        Dossier dossier = new Dossier(new File("src/main/java/projet"));
        Modele modele = new Modele();
        // on met un dossier par défaut le temps de travailler, sinon on devra sélectionner un dossier à chaque fois
        modele.setRacine(dossier);

        // controleurs
        ControlerClic controlerClic = new ControlerClic(modele);
        ControlerDrag controlerDrag = new ControlerDrag(modele);
        ControlerVues controlerVues = new ControlerVues(modele);

        HBox hbox = new HBox();
        VueArborescence arborescence = new VueArborescence(modele, controlerClic);

//        modele.enregistrerObservateur((arborescence));
        VueUML vueUML = new VueUML(modele);
        modele.enregistrerObservateur(vueUML);
        VueClasse scrollpane = new VueClasse(modele, controlerClic);
        modele.enregistrerObservateur(scrollpane);

        scrollpane.setOnMouseClicked(controlerClic);


        scrollpane.setOnDragOver(controlerDrag);
        scrollpane.setOnDragDropped(controlerDrag);
        arborescence.setOnDragOver(controlerDrag);
        arborescence.setOnDragDropped(controlerDrag);


        //scrollpane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        arborescence.setStyle("-fx-border-color: black; -fx-border-width: 2;");
        scrollpane.setStyle("-fx-border-color: black; -fx-border-width: 2;");

        hbox.getChildren().add(arborescence);
        hbox.getChildren().add(scrollpane);

        Button buttonVueUML = new Button("VueUML");
        buttonVueUML.setStyle("-fx-background-color: #2c8cff; -fx-text-fill: white;");


        Button buttonVueClassique = new Button("VueClassique");

        HBox vues = new HBox(buttonVueClassique, buttonVueUML);
        GridPane gp = new GridPane();

        buttonVueClassique.setStyle("-fx-background-color: #001e42; -fx-text-fill: white;");

        buttonVueClassique.setOnAction(e -> {
            controlerVues.afficherVueClasse(gp);
            buttonVueClassique.setStyle("-fx-background-color: #001e42; -fx-text-fill: white;");
            buttonVueUML.setStyle("-fx-background-color: #2c8cff; -fx-text-fill: white;");
        });
        buttonVueUML.setOnAction(e -> {
            controlerVues.afficherVueUML(gp);
            buttonVueUML.setStyle("-fx-background-color: #001e42; -fx-text-fill: white;");
            buttonVueClassique.setStyle("-fx-background-color: #2c8cff; -fx-text-fill: white;");
        });

        MenuButton menuButtonOptions = new MenuButton("Options");

        // Créer les éléments du menu
        MenuItem chooseDirectory = new MenuItem("Choisir un répertoire");
        MenuItem saveDiagram = new MenuItem("Sauvegarder le diagramme");
        MenuItem loadDiagram = new MenuItem("Charger un diagramme");
        MenuItem affichage = new MenuItem("Modifier l'affichage");

        // Ajouter des actions aux éléments du menu
        chooseDirectory.setOnAction(e -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Choisir un répertoire");
            File selectedDirectory = directoryChooser.showDialog(stage);
            if (selectedDirectory != null) {
                Dossier dos = new Dossier(selectedDirectory);
                modele.setRacine(dos);
                arborescence.actualiser(modele);
            }
        });

        affichage.setOnAction(e -> {
            // Créer une nouvelle fenêtre popup
            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.setTitle("Modifier l'affichage");

            // Créer un VBox pour contenir les CheckBox et le bouton de validation
            VBox layout = new VBox(10);
            layout.setPadding(new Insets(20));

            // Créer les CheckBox
            CheckBox option1 = new CheckBox("Afficher les getters/setters/constructeurs");
            CheckBox option2 = new CheckBox("Afficher les attributs hérités");
            CheckBox option3 = new CheckBox("Afficher les flèches");

            // Créer un bouton pour valider les choix
            Button validateButton = new Button("Valider");

            // Ajouter les CheckBox et le bouton au layout
            layout.getChildren().addAll(option1, option2, option3, validateButton);

            // Créer une scène pour la fenêtre popup
            Scene popupScene = new Scene(layout);
            popupStage.setScene(popupScene);

            // Action du bouton valider
            validateButton.setOnAction(action -> {
                // Nettoyer les options précédentes affichées dans le GridPane
                gp.getChildren().removeIf(node -> GridPane.getRowIndex(node) != null && GridPane.getRowIndex(node) >= 2);

                // Créer un nouveau GridPane pour organiser les options sélectionnées
                GridPane optionsGrid = new GridPane();
                optionsGrid.setPadding(new Insets(10));
                optionsGrid.setHgap(10);
                optionsGrid.setVgap(10);

                // Ajouter les options sur la même ligne, avec différentes colonnes
                int col = 0; // Initialiser la première colonne

                if (option1.isSelected()) {
                    optionsGrid.add(new Label("Affichage des getters/setters"), col++, 0);
                    modele.setVoirGetSet(true);
                } else {
                    optionsGrid.add(new Label("Masquage des getters/setters"), col++, 0);
                    modele.setVoirGetSet(false);
                }
                if (option2.isSelected()) {
                    optionsGrid.add(new Label("Affichage des attributs hérités"), col++, 0);
                    modele.setVoirAttributsHerites(true);
                } else {
                    optionsGrid.add(new Label("Masquage des attributs hérités"), col++, 0);
                    modele.setVoirAttributsHerites(false);
                }
                if (option3.isSelected()) {
                    optionsGrid.add(new Label("Affichage des flèches"), col++, 0);
                    modele.setVoirFleches(true);
                } else {
                    optionsGrid.add(new Label("Masquage des flèches"), col++, 0);
                    modele.setVoirFleches(false);
                }

                // Ajouter les nouvelles options au GridPane principal
                gp.add(optionsGrid, 0, 2, 2, 1);

                // Fermer la fenêtre popup
                popupStage.close();
            });

            // Afficher la fenêtre popup
            popupStage.showAndWait();
        });



        saveDiagram.setOnAction(e -> {
            // choisir où sauvegarder le nouveau fichier et son nom
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Sauvegarder le diagramme");
            File selectedFile = fileChooser.showSaveDialog(stage);
            if (selectedFile!= null) {
                modele.saveToFile(selectedFile.getAbsolutePath());
            }
        });

        loadDiagram.setOnAction(e -> {
            // choisir le fichier à charger
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Choisir un fichier");
            File selectedFile = fileChooser.showOpenDialog(stage);
            if (selectedFile!= null && selectedFile.exists() && selectedFile.getName().endsWith(".sav")) {
                modele.loadFromFile(selectedFile.getAbsolutePath());
                arborescence.actualiser(modele);
            }
        });

        // Ajouter les éléments au MenuButton
        menuButtonOptions.getItems().addAll(chooseDirectory, saveDiagram, loadDiagram, affichage);


        gp.add(menuButtonOptions, 0, 0);
//        gp.add(buttonAjoutClasse, 0, 0);
        gp.add(vues, 1, 0);
        gp.add(arborescence, 0, 1);
        gp.add(scrollpane, 1, 1);

        ImageView img = new ImageView("file:Diag.png");
        img.fitWidthProperty().bind(gp.widthProperty());
        img.fitHeightProperty().bind(gp.heightProperty());
        img.setPreserveRatio(true);
//        gp.add(img, 1,1);

        // Configurer les tailles dynamiques
        arborescence.minWidthProperty().bind(gp.widthProperty().multiply(0.2));
        arborescence.maxWidthProperty().bind(gp.widthProperty().multiply(0.2));

        scrollpane.minWidthProperty().bind(gp.widthProperty().multiply(0.8));
        scrollpane.maxWidthProperty().bind(gp.widthProperty().multiply(0.8));

        scrollpane.prefHeightProperty().bind(gp.heightProperty());
        arborescence.prefHeightProperty().bind(gp.heightProperty());

        //hbox.setSpacing(10); // Ajouter un espacement entre les éléments

        Scene scene = new Scene(gp, 1000, 600);
        modele.setScene(scene);
        modele.setVueClasse(scrollpane);
        stage.setTitle("Diagramme Class Makker");
        stage.setScene(scene);
        stage.show();
    }
}