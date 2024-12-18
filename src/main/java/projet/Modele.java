package projet;

import projet.arborescence.FileComposite;
import projet.classes.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;

public class Modele implements Sujet{

    private final FileComposite file;
    private List<Observateur> observateurs;
    private ArrayList<Classe> classes;
    private ArrayList<Relation> relations;
    private String UML = "";

    public Modele(FileComposite f) {
        this.file = f;
        this.observateurs = new ArrayList<>();
        this.classes = new ArrayList<>();
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

    public void saveIntrospection(String className){
        Classe classe = new Classe(className);

        try{
            Class<?> classeJava = Class.forName(className);

            classe.setNomPackage(getPackage(classeJava));

            if (classeJava.isInterface()){
                classe.setInterface(true);
            }
            else {
                if (Modifier.isAbstract(classeJava.getModifiers())){
                    classe.setAbstract(true);
                }
            }

            ArrayList<Methode> methodes = getMethode(classeJava);
            ArrayList<Attribut> attributs = getAttributs(classeJava);

            classe.setAttributs(attributs);
            classe.setMethodes(methodes);

            if (!classes.contains(classe)){
                classes.add(classe);
            }

            ajouterHeritage(classeJava);

            // génération du diagramme UML
            UML = createUML();
        } catch (ClassNotFoundException e){
            e.printStackTrace();
        }
        notifierObservateur();
    }

    /**
     * retourne true si le type est un type primitif ou un type de la classe java.lang
     * false sinon
     * @param type
     * @return
     */
    public boolean typePrimitif(Class<?> type){
        if (type.isPrimitive()){
            return true;
        }
        else if (type.getName().startsWith("java.lang")){
            return true;
        }
        else
            System.out.println(type);
        return false;
    }

    /**
     * retourne false si la classe est une collection (List, Set, Map, etc)
     * true sinon
     * @param nom
     * @return
     */
    public boolean classeExiste(String nom){
        if (nom.startsWith("java.util")){
            System.out.println(nom);
            return false;
        }
        return true;
    }

    public String getPackage(Class<?> classe) {
        return classe.getPackage().getName();
    }

    public ArrayList<Attribut> getAttributs(Class<?> classe){
        ArrayList<Attribut> attributs = new ArrayList<>();
        String className = classe.getName();

        for (Field field : classe.getDeclaredFields()) {
            Class<?> type = field.getType();
            if (typePrimitif(type)) {
                Attribut attribut = new Attribut(field.getName(), field.getType(), field.getModifiers());
                attributs.add(attribut);
            } else {
                if (classeExiste(type.getName())) {
                    ajouterRelation(type.getName(), className, Relation.DEPENDANCE, "1", "1", field.getName());
                } else {
                    Type genericType = field.getGenericType();
                    if (genericType instanceof ParameterizedType) {
                        ParameterizedType paramT = (ParameterizedType) genericType;
                        Type[] typeArguments = paramT.getActualTypeArguments();
                        for (Type arg : typeArguments) {
                            if (arg instanceof Class) {
                                if (classeExiste(arg.getTypeName())) {
                                    ajouterRelation(arg.getTypeName(), className, Relation.DEPENDANCE, "*", "1", field.getName());
                                }
                            }
                        }
                    }

                }
            }
        }
        return attributs;

    }

    private void ajouterRelation(String parent, String enfant, String type, String parentCardinalite, String enfantCardinalite, String nom){
        Classe parentClasse = new Classe(parent);
        Classe enfantClasse = new Classe(enfant);
        Relation relation = new Relation(parentClasse, enfantClasse, type);
        relation.setParentCardinalite(parentCardinalite);
        relation.setEnfantCardinalite(enfantCardinalite);
        relation.setNom(nom);
        if (!relations.contains(relation)){
            relations.add(relation);
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

    public void ajouterHeritage(Class<?> className) {
        Class<?> superClass = className.getSuperclass();
        if (superClass != null && superClass != Object.class) {
            Classe parent = new Classe(superClass.getName());
            Classe enfant = new Classe(className.getName());
            Relation relation = new Relation(parent, enfant, Relation.EXTENDS);
            if (!relations.contains(relation)) {
                relations.add(relation);
            }
        }

        Class<?>[] interfaces = className.getInterfaces();
        for (Class<?> interfaceClass : interfaces) {
            Classe enfant = new Classe(className.getName());
            Classe parent = new Classe(interfaceClass.getName());
            Relation relation = new Relation(parent, enfant, Relation.IMPLEMENTS);
            if (!relations.contains(relation)) {
                relations.add(relation);
            }
        }
    }

    public String createUML(){
        StringBuilder res = new StringBuilder("@startuml\n");
        for (Classe c : classes){
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
        for (Relation r : relations){
            res.append(r.getUMLString()).append("\n");
        }
        res.append("@enduml");
        return res.toString();
    }

    public String getUML(){
        return UML;
    }
}