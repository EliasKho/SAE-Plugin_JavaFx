package projet.arborescence;

import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import projet.Modele;
import projet.Observateur;

import java.io.File;

public class VueArborescence extends VBox implements Observateur {

    private TreeView<String> arbre;
    private Modele modele;

    public VueArborescence(Modele m) {
        this.modele = m;
        actualiser();
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

    public void actualiser(){
        FileComposite file;
        this.getChildren().clear();
        if (new File(modele.getPath()).isDirectory()) {
            file = new Dossier(new File(modele.getPath()));
        }
        else {
            file = new Fichier(new File(modele.getPath()));
        }
        this.arbre= new TreeView<>(getArbre(file));
        this.arbre.setMaxSize(250,600);
        this.arbre.setMinSize(250,600);
        this.getChildren().add(arbre);
    }
}
