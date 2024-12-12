package projet.arborescence;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Dossier extends FileComposite {
    protected ArrayList<FileComposite> ss_dossier = new ArrayList<FileComposite>();
    public Dossier(File file) {
        super(file);
        File[] list = this.listFiles();
        for (File f : list) {
            if (f.isDirectory()) {
                Dossier dossier = new Dossier(f);
                ss_dossier.add(dossier);
            } else {
                Fichier fichier = new Fichier(f);
                ss_dossier.add(fichier);
            }
        }
    }

    public void display(String prefix) {
        System.out.print(prefix + "|-");
        System.out.println(getName());

        for (FileComposite file : ss_dossier) {
            file.display(prefix+"| ");
        }
    }

    public List<FileComposite> getChildren() {
        return ss_dossier;
    }

}
