package projet;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Popup;
import javafx.stage.Stage;
import projet.arborescence.Dossier;
import projet.arborescence.VueArborescence;
import projet.classes.VueClasse;
import projet.controleur.*;

import java.io.File;
import java.io.IOException;

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
        Dossier dossier = new Dossier(new File("C:\\Users\\leofo\\Desktop\\S3 BUT\\QDev S3\\Qdev\\src/"));
        Modele modele = new Modele(dossier);

        // controleurs
        ControlerClic controlerClic = new ControlerClic(modele);
        ControlerDrag controlerDrag = new ControlerDrag(modele);
        ControlerVues controlerVues = new ControlerVues(modele);
        ControlerImage controlerImage = new ControlerImage(modele);

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

        Button buttonAjoutClasse = new Button("Ajouter une classe");
        buttonAjoutClasse.setOnAction(e -> {
            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.setTitle("Ma fenêtre popup");

            VBox layout = new VBox(10);
            layout.setPadding(new Insets(20));
            TextField textField = new TextField();
            Button addButton = new Button("Ajouter");
            layout.getChildren().addAll(textField, addButton);

            Scene scene = new Scene(layout);
            popupStage.setScene(scene);

            addButton.setOnAction(f -> {
                String text = textField.getText();
                //TODO
                // Ne fonctionne pas pour les packages
                modele.ajouterClasse("projet."+text+".java",0,0);
                popupStage.close();
            });
            EventHandler<ActionEvent> handleInput = g -> {
                String text = textField.getText();
                //TODO
                // Ne fonctionne pas pour les packages
                modele.ajouterClasse("projet."+text+".java",0,0);
                popupStage.close();
            };
            textField.setOnAction(handleInput);
            popupStage.show();
        });

        Button buttonVueClassique = new Button("VueClassique");

        Button buttonGenererCodeSource = new Button("Générer le code source correspondant au diagramme");
        buttonGenererCodeSource.setStyle("-fx-background-color: #2c8cff; -fx-text-fill: white;");


        buttonGenererCodeSource.setOnMousePressed(e -> {
            buttonGenererCodeSource.setStyle("-fx-background-color: #001e42; -fx-text-fill: white;");
        });
        buttonGenererCodeSource.setOnMouseReleased(e -> {
            buttonGenererCodeSource.setStyle("-fx-background-color: #2c8cff; -fx-text-fill: white;");
        });

        buttonGenererCodeSource.setOnAction(e -> {
//            buttonGenererCodeSource.setStyle("-fx-background-color: #001e42; -fx-text-fill: white;");
//            modele.genererCodeSource();
        });


        Button buttonSave = new Button("Save");

        Button buttonLoad = new Button("Load");

        HBox vues = new HBox(buttonVueClassique, buttonVueUML, buttonSave, buttonLoad);
        GridPane gp = new GridPane();

        buttonVueClassique.setStyle("-fx-background-color: #001e42; -fx-text-fill: white;");
        buttonSave.setOnAction(e -> {
            modele.saveToFile("modele.sav");
        });
        buttonLoad.setOnAction(e ->{
            modele.loadFromFile("modele.sav");
        });
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
        gp.add(buttonAjoutClasse, 0, 0);
        gp.add(vues, 1, 0);
        gp.add(arborescence, 0, 1);
        gp.add(scrollpane, 1, 1);
        gp.add(buttonGenererCodeSource, 0,2, 2,1);
        // le bouton prend toute la largeur de l'écran

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
        buttonGenererCodeSource.setPrefWidth(scene.getWidth());
        modele.setScene(scene);
        modele.setVueClasse(scrollpane);
        stage.setTitle("Diagramme Class Makker");
        stage.setScene(scene);
        stage.show();
    }
}