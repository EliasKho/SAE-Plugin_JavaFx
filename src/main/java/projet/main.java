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
/*
        String[] ingredients = {"Fromage", "Champignons", "Chorizo", "Oeuf", "Oignons", "Olives noires", "Olives vertes", "Roquette"};
        // String[] fidelite = {"Nouveau client", "Cliente avec carte", "Client adhérent"};
        Button[] bIngr;
        BorderPane bp = new BorderPane();
        bp.setPadding(new Insets(10));


        // Panneau situe au nord de l'IG contenant les 2 boutons
        // permettant le choix de la base des pizzas et le niveau de fidelite du client
        HBox pnord = new HBox(20);
        pnord.setPadding(new Insets(10));
        pnord.setAlignment(Pos.CENTER);

        ComboBox<String> choixFidelite = new ComboBox<String>();
        choixFidelite.getItems().add("Nouveau client");
        choixFidelite.getItems().add("Client adhérent");
        choixFidelite.getItems().add("Cliente avec carte");
        choixFidelite.setValue("Nouveau client");

        Button addPizzaCreme = new Button(" Ajouter une pizza fond creme ");
        Button addPizzaTomate = new Button(" Ajouter une pizza fond tomate ");
        pnord.getChildren().addAll(choixFidelite, addPizzaCreme, addPizzaTomate);
        bp.setTop(pnord); //place pnord en haut de l'IG


        // Panneau au centre de l'IG contenant la vision du choix des pizzas
        // ainsi que les boutons pour ajouter des ingredients
        BorderPane pcentral = new BorderPane();
        pcentral.setMaxHeight(300);

        //--> Le panneau avec la vision des images des pizzas
        //--> Le panneau contenant les boutons des ingredrients
        GridPane pingr = new GridPane();
        pingr.setAlignment(Pos.CENTER);
        pingr.setHgap(10);
        bIngr = new Button[8];
        for (int i = 0; i < ingredients.length; i++) {
            bIngr[i] = new Button(ingredients[i]);
            bIngr[i].setMinSize(100, 40);
            pingr.add(bIngr[i], i, 0);
        }
        // pingr.setPreferredSize(new Dimension(935,50));
        pcentral.setBottom(pingr);
        bp.setCenter(pcentral); //place pcentral au centre de l'IG

        // Panneau au sud de l'IG dans lequel se trouve l'affichage
        // du contenu de la commande et son prix
        BorderPane psud = new BorderPane();
        psud.setMinHeight(300);

        Label commtxt = new Label("Aucune commande en cours");
        commtxt.setFont(new Font("Times", 14));
        //  commtxt.setPreferredSize(new Dimension(935,200));
        psud.setCenter(commtxt);
        //Mettre un panneau VBox
        VBox vb = new VBox();
        vb.setAlignment(Pos.CENTER);
        Label txtBas = new Label("Pas de commande en cours");
        txtBas.setFont(new Font("Times", 16));
        txtBas.setTextAlignment(TextAlignment.CENTER);
        vb.getChildren().add(txtBas);projet.Modele
        psud.setBottom(vb);projet.Modele
        bp.setBottom(psud);


        Scene scene = new Scene(bp, 935, 670);
        stage.setTitle("Commande de pizzas");
        stage.setScene(scene);
        stage.show();
    }*/
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