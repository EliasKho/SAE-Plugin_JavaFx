package projet.arborescence;

import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import projet.Modele;
import projet.Observateur;
import projet.Sujet;

import java.io.File;

public class VueArborescence extends Pane implements Observateur {

    private TreeView<String> arbre;
    private Modele modele;

    public VueArborescence(Modele m) {
        this.modele = m;
        actualiser(m);
    }

    public TreeItem<String> getArbre(FileComposite file) {
        TreeItem<String> racine = new TreeItem<>(file.getName());

        if (file instanceof Dossier) {
            for (FileComposite f : file.getChildren()) {
                racine.getChildren().add(getArbre(f));
            }
        }
        return racine;
    }

    public void actualiser(Sujet s){
        Modele m = (Modele) s;
        FileComposite file;
        this.getChildren().clear();
        if (new File(m.getPath()).isDirectory()) {
            file = new Dossier(new File(m.getPath()));
        }
        else {
            file = new Fichier(new File(m.getPath()));
        }
        this.arbre= new TreeView<>(getArbre(file));
        this.arbre.setMaxSize(250,600);
        this.arbre.setMinSize(250,600);
        this.getChildren().add(arbre);
    }
}
