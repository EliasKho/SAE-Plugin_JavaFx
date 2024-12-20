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
            x=mouseEvent.getX();
            y=mouseEvent.getY();
        }

        if (mouseEvent.getEventType()==MouseEvent.MOUSE_RELEASED) {
            if(!modele.getClasses().isEmpty()){
                for(Classe c : modele.getClasses()){
                    if(c.getX()<=x && c.getY()<=y && c.getX()+c.getLargeur()>=x && c.getY()+c.getLongueur()>=y){
                        c.setX(mouseEvent.getX());
                        c.setY(mouseEvent.getY());
                        modele.notifierObservateur();
                    }
                }
            }
        }
    }
}
