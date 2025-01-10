package projet;

import javafx.scene.Scene;
import projet.arborescence.Dossier;
import projet.arborescence.FileComposite;
import projet.classes.*;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Modele implements Sujet, Serializable{

    private FileComposite racine;
    private final List<Observateur> observateurs;
    private final HashMap<String, Classe> classes;
    private final List<Fleche> relations;
    private String UML = "";
    private Scene scene;
    private VueClasse vueClasse;
    private String vue;

    public Modele() {
        this.racine = null;
        this.observateurs = new ArrayList<>();
        this.classes = new HashMap<>();
        this.relations = new ArrayList<>();
        this.vue = "classique";
    }

    public FileComposite getRacine() {
        return racine;
    }

    public void setRacine(FileComposite racine) {
        this.racine = racine;
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
            try {
                String packageName = ClasseLoader.loadClass(className, racine);
                Class<?> classeJava = ClasseLoader.getClasses().get(packageName);

                if(classeJava == null){
                    classeJava = Class.forName(className);
                    packageName = classeJava.getName();
                }

                    classe = new Classe(packageName);
                    classe.setNomPackage(getPackage(classeJava));
                    classe.setAbsolutePath(className);

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

            } catch (StringIndexOutOfBoundsException e) {

            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        notifierObservateur();
        return classe;
    }

    public void ajouterClasseInexistante(String className, String type, double x, double y, List<Attribut> attributs, List<Methode> methodes){
        if (!classes.containsKey(className)) {
            Classe classe = new Classe(className);
            classe.setX(x);
            classe.setY(y);

            switch(type){
                case "interface":
                    classe.setInterface(true);
                    break;
                case "abstract":
                    classe.setAbstract(true);
                    break;
                default:
                    break;
            }
            classe.setAttributs(attributs);
            classe.setMethodes(methodes);

            classes.put(className, classe);
            updateRelations();
            UML = createUML();
            notifierObservateur();
        }
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

    public void ajouterRelation(String parent, String enfant, String type, String parentCardinalite, String enfantCardinalite, String nom){
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
        UML = createUML();
        notifierObservateur();
    }

    public ArrayList<Methode> getMethode(Class<?> classe){
        ArrayList<Methode> methodes = new ArrayList<>();
        for (Constructor<?> constructor : classe.getDeclaredConstructors()){
            List<Class<?>> parameters = List.of(constructor.getParameterTypes());
            List<Parametre> parametres = new ArrayList<>();
            for (Class<?> param : parameters){
                Parametre p = new Parametre(param.getName());
                parametres.add(p);
            }
            Methode methode = new Methode(constructor.getName(), parametres, constructor.getModifiers());
            methodes.add(methode);
        }
        for (Method method : classe.getDeclaredMethods()){
            List<Class<?>> parameters = List.of(method.getParameterTypes());
            List<Parametre> parametres = new ArrayList<>();
            for (Class<?> param : parameters){
                Parametre p = new Parametre(param.getName());
                parametres.add(p);
            }

            if (!method.isSynthetic()) {
                Methode methode;
                methode = new Methode(method.getName(), method.getReturnType().getTypeName(), parametres, method.getModifiers());
                if (classe.isInterface()) {
                    if (Modifier.isPublic(method.getModifiers())) {
                        methode = new Methode(method.getName(), method.getReturnType().getTypeName(), parametres, 1);
                    } else if (Modifier.isProtected(method.getModifiers())) {
                        methode = new Methode(method.getName(), method.getReturnType().getTypeName(), parametres, 2);
                    } else {
                        methode = new Methode(method.getName(), method.getReturnType().getTypeName(), parametres, 4);
                    }
                }

                methodes.add(methode);
            }
        }
        return methodes;
    }

    public void updateRelationHeritage(Classe classe) throws ClassNotFoundException {
        String enfant;
        String parent;
        Class<?> className = ClasseLoader.getClasses().get(classe.getRealName());

        if (className != null) {
            Class<?> superClass = className.getSuperclass();

            enfant = className.getName();

            if (superClass != null && superClass != Object.class) {
                parent = superClass.getName();
                if (!classeInDiagramme(parent)) {
                    if(!parent.substring(0, parent.indexOf(".")).equals(enfant.substring(0, enfant.indexOf(".")))) {
                        classe.ajouterClasseExterne(parent);
                    }
                }
                else {
                    ajouterRelation(parent, enfant, Fleche.EXTENDS, null, null, "\"\"");
                }
            }

            Class<?>[] interfaces = className.getInterfaces();

            for (Class<?> interfaceClass : interfaces) {
                if (!classeInDiagramme(interfaceClass.getName())) {
                    if (!interfaceClass.getName().substring(0, interfaceClass.getName().indexOf(".")).equals(enfant.substring(0, enfant.indexOf(".")))) {
                        classe.ajouterClasseExterne(interfaceClass.getName());
                    }
                }
                else {
                    parent = interfaceClass.getName();
                    ajouterRelation(parent, enfant, Fleche.IMPLEMENTS, null, null, "\"\"");
                }
            }
        }
    }

    public void updateRelationAttributs(Classe c) {
        String className = c.getRealName();
        Class<?> classe = ClasseLoader.getClasses().get(className);
        if (classe!= null) {

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
    }

    public void updateRelations(){
        Iterator<Classe> it = classes.values().iterator();
        while (it.hasNext()){
            Classe classe = it.next();
            try {
                updateRelationHeritage(classe);
                updateRelationAttributs(classe);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        checkRelations();
        notifierObservateur();
    }

    public boolean isInDiagram(String nom){
        return classes.containsKey(nom);
    }

    public void checkRelations(){
        Iterator<Fleche> it = relations.iterator();
        while (it.hasNext()){
            Fleche r = it.next();
            String nomParent = r.getParent().getRealName();
            String nomEnfant = r.getEnfant().getRealName();

            if (!isInDiagram(nomEnfant) || !isInDiagram(nomParent)){
                it.remove();
                // on retire 1 au nombre de relations entre les 2 classes
                String keyEnfantParent = nomParent+nomEnfant;
                Fleche.retirer1Relation(keyEnfantParent);
            }
        }
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
        Fleche.reinitialiserNbRelations();
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

    public void saveToFile(String fileP) {
        if (!fileP.endsWith(".sav")) {
            fileP += ".sav";
        }
        File file = new File(fileP);
        try(FileOutputStream fose = new FileOutputStream(file);
            // sauvegarde des classes et des relations
            ObjectOutputStream oss = new ObjectOutputStream(fose)){
            oss.writeObject(this.getClasses());
            oss.writeObject(this.getRelations());
            oss.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void loadFromFile(String file){
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            HashMap<String , Classe> res = (HashMap<String, Classe>) ois.readObject();
            List<Fleche> relations = (List<Fleche>) ois.readObject();

            Object[] classesKeyObject = res.keySet().toArray();
            String[] classesKey = new String[classesKeyObject.length];
            //Une boucle pour transformer les Objets sortis du fichier soit en chaîne de caractère
            for(int j = 0;j<classesKeyObject.length;j++){
                classesKey[j] = classesKeyObject[j].toString();
            }

            this.viderClasses();
            Fleche.reinitialiserNbRelations();
            Classe c;
            for (int i=0; i<classesKey.length;i++){
                c = res.get(classesKey[i]);
                if (c.getAbsolutePath() == null){
                    String type = "class";
                    if (c.isInterface()){
                        type = "interface";
                    } else if (c.isAbstract()){
                        type = "abstract";
                    }
                    this.ajouterClasseInexistante(c.getNom(), type, c.getX(), c.getY(), c.getAttributs(), c.getMethodes());
                }
                else {
                    //On relie les classe qui était présente dans le fichier de sauvegarde au chemin
                    this.ajouterClasse(c.getAbsolutePath(), c.getX(), c.getY());
                }
            }
            // on ajoute les relations
            for (Fleche r : relations){
                this.ajouterRelation(r.getParent().getRealName(), r.getEnfant().getRealName(), r.getType(), r.getParentCardinalite(), r.getEnfantCardinalite(), r.getNom());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean genererCodeSource() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            for (Classe c : classes.values()) {
                try {
                    String nom = c.getNom();
                    String nomPackage = c.getNomPackage();
                    File fichier = new File(nom + ".java");
                    FileWriter fw = new FileWriter(fichier);
                    BufferedWriter bw = new BufferedWriter(fw);
                    bw.write("package " + nomPackage + ";\n\n");
                    if (c.isInterface()) {
                        bw.write("public interface " + nom + " {\n");
                    } else if (c.isAbstract()) {
                        bw.write("public abstract class " + nom + " {\n");
                    } else {
                        bw.write("public class " + nom + " {\n");
                    }
                    for (Attribut a : c.getAttributs()) {
                        bw.write("\t"+a.getStringCode() + "\n");
                    }
                    for (Methode m : c.getMethodes()) {
                        bw.write("\t"+m.getStringCode() + "{}\n");
                    }
                    bw.write("}\n");
                    bw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        executor.shutdownNow();
        return true;
    }

    public void ajouterMethode(String nomClasse, String nomMethode, String typeRetour, String typeParam, int modifier){
        Classe classe = classes.get(nomClasse);
        if (classe != null) {
            List<Parametre> param = new ArrayList<>();
            for (String s : typeParam.split(",")) {
                param.add(new Parametre(s));
            }
            Methode methode = new Methode(nomMethode, typeRetour, param, modifier);
            classe.ajouterMethode(methode);
            updateRelations();
            UML = createUML();
            notifierObservateur();
        }
    }
    public void ajouterConstructeur(String nomClasse, String typeParam, int modifier){
        Classe classe = classes.get(nomClasse);
        if (classe != null) {
            List<Parametre> param = new ArrayList<>();
            for (String s : typeParam.split(",")) {
                param.add(new Parametre(s));
            }
            String nomMethode = nomClasse.substring(nomClasse.lastIndexOf(".")+1);
            Methode methode = new Methode(nomMethode, param, modifier);
            classe.ajouterMethode(methode);
            updateRelations();
            UML = createUML();
            notifierObservateur();
        }
    }

    public void ajouterAttribut(String nomClasse, String nomAttribut, String typeAttribut, int modifier){
        Classe classe = classes.get(nomClasse);
        if (classe != null) {
            Attribut attribut = new Attribut(nomAttribut, typeAttribut, modifier);
            classe.getAttributs().add(attribut);
            updateRelations();
            UML = createUML();
            notifierObservateur();
        }
    }
}