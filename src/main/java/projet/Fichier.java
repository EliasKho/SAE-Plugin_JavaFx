package projet;

import java.io.File;

public class Fichier extends FileComposite {
    public Fichier(File file) {
        super(file);
    }

    public void display(String prefix) {
        System.out.println(prefix+"|>" + getName()+ " (" + length() + " bytes)");
    }
}
