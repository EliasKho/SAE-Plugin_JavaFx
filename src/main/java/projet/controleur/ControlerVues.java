package projet.controleur;

import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import projet.Modele;

public class ControlerVues {
    private Modele modele;

    public ControlerVues(Modele modele) {
        this.modele = modele;
    }

//    controler qui permet de changer de vues, afficher les classes en javafx ou l'image plantuml (Diag.png)
    public void changeVue(String vue){
        switch (vue){
            case "UML":
                System.out.println("Affichage de l'image UML");
                break;
            case "Classe":
                System.out.println("Affichage des classes");
                break;
            default:
                System.out.println("Erreur de vue");
        }
    }

    public void afficherVueUML(GridPane gridPane){
        if (modele.getVue().equals("classique")){
            modele.setVue("UML");
            ControlerImage controlerImage = new ControlerImage(modele);
            controlerImage.captureImageUML();
            ImageView img = new ImageView("file:Diag.png");
            img.fitWidthProperty().bind(gridPane.widthProperty());
            img.fitHeightProperty().bind(gridPane.heightProperty());
            img.setPreserveRatio(true);
            gridPane.getChildren().remove(2);
            gridPane.add(img, 1,1);
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
