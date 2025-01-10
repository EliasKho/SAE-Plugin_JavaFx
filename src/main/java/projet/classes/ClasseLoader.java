package projet.classes;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;

/**
 * Classe permettant de charger une classe à partir d'un chemin d'accès
 */
public class ClasseLoader {

    // HashMap contenant les classes chargées (pour ne pas les recharger)
    private static HashMap<String, Class<?>> classes = new HashMap<>();

    // Chargement d'une classe à partir d'un chemin d'accès
    public static String loadClass(String cheminAbsolu, File racine) {
        // on a le chemin absolu du fichier à ajouter
        // on récupère le chemin absolu de la racine du dossier sélectionné (racine dans modele)
        // on teste de récupérer le .class du fichier en remontant dans les dossiers si erreur jusqu'à trouver le bon (package)

        // on récupère le chemin absolu de la racine
        String cheminRacine = racine.getAbsolutePath();

        // on remplace les \ par des . pour simuler un package
        cheminAbsolu = cheminAbsolu.replace("\\", ".");

        // on retire le .java
        cheminAbsolu = cheminAbsolu.substring(0, cheminAbsolu.length() - 5);

        // on sépare le chemin absolu du fichier en tableau de String
        String[] cheminAbsoluTab = cheminAbsolu.split("\\.");

        String packageName = cheminAbsoluTab[cheminAbsoluTab.length - 1];

        // on retire tout ce qu'il y a après le src dans le chemin de la racine
        cheminRacine = cheminRacine.substring(0, cheminRacine.indexOf("\\src"));

        // on récupère le nom du projet
        String nomProjet = cheminRacine.substring(cheminRacine.lastIndexOf("\\") + 1);

        // on ajoute le chemin du projet pour avoir le chemin de l'URL
        cheminRacine = cheminRacine + "/out/production/" + nomProjet + "/";

        // on remonte dans les dossiers pour trouver le bon package
        for (int i = cheminAbsoluTab.length - 2; i >= 0; i--) {
            // on remonte dans les chemins
            packageName = cheminAbsoluTab[i] + "." + packageName;
            try {
                // on charge la classe
                URL url = new File(cheminRacine).toURI().toURL();
                URL[] urls = new URL[]{url};
                ClassLoader classeLoader = new URLClassLoader(urls);
                // on essaye de charger la classe
                Class<?> classe = classeLoader.loadClass(packageName);

                // si on a réussi à charger la classe on l'ajoute à la HashMap
                classes.put(packageName, classe);
                return packageName;
            } catch (ClassNotFoundException | MalformedURLException e) {
                // si on a une erreur on remonte dans les dossiers
                if (i == 0) {
                    return null;
                }
            }
        }
        return null;
    }

    // Getter de la HashMap contenant les classes chargées
    public static HashMap<String, Class<?>> getClasses() {
        return classes;
    }
}
