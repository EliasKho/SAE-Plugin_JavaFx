package projet.controleur;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;
import projet.Modele;
import projet.classes.Classe;
import projet.classes.VueClasse;

public class ControlerDrag implements EventHandler<MouseEvent> {
    private Modele modele;
    private double x;
    private double y;

    public ControlerDrag(Modele modele) {
        this.modele = modele;
    }

    @Override
    public void handle(MouseEvent mouseEvent) {
        if (mouseEvent.getEventType()==MouseEvent.MOUSE_PRESSED) {
            x = mouseEvent.getSceneX()-((Node)mouseEvent.getSource()).getTranslateX();
            y = mouseEvent.getSceneY()-((Node)mouseEvent.getSource()).getTranslateY();
        }
        if (mouseEvent.getEventType()==MouseEvent.MOUSE_DRAGGED) {
            ((Node)mouseEvent.getSource()).setTranslateX(mouseEvent.getSceneX()-x);
            ((Node)mouseEvent.getSource()).setTranslateY(mouseEvent.getSceneY()-y);
        }
    }
}
