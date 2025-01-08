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
        event.acceptTransferModes(TransferMode.MOVE);
    }

}