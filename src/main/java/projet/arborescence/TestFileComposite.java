package projet.arborescence;

import projet.Modele;

import java.io.File;

public class TestFileComposite {
    public static void main(String[] args) throws ClassNotFoundException {
        Fichier fichier = new Fichier(new File("src/main/java/projet/arborescence/Fichier.java"));
        Modele modele = new Modele(fichier);
        String s = modele.analyseClasse("projet.arborescence.Fichier");
        //Dossier dossier = new Dossier(new File("src"));
        //dossier.display("");
        System.out.println(s);

    }
}
