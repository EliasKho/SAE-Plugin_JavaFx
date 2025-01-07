package projet;

import projet.arborescence.FileComposite;
import projet.classes.*;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Modele implements Sujet{

    private final FileComposite file;
    private final List<Observateur> observateurs;
    private final HashMap<String, Classe> classes;
    private final List<Fleche> relations;
    private String UML = "";

    public Modele(FileComposite f) {
        this.file = f;
        this.observateurs = new ArrayList<>();
        this.classes = new HashMap<>();
        this.relations = new ArrayList<>();
    }

    public FileComposite getRacine() {
        return file;
    }

    public void notifierObservateur(){
        for(Observateur observateur : observateurs){
            observateur.actualiser(this);
        }
    }

    public void enregistrerObservateur(Observateur observateur){
        observateurs.add(observateur);
    }

    public void supprimerObservateur(Observateur observateur){
        observateurs.remove(observateur);
    }

    public void ajouterClasse(String className, double x, double y){
        if (!classes.containsKey(className)) {
            Classe classe = new Classe(className);

            try {
                Class<?> classeJava = Class.forName(className);

                classe.setNomPackage(getPackage(classeJava));

                if (classeJava.isInterface()) {
                    classe.setInterface(true);
                } else {
                    if (Modifier.isAbstract(classeJava.getModifiers())) {
                        classe.setAbstract(true);
                    }
                }

                ArrayList<Methode> methodes = getMethode(classeJava);
                ArrayList<Attribut> attributs = getAttributs(classeJava);

                classe.setAttributs(attributs);
                classe.setMethodes(methodes);

                classe.setX(x);
                classe.setY(y);

                classes.put(className, classe);

                updateRelations();
                // génération du diagramme UML
                UML = createUML();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        notifierObservateur();
    }

    /**
     * retourne true si le type est un type primitif ou un type de la classe java.lang
     * false sinon
     */
    public boolean typePrimitif(Class<?> type){
        return (type.isPrimitive() || type.getName().startsWith("java.lang"));
    }

    /**
     * retourne false si la classe est une collection (List, Set, Map, etc)
     * true sinon
     */
    public boolean classeExiste(String nom){
        // si la classe n'est pas dans le diagramme et n'est pas une classe de java.util
        return (!nom.startsWith("java.util") && classes.containsKey(nom));
    }

    public String getPackage(Class<?> classe) {
        return classe.getPackage().getName();
    }

    public ArrayList<Attribut> getAttributs(Class<?> classe){
        ArrayList<Attribut> attributs = new ArrayList<>();

        for (Field field : classe.getDeclaredFields()) {
            Class<?> type = field.getType();
            if (typePrimitif(type)) {
                Attribut attribut = new Attribut(field.getName(), field.getType(), field.getModifiers());
                attributs.add(attribut);
            }
        }
        return attributs;
    }

    private void ajouterRelation(String parent, String enfant, String type, String parentCardinalite, String enfantCardinalite, String nom){
        // récupération de la classe dans la map
        Classe parentClasse = classes.get(parent);
        Classe enfantClasse = classes.get(enfant);
        Fleche fleche = new Fleche(parentClasse, enfantClasse, type);
        fleche.setParentCardinalite(parentCardinalite);
        fleche.setEnfantCardinalite(enfantCardinalite);
        fleche.setNom(nom);
        if (!relations.contains(fleche)){
            relations.add(fleche);
        }
    }

    public ArrayList<Methode> getMethode(Class<?> classe){
        ArrayList<Methode> methodes = new ArrayList<>();
        for (Constructor<?> constructor : classe.getDeclaredConstructors()){
            Methode methode = new Methode(constructor.getName(), List.of(constructor.getParameters()), constructor.getModifiers());
            methodes.add(methode);
        }
        for (Method method : classe.getDeclaredMethods()){
            Methode methode = new Methode(method.getName(), method.getReturnType(), List.of(method.getParameters()), method.getModifiers());
            methodes.add(methode);
        }
        return methodes;
    }

    public void updateRelationHeritage(Classe classe){
        String enfant;
        String parent;
        try {
            Class<?> className = Class.forName(classe.getNomPackage() + "." + classe.getNom());
            Class<?> superClass = className.getSuperclass();

            enfant = className.getName();

            if (superClass != null && superClass != Object.class && classeExiste(superClass.getName())) {
                parent = superClass.getName();
                ajouterRelation(parent, enfant, Fleche.EXTENDS, null, null, "\"\"");
            }

            Class<?>[] interfaces = className.getInterfaces();
            for (Class<?> interfaceClass : interfaces) {
                if (!classeExiste(interfaceClass.getName())) {
                    continue;
                }
                parent = interfaceClass.getName();
                ajouterRelation(parent, enfant, Fleche.IMPLEMENTS, null, null, "\"\"");
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void updateRelationAttributs(Classe c){
        try {
            String className = c.getNomPackage()+"."+c.getNom();
            Class<?> classe = Class.forName(className);

            for (Field field : classe.getDeclaredFields()) {
                Class<?> type = field.getType();
                if (!typePrimitif(type)) {
                    if (classeExiste(type.getName())) {
                        ajouterRelation(type.getName(), className, Fleche.DEPENDANCE, "1", "1", field.getName());
                    } else {
                        Type genericType = field.getGenericType();
                        if (genericType instanceof ParameterizedType) {
                            ParameterizedType paramT = (ParameterizedType) genericType;
                            Type[] typeArguments = paramT.getActualTypeArguments();
                            for (Type arg : typeArguments) {
                                if (arg instanceof Class) {
                                    if (classeExiste(arg.getTypeName())) {
                                        ajouterRelation(arg.getTypeName(), className, Fleche.DEPENDANCE, "*", "1", field.getName());
                                    }
                                }
                            }
                        }

                    }
                }
            }
        }catch (ClassNotFoundException e){
            e.printStackTrace();
        }
    }

    public void updateRelations(){
        // on supprime tout et on réajoute les relations pour enlever les relations qui ne sont plus présentes
        // (possiblement enlevé plus tard pour laisser cela à l'action d'actualisation du diagramme)
        relations.clear();
        Fleche.reinitialiserNbRelations();
        Fleche.reinitialiserNbRelations();
        for (Classe classe : classes.values()) {
            try {
                updateRelationHeritage(classe);
                updateRelationAttributs(classe);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        notifierObservateur();
    }



    public String createUML(){
        StringBuilder res = new StringBuilder("@startuml\n");
        for (Classe c : classes.values()){
            if (c.isInterface()){
                res.append("interface ").append(c.getNom()).append(" {\n");
            } else if (c.isAbstract()) {
                res.append("abstract class ").append(c.getNom()).append(" {\n");
            }
            else{
                res.append("class ").append(c.getNom()).append(" {\n");
            }

            for (Attribut a : c.getAttributs()){
                res.append(a.getUMLString()).append("\n");
            }

            for (Methode m : c.getMethodes()){
                res.append(m.getUMLString()).append("\n");
            }
            res.append("}\n\n");
        }
        for (Fleche r : relations){
            res.append(r.getUMLString()).append("\n");
        }
        res.append("@enduml");
        return res.toString();
    }

    public String getUML(){
        return UML;
    }

    public HashMap<String, Classe> getClasses(){
        return classes;
    }

    public List<Fleche> getRelations(){
        return relations;
    }
}