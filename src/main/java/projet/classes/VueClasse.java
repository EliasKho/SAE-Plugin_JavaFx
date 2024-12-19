package projet.classes;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import projet.Modele;
import projet.Observateur;
import projet.Sujet;

import java.util.List;

public class VueClasse extends Pane implements Observateur {

    Modele modele;

    VueClasse(Modele modele) {
        this.modele = modele;
    }

    @Override
    public void actualiser(Sujet s) {
        // Récupérer les attributs et méthodes de la classe
        Classe classe = this.modele.getClasses().get(0);
        List<Attribut> attributs = classe.getAttributs();
        List<Methode> methodes = classe.getMethodes();

        // Effacer le pane avant de redessiner
        this.getChildren().clear();

        // Positionnement des éléments
        double yPosition = 10;

        // 1. Affichage du nom de la classe
        Text classNameText = new Text(classe.getNom());
        classNameText.setX(10); // Position horizontale
        classNameText.setY(yPosition);
        this.getChildren().add(classNameText);

        // 2. Création d'un rectangle pour délimiter la classe
        Rectangle classBox = new Rectangle(200, 50 + (attributs.size() * 20) + (methodes.size() * 20));
        classBox.setX(5);
        classBox.setY(5);
        classBox.setFill(Color.LIGHTGRAY);
        classBox.setStroke(Color.BLACK);
        this.getChildren().add(classBox);

        // 3. Affichage des attributs
        yPosition += 30; // Espacement après le nom de la classe
        for (Attribut attribut : attributs) {
            Text attributText = new Text(attribut.getNom());
            attributText.setX(10); // Position horizontale
            attributText.setY(yPosition);
            this.getChildren().add(attributText);
            yPosition += 20; // Espacement vertical entre les attributs
        }

        // 4. Affichage des méthodes
        for (Methode methode : methodes) {
            Text methodeText = new Text(methode.getNom());
            methodeText.setX(10); // Position horizontale
            methodeText.setY(yPosition);
            this.getChildren().add(methodeText);
            yPosition += 20; // Espacement vertical entre les méthodes
        }
    }
}
