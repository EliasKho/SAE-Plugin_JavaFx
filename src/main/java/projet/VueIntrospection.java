package projet;

import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;

public class VueIntrospection extends ScrollPane implements Observateur{
    private Modele modele;
    private Label introspection;

    public VueIntrospection(Modele m) {
        this.modele = m;
        actualiser(m);
    }

    public void actualiser(Sujet s){
        Modele m = (Modele) s;
        introspection = new Label(m.getIntrospection());
        introspection.setLayoutX(10);
        introspection.setLayoutY(0);
        this.setContent(introspection);
    }
}
