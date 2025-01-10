package projet.controleur;

import javafx.event.EventHandler;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import projet.Modele;
import projet.arborescence.VueArborescence;
import projet.classes.Classe;
import projet.classes.VueClasse;

public class ControlerDrag implements EventHandler<DragEvent> {
    private Modele modele;

    private static double init_x=0, init_y=0;

    public static void setXY(double x, double y) {
        init_x = x;
        init_y = y;
    }

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

                // on récupère la classe
                String nomClasse = dragEvent.getDragboard().getString();

                if (modele.isInDiagram(nomClasse)) {

                    Classe classe = modele.getClasses().get(nomClasse);

                    // on modifie les coordonnées de la classe (on la déplace tel que la position de la souris soit le centre)
                    double x,y;
                    if (dragEvent.getGestureTarget() instanceof VueArborescence) {
                        x = 0;
                        y = dragEvent.getY();
                    } else {
                        x = dragEvent.getSceneX() + classe.getX() - init_x;
                        y = dragEvent.getSceneY() + classe.getY() - init_y;
                        init_x = dragEvent.getSceneX();
                        init_y = dragEvent.getSceneY();
                    }
                    classe.setX(x);
                    classe.setY(y);
                    setXY(dragEvent.getSceneX(), dragEvent.getSceneY());
                } else {
                    if (!modele.isInDiagram(nomClasse) && dragEvent.getGestureTarget() instanceof VueClasse) {
                        double x = dragEvent.getX();
                        double y = dragEvent.getY();
                        Classe c = modele.ajouterClasse(nomClasse, x, y);
                        ClipboardContent content = new ClipboardContent();
                        content.putString(c.getRealName());
                        dragEvent.getDragboard().setContent(content);
                        setXY(dragEvent.getSceneX(), dragEvent.getSceneY());
                    }
                }
                dragEvent.consume();
                modele.notifierObservateur();
            }
        }

        if (dragEvent.getEventType() == DragEvent.DRAG_DROPPED) {
            if (dragEvent.getGestureSource() != dragEvent.getTarget() && dragEvent.getDragboard().hasString()) {
                boolean success = false;
                // on verifie si l'evenement est un setOnDragDropped et on vérifie si l'élément placer est sur l'arborescence
                String nomClasse = dragEvent.getDragboard().getString();
                if (dragEvent.getX() < 0 || dragEvent.getY() < 0) {
                    //Petite vérification pour être sûr si la classe est bien présente dans la fenêtre
                    if (modele.isInDiagram(nomClasse)) {
                        modele.supprimerClasse(nomClasse);
                        success = true;
                    }
                }
                //Si ce n'est pas sur l'arborescence cela déplace ou place la classe dans la fenêtre
                else {
                    // on récupère la classe

                    if (modele.isInDiagram(nomClasse)) {

                        Classe classe = modele.getClasses().get(nomClasse);

                        // on modifie les coordonnées de la classe (on la déplace tel que la position de la souris sur la classe reste la même)
                        double x = dragEvent.getSceneX() + classe.getX() - init_x;
                        double y = dragEvent.getSceneY() + classe.getY() - init_y;
                        if (x < 0) x = 0;
                        if (y < 0) y = 0;
                        classe.setX(x);
                        classe.setY(y);

                    } else {
                        double x = dragEvent.getX();
                        double y = dragEvent.getY();
                        modele.ajouterClasse(nomClasse, x, y);
                    }
                    success = true;

                }

                dragEvent.setDropCompleted(success);
                dragEvent.consume();
                modele.notifierObservateur();
            }
        }
    }
}
