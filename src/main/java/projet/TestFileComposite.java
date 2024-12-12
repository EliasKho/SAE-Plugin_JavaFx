package projet;

import java.io.File;

public class TestFileComposite {
    public static void main(String[] args) throws ClassNotFoundException {
        Dossier dossier = new Dossier(new File("src"));
        dossier.display("");

    }
}
