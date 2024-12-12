package projet.arborescence;

import java.lang.reflect.*;

public class Analyseur {

    public static void analyseClasse(String nomClasse) throws ClassNotFoundException {

        // nom de la classe
        Class<?> classe = Class.forName(nomClasse);

        // constructeurs
        Constructor<?>[] constructeursDeclares = classe.getDeclaredConstructors();

        // attributs
        Field[] fieldsDeclares = classe.getDeclaredFields();

        // méthodes
        Method[] methodsDeclares = classe.getDeclaredMethods();

        // interfaces
        Class<?>[] interfaces = classe.getInterfaces();

        // classe héritée
        Class<?> superClass = classe.getSuperclass();


        System.out.println("Intitulé :");
        System.out.println(getAffichageClasse(classe));
        System.out.println();
        System.out.println("Package: ");
        System.out.println(classe.getPackage().getName());

        System.out.println();
        System.out.println("Constructeurs déclarés: ");
        for (Constructor<?> constructeur : constructeursDeclares) {
            System.out.println(getAffichageConstructeur(constructeur));
        }

        System.out.println();
        System.out.println("Attributs déclarés: ");
        for (Field field : fieldsDeclares) {
            System.out.println(getAffichageAttribut(field));
        }

        System.out.println();
        System.out.println("Méthodes déclarées: ");
        for (Method method : methodsDeclares) {
            System.out.println(getAffichageMethode(method));
        }

        System.out.println();
        System.out.println("Interfaces implémentées: ");
        for (Class<?> interfaceClasse : interfaces) {
            System.out.println(getNom(interfaceClasse));
        }

        System.out.println();
        System.out.println("Classe mère: ");
        System.out.println(getNom(superClass));
    }

    /**
     * Retourne l'affichage de la classe
     * @param classe
     * @return
     */
    public static String getAffichageClasse(Class<?> classe) {
        String affichage = "";
        affichage += getAffichageModifier(classe.getModifiers());
        if (classe.isInterface()) {
            affichage += "interface " + getNom(classe);
        } else {
            affichage += "class " + getNom(classe);
        }
        return affichage;
    }

    /**
     * Retourne les modificateurs
     * @param mod
     * @return
     */
    public static String getAffichageModifier(int mod) {
        String modifier = "";
        if (Modifier.isPublic(mod)) {
            modifier += "public ";
        }
        if (Modifier.isProtected(mod)) {
            modifier += "protected ";
        }
        if (Modifier.isPrivate(mod)) {
            modifier += "private ";
        }
        if (Modifier.isStatic(mod)) {
            modifier += "static ";
        }
        if (Modifier.isFinal(mod)) {
            modifier += "final ";
        }
        if (Modifier.isAbstract(mod)) {
            modifier += "abstract ";
        }
        return modifier;
    }

    /**
     * Retourne le type de retour d'une méthode
     * @param methode
     * @return
     */
    public static String getAffichageTypeRetour(Method methode) {
        String affichage = "";
        affichage += getNom(methode.getReturnType())+" ";
        return affichage;
    }

    /**
     * Retourne les paramètres d'une méthode ou d'un constructeur
     * @param methode
     * @return
     */
    public static String getAffichageParametres(Executable methode) {
        String affichage = "";
        Class<?>[] params = methode.getParameterTypes();
        affichage += "(";
        for (int i = 0; i < params.length; i++) {
            affichage += getNom(params[i]);
            if (i < params.length - 1) {
                affichage += ", ";
            }
        }
        affichage += ")";
        return affichage;
    }

    /**
     * Retourne les exceptions d'une méthode ou d'un constructeur
     * @param methode
     * @return
     */
    public static String getAffichageExceptions(Executable methode) {
        String affichage = "";
        Class<?>[] exceptions = methode.getExceptionTypes();
        if (exceptions.length > 0) {
            affichage += " throws ";
            for (int i = 0; i < exceptions.length; i++) {
                affichage += getNom(exceptions[i]);
                if (i < exceptions.length - 1) {
                    affichage += ", ";
                }
            }
        }
        return affichage;
    }

    /**
     * Retourne l'affichage d'une méthode
     * @param methode
     * @return
     */
    public static String getAffichageMethode(Method methode) {
        String affichage = "";
        affichage += getAffichageModifier(methode.getModifiers());
        affichage += getAffichageTypeRetour(methode);
        affichage += methode.getName();
        affichage += getAffichageParametres(methode);
        affichage += getAffichageExceptions(methode);
        return affichage;
    }

    /**
     * Retourne l'affichage d'un constructeur
     * @param constructeur
     * @return
     */
    public static String getAffichageConstructeur(Executable constructeur) {
        String affichage = "";
        affichage += getAffichageModifier(constructeur.getModifiers());
        affichage += getNom(constructeur.getDeclaringClass());
        affichage += getAffichageParametres(constructeur);
        affichage += getAffichageExceptions(constructeur);
        return affichage;
    }

    /**
     * Retourne l'affichage d'un attribut
     * @param attribut
     * @return
     */
    public static String getAffichageAttribut(Field attribut) {
        String affichage = "";
        affichage += getAffichageModifier(attribut.getModifiers());
        affichage += getNom(attribut.getType());
        affichage += " " + attribut.getName();
        return affichage;
    }

    /**
     * Retourne le nom d'une classe (sans le package)
     * @param classe
     * @return
     */
    public static String getNom(Class<?> classe) {
        if (classe.isArray()) {
            return getNom(classe.getComponentType()) + "[]";
        }
        String nom = classe.getName();
        return nom.substring(nom.lastIndexOf('.') + 1);
    }
}
