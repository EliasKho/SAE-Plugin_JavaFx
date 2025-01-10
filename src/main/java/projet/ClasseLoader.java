package projet;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;

public class ClasseLoader {

    private static HashMap<String, Class<?>> classes = new HashMap<>();

    // Chargement d'une classe à partir d'un chemin d'accès
    public static String loadClass(String cheminAbsolu, File racine) {
        // on a le chemin absolu du fichier à ajouter
        // on récupère le chemin absolu de la racine du dossier sélectionné (racine dans modele)
        // on teste de récupérer le .class du fichier en remontant dans les dossiers si erreur jusqu'à trouver le bon (package)

        String cheminRacine = racine.getAbsolutePath();

        // on remplace les \ par des .
        cheminAbsolu = cheminAbsolu.replace("\\", ".");

        // on retire le .java
        cheminAbsolu = cheminAbsolu.substring(0, cheminAbsolu.length() - 5);

        // on sépare le chemin absolu du fichier en tableau de String
        String[] cheminAbsoluTab = cheminAbsolu.split("\\.");

        String packageName = cheminAbsoluTab[cheminAbsoluTab.length - 1];

        // traitement pour passer du chemin racine au chemin de l'URL
        // on retire tout ce qu'il y a après le src
        cheminRacine = cheminRacine.substring(0, cheminRacine.indexOf("\\src"));

        // on récupère le nom du projet
        String nomProjet = cheminRacine.substring(cheminRacine.lastIndexOf("\\") + 1);

        // on ajoute le chemin du projet
        cheminRacine = cheminRacine + "/out/production/" + nomProjet + "/";

        for (int i = cheminAbsoluTab.length - 2; i >= 0; i--) {
            packageName = cheminAbsoluTab[i] + "." + packageName;
            try {
                URL url = new File(cheminRacine).toURI().toURL();
                URL[] urls = new URL[]{url};
                ClassLoader classeLoader = new URLClassLoader(urls);

                Class<?> classe = classeLoader.loadClass(packageName);

                classes.put(packageName, classe);
                return packageName;
            } catch (ClassNotFoundException | MalformedURLException e) {
                if (i == 0) {
                    return null;
                }
            }
        }
        return null;
    }

    public static HashMap<String, Class<?>> getClasses() {
        return classes;
    }
}
