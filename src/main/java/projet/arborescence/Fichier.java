package projet.arborescence;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Fichier extends FileComposite {
    public Fichier(File file) {
        super(file);
    }

    public List<FileComposite> getChildren() {
        return new ArrayList<>();
    }
}
