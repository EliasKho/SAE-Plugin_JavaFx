package projet.controleur;

import javafx.event.EventHandler;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import projet.Modele;
import projet.arborescence.VueArborescence;
import projet.classes.Classe;

public class ControlerDrag implements EventHandler<DragEvent> {
    private Modele modele;

    public ControlerDrag(Modele modele) {
        this.modele = modele;
    }

    @Override
    public void handle(DragEvent dragEvent) {
        dragEvent.consume();

        // on fait le nécessaire pour pouvoir déplacer une classe (VBox) sur le diagramme
        // on récupère les coordonnées du clic

        if (dragEvent.getEventType() == DragEvent.DRAG_OVER) {
            // on verifie si l'evenement est un setOnDragOver
            if (dragEvent.getGestureSource() != dragEvent.getTarget() && dragEvent.getDragboard().hasString()) {
                dragEvent.acceptTransferModes(TransferMode.MOVE);
            }
        }

        if (dragEvent.getEventType() == DragEvent.DRAG_DROPPED) {
            boolean success = false;
            // on verifie si l'evenement est un setOnDragDropped et on vérifie si l'élément placer est sur l'arborescence
            if (dragEvent.getGestureSource() != dragEvent.getTarget() && dragEvent.getDragboard().hasString() && dragEvent.getGestureTarget() instanceof VueArborescence){
                String nomClasse = dragEvent.getDragboard().getString();
                //Petite vérification pour être sûr si la classe est bien présente dans la fenêtre
                if(modele.isInDiagram(nomClasse)){
                    modele.supprimerClasse(nomClasse);
                    success = true;
                }
            }
            //Si ce n'est pas sur l'arborescence cela déplace ou place la classe dans la fenêtre
            else{
                if (dragEvent.getGestureSource() != dragEvent.getTarget() && dragEvent.getDragboard().hasString()) {
                    // on récupère la classe
                    String nomClasse = dragEvent.getDragboard().getString();

                    if (modele.isInDiagram(nomClasse)) {

                        Classe classe = modele.getClasses().get(nomClasse);

                        // on modifie les coordonnées de la classe (on la déplace tel que la position de la souris soit le centre)
                        double x = dragEvent.getX() - classe.getLargeur() / 2;
                        double y = dragEvent.getY() - classe.getLongueur() / 2;
                        if (x < 0) x = 0;
                        if (y < 0) y = 0;
                        classe.setX(x);
                        classe.setY(y);

                        success = true;
                    } else {
                        double x = dragEvent.getX();
                        double y = dragEvent.getY();
                        modele.ajouterClasse(nomClasse, x, y);
                        success = true;
                    }
                }
            }

            dragEvent.setDropCompleted(success);
            dragEvent.consume();
            modele.notifierObservateur();
        }
    }
}
