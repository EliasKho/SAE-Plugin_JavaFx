package projet;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
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
        Modele modele = new Modele(dossier);

        // controleurs
        ControlerClic controlerClic = new ControlerClic(modele);
        ControlerDrag controlerDrag = new ControlerDrag(modele);
        ControlerDragDrop controlerDragDrop = new ControlerDragDrop(modele);
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

        Button scButton = new Button("Sauvegarder image");
        scButton.setOnAction(e -> {
            controlerImage.captureImage();
        });
        scrollpane.setOnDragOver(controlerDrag);
        scrollpane.setOnDragDropped(controlerDrag);


        //scrollpane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        arborescence.setStyle("-fx-border-color: black; -fx-border-width: 2;");
        scrollpane.setStyle("-fx-border-color: black; -fx-border-width: 2;");

        hbox.getChildren().add(arborescence);
        hbox.getChildren().add(scrollpane);

        Button buttonVueUML = new Button("VueUML");



        Button buttonVueClassique = new Button("VueClassique");

        HBox vues = new HBox(buttonVueClassique, buttonVueUML);
        GridPane gp = new GridPane();
        buttonVueClassique.setOnAction(e -> {
            controlerVues.afficherVueClasse(gp);
        });
        buttonVueUML.setOnAction(e -> {
            controlerVues.afficherVueUML(gp);
        });
        gp.add(vues, 1, 0);
        gp.add(arborescence, 0, 1);
        gp.add(scrollpane, 1, 1);

        ImageView img = new ImageView("file:Diag.png");
        img.fitWidthProperty().bind(gp.widthProperty());
        img.fitHeightProperty().bind(gp.heightProperty());
        img.setPreserveRatio(true);
//        gp.add(img, 1,1);

        //hbox.setSpacing(10); // Ajouter un espacement entre les éléments

        Scene scene = new Scene(gp, 1000, 600);
        modele.setScene(scene);
        modele.setVueClasse(scrollpane);
        arborescence.setMinWidth(scene.getWidth()*25/100);
        arborescence.setMaxWidth(scene.getWidth()*25/100);

        scrollpane.setMinWidth(scene.getWidth()*75/100);
        scrollpane.setMaxWidth(scene.getWidth()*75/100);

        stage.setTitle("Diagramme Class Makker");
        stage.setScene(scene);
        stage.show();
    }
}