package projet;

import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

public class VueIntrospection extends Pane implements Observateur{
    private Modele modele;

    public VueIntrospection(Modele m) {
        this.modele = m;
        actualiser(m);
    }

    public void actualiser(Sujet s){
        Modele m = (Modele) s;
        Label label = new Label(m.getIntrospection());
        label.setLayoutX(10);
        label.setLayoutY(0);
        this.getChildren().clear();
        this.getChildren().add(label);
    }
}
