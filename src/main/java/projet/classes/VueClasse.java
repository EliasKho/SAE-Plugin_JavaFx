package projet.classes;

import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import projet.Modele;
import projet.Observateur;
import projet.Sujet;

import java.util.List;

public class VueClasse extends ScrollPane implements Observateur {

    private Modele modele;

    public VueClasse(Modele modele) {
        this.modele = modele;
    }

    @Override
    public void actualiser(Sujet s) {
        if (this.modele.getClasses().isEmpty()){
            // affichage d'un écran vide
            this.getChildren().clear();
            return;
        }
        // Récupérer la classe et ses attributs et méthodes
        Classe classe = this.modele.getClasses().getLast();
        List<Attribut> attributs = classe.getAttributs();
        List<Methode> methodes = classe.getMethodes();

        // Effacer le pane avant de redessiner
        this.getChildren().clear();

        // Position de départ pour le premier rectangle (nom de la classe)
        double yPosition = 20;
        double xPosition = 10;

        // 1. Création du rectangle pour le nom de la classe
        Rectangle classBox = new Rectangle(200, 30);
        classBox.setX(xPosition);
        classBox.setY(yPosition);
        classBox.setFill(Color.LIGHTGRAY);
        classBox.setStroke(Color.BLACK);
        this.getChildren().add(classBox);

        // Affichage du nom de la classe dans le rectangle
        String nom ="";
        if(classe.isInterface()){
            nom+="interface ";
        }
        else {
            if(classe.isAbstract()){
                nom+="abstract ";
            }
            nom+="class ";
        }
        nom += classe.getNom();
        Text classNameText = new Text(nom);
        classNameText.setX(xPosition + 5); // Décalage légèrement à droite pour ne pas être collé au bord
        classNameText.setY(yPosition + 20); // Positionner verticalement au centre du rectangle
        this.getChildren().add(classNameText);

        // 2. Positionnement du rectangle pour les attributs
        yPosition += 30; // Déplacer un peu plus bas pour les attributs

        // Création du rectangle pour les attributs
        Rectangle attributesBox = new Rectangle(200, 20 + attributs.size() * 20); // Ajuste la hauteur en fonction du nombre d'attributs
        attributesBox.setX(xPosition);
        attributesBox.setY(yPosition);
        attributesBox.setFill(Color.LIGHTYELLOW);
        attributesBox.setStroke(Color.BLACK);
        this.getChildren().add(attributesBox);

        // Affichage des attributs dans le rectangle
        double attributYPosition = yPosition + 20; // Commence juste après le rectangle
        for (Attribut attribut : attributs) {
            String nomAttribut = attribut.getString();
            Text attributText = new Text(nomAttribut);
            attributText.setX(xPosition + 5); // Décalage légèrement à droite pour les attributs
            attributText.setY(attributYPosition);
            this.getChildren().add(attributText);
            attributYPosition += 20; // Espacement entre les attributs
        }

        // 3. Positionnement du rectangle pour les méthodes
        yPosition += attributesBox.getHeight(); // Positionner juste après les attributs

        // Création du rectangle pour les méthodes
        Rectangle methodsBox = new Rectangle(200, 20 + methodes.size() * 20); // Ajuste la hauteur en fonction du nombre de méthodes
        methodsBox.setX(xPosition);
        methodsBox.setY(yPosition);
        methodsBox.setFill(Color.LIGHTGREEN);
        methodsBox.setStroke(Color.BLACK);
        this.getChildren().add(methodsBox);

        // Affichage des méthodes dans le rectangle
        double methodYPosition = yPosition + 20; // Commence juste après le rectangle
        for (Methode methode : methodes) {
            String nomMethode = methode.getString();
            Text methodeText = new Text(nomMethode);
            methodeText.setX(xPosition + 5); // Décalage légèrement à droite pour les méthodes
            methodeText.setY(methodYPosition);
            this.getChildren().add(methodeText);
            methodYPosition += 20; // Espacement entre les méthodes
        }
    }
}
