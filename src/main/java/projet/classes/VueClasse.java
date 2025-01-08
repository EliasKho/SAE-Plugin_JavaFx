package projet.classes;

import javafx.event.EventHandler;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;
import projet.Modele;
import projet.Observateur;
import projet.Sujet;
import projet.controleur.ControlerClic;

import java.util.HashMap;
import java.util.List;

public class VueClasse extends Pane implements Observateur {

    private final Modele modele;
    private ControlerClic controlerClic;

    public VueClasse(Modele modele, ControlerClic c) {
        this.modele = modele;
        this.controlerClic = c;
        this.getChildren().clear();
    }

    @Override
    public void actualiser(Sujet s) {
        this.getChildren().clear();
        if(!modele.getClasses().isEmpty()) {
            HashMap<String,Classe> classes = modele.getClasses();
            for (String packageName : classes.keySet()) {
                Classe classe = classes.get(packageName);
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

                StringBuilder strAtr = new StringBuilder();
                for (Attribut attribut : attributs) {
                    strAtr.append(attribut.getString()).append("\n");
                }
                Text attributesText = new Text(strAtr.toString());
                attributesContainer.getChildren().add(attributesText);

                // 4. Méthodes
                StackPane methodsContainer = new StackPane();
                methodsContainer.setStyle("-fx-background-color: lightgreen; -fx-padding: 5px;");

                StringBuilder strMet = new StringBuilder();
                for (Methode methode : methodes) {
                    strMet.append(methode.getString()).append("\n");
                }
                Text methodsText = new Text(strMet.toString());
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
                classe.setLargeur(largeurMax+10); // +10 avec le padding

                // Mettre à jour la hauteur totale
                double totalHeight = classBoxContainer.getHeight() + attributesContainer.getHeight() + methodsContainer.getHeight();
                classe.setLongueur(totalHeight+30); // +30 avec le padding

                // Déplacer la classe à la position X et Y spécifiées
                container.setTranslateX(classe.getX());
                container.setTranslateY(classe.getY());

                container.setId(packageName);

                container.setOnDragDetected(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        Dragboard db = container.startDragAndDrop(TransferMode.MOVE);
                        ClipboardContent content = new ClipboardContent();
                        content.putString(packageName);
                        content.putImage(container.snapshot(null, null));
                        db.setContent(content);
                        mouseEvent.consume();
                    }
                });

                container.setOnMouseClicked(controlerClic);

                this.getChildren().add(container);
            }
        }
        actualiserRelations(s);
    }

    public void actualiserRelations(Sujet s) {
        Modele m = (Modele) s;

        for (Fleche r : m.getRelations()) {
            // Créez la flèche correspondante
            Pane arrowHead = createArrow(r);

            // Ajoutez la flèche au canevas
            this.getChildren().add(arrowHead);
        }
    }



    private Pane createArrow(Fleche r) {
        Pane arrow = new Pane();

        // Coordonnées de la relation
        double[] coord = r.getPosition();
        double startX = coord[0];
        double startY = coord[1];
        double endX = coord[2];
        double endY = coord[3];

        // Déterminez le type de relation et ses détails
        String type = r.getType(); // Exemple : "heritage", "implementation", "attribut"
        String label = r.getNom().toLowerCase(); // Nom de l'attribut (si applicable)
        String cardinalityFrom = r.getEnfantCardinalite(); // Cardinalité côté classe source
        String cardinalityTo = r.getParentCardinalite(); // Cardinalité côté classe cible

        // Calculer l'angle de la ligne
        double angle = Math.atan2(endY - startY, endX - startX);

        // Longueur de la flèche
        double arrowLength = 10;

        // Points pour les extrémités du triangle
        double x1 = endX - arrowLength * Math.cos(angle - Math.PI / 6);
        double y1 = endY - arrowLength * Math.sin(angle - Math.PI / 6);
        double x2 = endX - arrowLength * Math.cos(angle + Math.PI / 6);
        double y2 = endY - arrowLength * Math.sin(angle + Math.PI / 6);

        // Ajouter la ligne principale
        Line mainLine = new Line(startX, startY, endX, endY);
        mainLine.setStroke(Color.BLACK);

        // Ajouter les différents types de flèches
        Polygon triangle = new Polygon();
        if (Fleche.EXTENDS.equals(type)) {
            // Héritage : triangle vide
            triangle.setFill(Color.WHITE);
            triangle.setStroke(Color.BLACK);
        } else if (Fleche.IMPLEMENTS.equals(type)) {
            // Implémentation : ligne pointillée + triangle vide
            mainLine.getStrokeDashArray().addAll(10.0, 5.0); // Pointillés
            triangle.setFill(Color.WHITE);
            triangle.setStroke(Color.BLACK);
        } else if (Fleche.DEPENDANCE.equals(type)) {
            // Attribut : triangle plein avec texte et cardinalités
            triangle.setFill(Color.BLACK); // Triangle plein

            // Ajouter le texte pour le nom de l'attribut
            if (!label.isEmpty()) {
                Text attributeLabel = new Text(label);
                attributeLabel.setX((startX + endX) / 2);
                attributeLabel.setY((startY + endY) / 2 - 10); // Position au-dessus de la ligne
                arrow.getChildren().add(attributeLabel);
            }

            // Ajouter les cardinalités
            if (cardinalityFrom != null && !cardinalityFrom.isEmpty()) {
                Text cardinalityFromText = new Text(cardinalityFrom);

                // Positionner en dehors de la boîte source
                cardinalityFromText.setX(startX + 15 * Math.cos(angle)); // Décaler selon l'angle
                cardinalityFromText.setY(startY + 15 * Math.sin(angle)); // Décaler selon l'angle
                arrow.getChildren().add(cardinalityFromText);
            }
            if (cardinalityTo != null && !cardinalityTo.isEmpty()) {
                Text cardinalityToText = new Text(cardinalityTo);

                // Positionner en dehors de la boîte cible
                cardinalityToText.setX(endX - 15 * Math.cos(angle)); // Décaler selon l'angle
                cardinalityToText.setY(endY - 15 * Math.sin(angle)); // Décaler selon l'angle
                arrow.getChildren().add(cardinalityToText);
            }
        }
        triangle.getPoints().addAll(endX, endY, x1, y1, x2, y2);
        arrow.getChildren().addAll(mainLine, triangle);

        return arrow;
    }
}
