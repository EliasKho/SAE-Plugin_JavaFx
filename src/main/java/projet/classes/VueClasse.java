package projet.classes;

import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import projet.Modele;
import projet.Observateur;
import projet.Sujet;

import java.awt.*;
import java.util.List;

public class VueClasse extends Pane implements Observateur {

    private Modele modele;

    public VueClasse(Modele modele) {
        this.modele = modele;
        this.getChildren().clear();
    }

    @Override
    public void actualiser(Sujet s) {
        this.getChildren().clear();
        if(!modele.getClasses().isEmpty()) {
            for (Classe classe : modele.getClasses()) {
                VBox container = new VBox();
                // Récupérer la classe et ses attributs et méthodes
                List<Attribut> attributs = classe.getAttributs();
                List<Methode> methodes = classe.getMethodes();

                // Effacer la VBox avant de redessiner
                container.getChildren().clear();

                // 1. Nom de la classe
                Text classNameText = new Text();
                String nom = (classe.isInterface() ? "interface " : (classe.isAbstract() ? "abstract " : "class ")) + classe.getNom();
                classNameText.setText(nom);

                // 2. Créer un StackPane pour la classe
                StackPane classBoxContainer = new StackPane();
                classBoxContainer.setStyle("-fx-background-color: lightgray; -fx-padding: 5px;");
                classBoxContainer.getChildren().add(classNameText);

                // 3. Attributs
                StackPane attributesContainer = new StackPane();
                attributesContainer.setStyle("-fx-background-color: lightyellow; -fx-padding: 5px;");

                String strAtr = "";
                for (Attribut attribut : attributs) {
                    strAtr += attribut.getString() + "\n";
                }
                Text attributesText = new Text(strAtr);
                attributesContainer.getChildren().add(attributesText);

                // 4. Méthodes
                StackPane methodsContainer = new StackPane();
                methodsContainer.setStyle("-fx-background-color: lightgreen; -fx-padding: 5px;");

                String strMet = "";
                for (Methode methode : methodes) {
                    strMet += methode.getString() + "\n";
                }
                Text methodsText = new Text(strMet);
                methodsContainer.getChildren().add(methodsText);

                // Ajouter les containers au VBox
                container.getChildren().add(classBoxContainer);
                container.getChildren().add(attributesContainer);
                container.getChildren().add(methodsContainer);

                // Ajuster la largeur de la classe en fonction des containers
                container.layout();  // Forcer la mise en page des éléments
                double largeurMax = Math.max(
                        classBoxContainer.getBoundsInParent().getWidth(),
                        Math.max(attributesContainer.getBoundsInParent().getWidth(),
                                methodsContainer.getBoundsInParent().getWidth())
                );

                // Mettre à jour la largeur de la classe
                classe.setLargeur(largeurMax);

                // Mettre à jour la hauteur totale
                double totalHeight = classBoxContainer.getHeight() + attributesContainer.getHeight() + methodsContainer.getHeight();
                classe.setLongueur(totalHeight);

                // Déplacer la classe à la position X et Y spécifiées
                container.setTranslateX(classe.getX());
                container.setTranslateY(classe.getY());
                this.getChildren().add(container);
            }
        }

    }
}
