package projet.controleur;

import javafx.event.EventHandler;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import projet.Modele;
import projet.arborescence.Fichier;
import projet.arborescence.FileComposite;

import java.io.File;
public class ControlerDragAndDrop implements EventHandler<DragEvent>{

    private Modele modele;
    private String nomClasse;

    public ControlerDragAndDrop(Modele m){
        modele = m;
    }

    @Override
    public void handle(DragEvent event) {
//        if (event.getGestureSource() instanceof File && event.getDragboard().hasString()) {
//            this.nomClasse = event.getSource().toString();
//            event.acceptTransferModes(TransferMode.MOVE);
//            System.out.println("DRAGOVER");
//        }
//        else {
//            Dragboard db = event.getDragboard();
//            boolean success = false;
//            if (db.hasString()) {
//                if (event.getSource() instanceof Pane) {
//                    if (nomClasse != null) {
//                        double x = event.getX();
//                        double y = event.getY();
//
//                        modele.ajouterClasse(nomClasse, x, y);
//                    }
//                    success = true;
//                }
//            }
//            event.setDropCompleted(success);
//            System.out.println("DRAGDROP");
//        }
//        event.consume();
        if (event.getGestureSource() != modele && event.getDragboard().hasString()){
            event.acceptTransferModes(TransferMode.MOVE);
            System.out.println("setOnDragOver");
        }
        else{
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasString()) {
                double x = event.getX();
                double y = event.getY();
                modele.ajouterClasse(nomClasse, x, y);
                success = true;
                System.out.println("setOnDragDropped");
            }
            event.setDropCompleted(success);
            event.consume();
        }
    }

}