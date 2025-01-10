package projet.controleur;

import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import projet.Modele;

import java.util.Iterator;

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
            img.setPreserveRatio(true);
            img.setFitWidth(gridPane.getWidth()-gridPane.getChildren().get(2).getBoundsInLocal().getWidth()-30);
            img.setFitHeight(gridPane.getHeight()-80);
            img.setOnMouseClicked(controlerClic);

            gridPane.getChildren().remove(modele.getVueClasse());
            gridPane.add(img, 1,1);
            GridPane.setHalignment(img, HPos.CENTER);
            GridPane.setValignment(img, VPos.CENTER);
            GridPane.setHgrow(img, Priority.ALWAYS);
            GridPane.setVgrow(img, Priority.ALWAYS);
        }
    }

    public void afficherVueClasse(GridPane gridPane){
        if (modele.getVue().equals("UML")){
            modele.setVue("classique");
            Iterator<Node> it = gridPane.getChildren().iterator();
            while (it.hasNext()){
                if (it.next() instanceof ImageView){
                    it.remove();
                }
            }
            gridPane.add(modele.getVueClasse(), 1, 1);
        }
    }
}