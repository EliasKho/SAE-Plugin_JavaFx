package projet.arborescence;

import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.Pane;
import projet.Modele;
import projet.Observateur;
import projet.Sujet;
import projet.controleur.ControlerClic;

public class VueArborescence extends Pane implements Observateur {

    private TreeView<FileComposite> arbre;
    private Modele modele;
    private ControlerClic controlerClic;

    public VueArborescence(Modele m, ControlerClic c) {
        this.modele = m;
        this.controlerClic = c;
        actualiser(m);

        // Liaison de la taille du TreeView à celle du conteneur
        this.widthProperty().addListener((obs, oldWidth, newWidth) -> majTaille());
        this.heightProperty().addListener((obs, oldHeight, newHeight) -> majTaille());
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
        FileComposite file = m.getRacine();

        this.arbre= new TreeView<>(getArbre(file));
        this.arbre.setOnMouseClicked(controlerClic);
        this.getChildren().add(arbre);

        majTaille();
    }

    public void majTaille() {
        this.arbre.setPrefWidth(this.getWidth());
        this.arbre.setPrefHeight(this.getHeight());
    }
}
