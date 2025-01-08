package projet.controleur;

import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import projet.Modele;

public class ControlerVues {
    private Modele modele;

    public ControlerVues(Modele modele) {
        this.modele = modele;
    }

    public void afficherVueUML(GridPane gridPane){
        if (modele.getVue().equals("classique")){
            modele.setVue("UML");
            ControlerImage controlerImage = new ControlerImage(modele);
            ControlerClic controlerClic = new ControlerClic(modele);
            controlerImage.captureImageUML();
            ImageView img = new ImageView("file:Diag.png");
//            img.fitWidthProperty().bind(gridPane.widthProperty());
//            img.fitHeightProperty().bind(gridPane.heightProperty());
//            La taille (hauteur et largeur) max de l'image est
            img.setPreserveRatio(true);
            img.setFitHeight(gridPane.getChildren().get(1).getBoundsInLocal().getHeight()-30);
            img.setOnMouseClicked(controlerClic);
            gridPane.getChildren().remove(2);
            gridPane.add(img, 1,1);
            GridPane.setHalignment(img, HPos.CENTER);
            GridPane.setValignment(img, VPos.CENTER);
            GridPane.setHgrow(img, Priority.ALWAYS);
            GridPane.setVgrow(img, Priority.ALWAYS);
        }
    }

    public void afficherVueClasse(GridPane gridPane){
//        gridPane.getChildren().remove(2);
        if (modele.getVue().equals("UML")){
            modele.setVue("classique");
            gridPane.getChildren().remove(2);
            gridPane.add(modele.getVueClasse(), 1, 1);
        }
    }
}
