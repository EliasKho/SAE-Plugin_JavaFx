package projet.arborescence;

import projet.Modele;

import java.io.File;

public class TestFileComposite {
    public static void main(String[] args) throws ClassNotFoundException {
        Modele modele = new Modele("");
        String s = modele.analyseClasse("projet.arborescence.Fichier");
        //Dossier dossier = new Dossier(new File("src"));
        //dossier.display("");
        System.out.println(s);

    }
}
