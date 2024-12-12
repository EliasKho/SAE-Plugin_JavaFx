package projet.controleur;

import javafx.event.EventHandler;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import projet.Modele;
import projet.arborescence.Dossier;
import projet.arborescence.Fichier;
import projet.arborescence.FileComposite;

public class ControlerClic implements EventHandler<MouseEvent> {
    private Modele modele;

    public ControlerClic(Modele modele) {
        this.modele = modele;
    }

    public void handle(MouseEvent event) {
        TreeView<FileComposite> item = (TreeView<FileComposite>) event.getSource();

        TreeItem<FileComposite> selectedItem = item.getSelectionModel().getSelectedItem();

        if (selectedItem != null) {
            FileComposite file = selectedItem.getValue();
            if (file instanceof Fichier) {
                System.out.println("clic sur le fichier " + file.getName());
            }
        }
    }
}
