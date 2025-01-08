package projet.controleur;

import javafx.event.EventHandler;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import projet.Modele;

public class ControlerDragDrop implements EventHandler<DragEvent> {

    private Modele modele;
    private String nomClasse;

    public ControlerDragDrop(Modele m){
        this.modele = m;
    }

    @Override
    public void handle(DragEvent event) {
        Dragboard db = event.getDragboard();
        boolean success = false;
        String text;
        text = db.getString();
        if (db.hasString()) {
            double x = event.getX();
            double y = event.getY();
            modele.ajouterClasse(text, x, y);
            success = true;
        }
        event.setDropCompleted(success);
        event.consume();
    }
}
