package projet.controleur;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;
import projet.Modele;
import projet.classes.Classe;
import projet.classes.VueClasse;

import java.util.Iterator;
import java.util.ListIterator;

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
            x=mouseEvent.getX();
            y=mouseEvent.getY();
        }

        if (mouseEvent.getEventType()==MouseEvent.MOUSE_RELEASED) {
            if(!modele.getClasses().isEmpty()){

                Iterator<Classe> it = modele.getClasses().values().iterator();

                while(it.hasNext()){
                    Classe c = it.next();
                    if (c.getX() <= x && c.getY() <= y && c.getX() + c.getLargeur() >= x && c.getY() + c.getLongueur() >= y) {
                        c.setX(mouseEvent.getX()+c.getX()-x);
                        c.setY(mouseEvent.getY()+c.getY()-y);
                        modele.notifierObservateur();
                        break;
                    }
                }
            }
        }
    }
}
