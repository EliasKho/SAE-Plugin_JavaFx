package projet;

import javafx.scene.Scene;
import projet.arborescence.FileComposite;
import projet.classes.*;

import java.io.*;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Modele implements Sujet{

    private final FileComposite racine;
    private final List<Observateur> observateurs;
    private final HashMap<String, Classe> classes;
    private final List<Fleche> relations;
    private String UML = "";
    private Scene scene;
    private VueClasse vueClasse;
    private String vue;
    private static double ratio = 1;

    public Modele(FileComposite f) {
        this.racine = f;
        this.observateurs = new ArrayList<>();
        this.classes = new HashMap<>();
        this.relations = new ArrayList<>();
        this.vue = "classique";
    }

    public FileComposite getRacine() {
        return racine;
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

    public Classe ajouterClasse(String className, double x, double y){
        Classe classe = null;
        if (!classes.containsKey(className)) {
            classe = new Classe(className);
            try {
                String packageName = ClasseLoader.loadClass(className, racine);
                Class<?> classeJava = ClasseLoader.getClasses().get(packageName);
                classe = new Classe(packageName);
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

                classes.put(packageName, classe);

                updateRelations();
                // génération du diagramme UML
                UML = createUML();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        notifierObservateur();
        return classe;
    }

    public void ajouterListClasses(List<String> liste){
        double x=10;
        double y=10;
        double lonMax=0;
        for(String className : liste){
            Classe classe = ajouterClasse(className, x, y);
            if(classe != null){
                if(classe.getLongueur()>lonMax)
                    lonMax = classe.getLongueur();

                if(x+10+classe.getLargeur()>600){
                    x=10;
                    y+=10+lonMax;
                } else {
                    x+=10+classe.getLargeur();
                }
            }
        }
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
        return (!nom.startsWith("java.util") && !nom.startsWith("java.lang"));
    }

    public boolean classeInDiagramme(String nom){
        return classes.containsKey(nom);
    }

    public String getPackage(Class<?> classe) {
        return classe.getPackage().getName();
    }

    public ArrayList<Attribut> getAttributs(Class<?> classe){
        ArrayList<Attribut> attributs = new ArrayList<>();

        for (Field field : classe.getDeclaredFields()) {
            Class<?> type = field.getType();
            if (typePrimitif(type)) {
                Attribut attribut = new Attribut(field.getName(), field.getType().getName(), field.getModifiers());
                attributs.add(attribut);
            }
            else if (!classeExiste(type.getName())) {
                Type genericType = field.getGenericType();
                if (genericType instanceof ParameterizedType) {
                    ParameterizedType paramT = (ParameterizedType) genericType;
                    Type[] typeArguments = paramT.getActualTypeArguments();
                    for (Type arg : typeArguments) {
                        if (arg instanceof Class) {
                            if (!classeExiste(arg.getTypeName())) {
                                String typeName = paramT.getTypeName();
                                String[] split = typeName.split("<");
                                typeName = split[0].substring(split[0].lastIndexOf(".")+1);
                                String[] split2 = split[1].split(",");
                                typeName+= "<";
                                for (int i = 0; i < split2.length; i++) {
                                    typeName += split2[i].substring(split2[i].lastIndexOf(".")+1);
                                    if (i != split2.length-1) {
                                        typeName += ",";
                                    }
                                }
                                Attribut attribut = new Attribut(field.getName(), typeName, field.getModifiers());
                                attributs.add(attribut);
                            }
                        }
                    }
                }
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

    public void updateRelationHeritage(Classe classe) {
        String enfant;
        String parent;
        Class<?> className = ClasseLoader.getClasses().get(classe.getNomPackage() + "." + classe.getNom());
        Class<?> superClass = className.getSuperclass();

        enfant = className.getName();

        if (superClass != null && superClass != Object.class && classeInDiagramme(superClass.getName())) {
            parent = superClass.getName();
            ajouterRelation(parent, enfant, Fleche.EXTENDS, null, null, "\"\"");
        }

        Class<?>[] interfaces = className.getInterfaces();

        for (Class<?> interfaceClass : interfaces) {
            if (!classeInDiagramme(interfaceClass.getName())) {
                continue;
            }
            parent = interfaceClass.getName();
            ajouterRelation(parent, enfant, Fleche.IMPLEMENTS, null, null, "\"\"");
        }
    }

    public void updateRelationAttributs(Classe c) {
        String className = c.getNomPackage() + "." + c.getNom();
        Class<?> classe = ClasseLoader.getClasses().get(className);

        for (Field field : classe.getDeclaredFields()) {
            Class<?> type = field.getType();
            if (!typePrimitif(type)) {
                if (classeInDiagramme(type.getName())) {
                    ajouterRelation(type.getName(), className, Fleche.DEPENDANCE, "1", "1", field.getName());
                } else {
                    Type genericType = field.getGenericType();
                    if (genericType instanceof ParameterizedType) {
                        ParameterizedType paramT = (ParameterizedType) genericType;
                        Type[] typeArguments = paramT.getActualTypeArguments();
                        for (Type arg : typeArguments) {
                            if (arg instanceof Class) {
                                if (classeInDiagramme(arg.getTypeName())) {
                                    ajouterRelation(arg.getTypeName(), className, Fleche.DEPENDANCE, "*", "1", field.getName());
                                }
                            }
                        }
                    }

                }
            }
        }
    }

    public void updateRelations(){
        // on supprime tout et on réajoute les relations pour enlever les relations qui ne sont plus présentes
        // (possiblement enlevé plus tard pour laisser cela à l'action d'actualisation du diagramme)
        relations.clear();
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

    public boolean isInDiagram(String nom){
        return classes.containsKey(nom);
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

    public void supprimerClasse(String nom){
        classes.remove(nom);
        updateRelations();
        UML = createUML();
        notifierObservateur();
    }

    public void viderClasses(){
        classes.clear();
        updateRelations();
        UML = createUML();
        notifierObservateur();
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

    public void setScene(Scene scene){
        this.scene = scene;
    }

    public Scene getScene(){
        return scene;
    }

    public void setVueClasse(VueClasse vueClasse){
        this.vueClasse = vueClasse;
    }

    public VueClasse getVueClasse(){
        return vueClasse;
    }

    public void setVue(String vue){
        this.vue = vue;
    }

    public String getVue(){
        return vue;
    }

    public void saveToFile(String fileP){
        File file = new File(fileP);
        try(FileOutputStream fose = new FileOutputStream(file);
            ObjectOutputStream oss = new ObjectOutputStream(fose)){
            String res = "";
            for (HashMap.Entry<String, Classe> entry : this.classes.entrySet()){
                res += entry.getValue().toString();
            }
            oss.writeObject(res);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void loadFromFile(String file){
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            String res = (String) ois.readObject();
            String[] classeParts = res.split("\n");
            for(String i : classeParts) {
                String[] difParts = i.split(":");
                this.ajouterClasse(difParts[0], Double.parseDouble(difParts[1]), Double.parseDouble(difParts[2]));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static double getRatio() {
        return ratio;
    }
    public static void setRatio(double r) {
        ratio = r;
    }

}