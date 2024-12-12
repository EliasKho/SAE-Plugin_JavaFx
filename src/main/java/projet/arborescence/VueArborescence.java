package projet.arborescence;

import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import projet.Modele;
import projet.Observateur;
import projet.Sujet;
import projet.controleur.ControlerClic;

import java.io.File;

public class VueArborescence extends Pane implements Observateur {

    private TreeView<FileComposite> arbre;
    private Modele modele;
    private ControlerClic controlerClic;

    public VueArborescence(Modele m, ControlerClic c) {
        this.modele = m;
        this.controlerClic = c;
        actualiser(m);
    }

    public TreeItem<FileComposite> getArbre(FileComposite file) {
        TreeItem<FileComposite> racine = new TreeItem<>(file);
        if (file instanceof Dossier) {
            for (FileComposite f : file.getChildren()) {
                racine.getChildren().add(getArbre(f));
            }
        }
        return racine;
    }

    public void actualiser(Sujet s){
        Modele m = (Modele) s;
        FileComposite file = m.getPath();

        this.arbre= new TreeView<>(getArbre(file));
        this.arbre.setMaxSize(250,600);
        this.arbre.setMinSize(250,600);
        this.arbre.setOnMouseClicked(controlerClic);
        this.getChildren().add(arbre);
    }
}
