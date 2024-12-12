package projet.arborescence;

import javafx.scene.control.Label;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Fichier extends FileComposite {
    public Fichier(File file) {
        super(file);
    }

    public void display(String prefix) {
        System.out.println(prefix+"|>" + getName()+ " (" + length() + " bytes)");
    }

    public List<FileComposite> getChildren() {
        return new ArrayList<FileComposite>();
    }
}
