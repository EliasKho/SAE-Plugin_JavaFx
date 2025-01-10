package projet;

import javafx.scene.Scene;
import projet.arborescence.FileComposite;
import projet.classes.*;
import projet.vues.Observateur;
import projet.vues.VueClasse;

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
    private static boolean voirGetSet;
    private static boolean voirFleches;
    private static boolean voirAttributsHerites;

    public Modele() {
        this.racine = null;
        this.observateurs = new ArrayList<>();
        this.classes = new HashMap<>();
        this.relations = new ArrayList<>();
        this.vue = "classique";
        voirGetSet = true;
        voirFleches = true;
        voirAttributsHerites = false;
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

    /**
     * Ajoute une classe au diagramme
     * @param className
     * @param x
     * @param y
     * @return
     */
    public Classe ajouterClasse(String className, double x, double y){
        // si la classe n'est pas déjà dans le diagramme
        Classe classe = null;
        if (!classes.containsKey(className)) {
            try {
                // on charge la classe
                String packageName = ClasseLoader.loadClass(className, racine);
                Class<?> classeJava = ClasseLoader.getClasses().get(packageName);

                // si la classe n'est pas dans le projet chargé (classe externe)
                if(classeJava == null){
                    // on charge la classe depuis la JVM
                    classeJava = Class.forName(className);
                    packageName = classeJava.getName();
                }
                if (packageName != null) {
                    // on créer la nouvelle classe
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

                    // on récupère les attributs et les méthodes de la classe
                    ArrayList<Methode> methodes = getMethode(classeJava);
                    ArrayList<ArrayList<Attribut>> listAttributs = getAttributs(classeJava);

                    // on ajoute les attributs et les méthodes à la classe
                    classe.setHeritesAttributs(listAttributs.get(0));
                    classe.setNonHeritesAttributs(listAttributs.get(1));
                    classe.setMethodes(methodes);

                    classe.setX(x);
                    classe.setY(y);

                    // on ajoute la classe au diagramme
                    classes.put(packageName, classe);

                    updateRelations();
                    // génération du diagramme UML
                    UML = createUML();
                }
            } catch (StringIndexOutOfBoundsException e) {

            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        notifierObservateur();
        return classe;
    }

    /**
     * Ajoute une classe qui n'est pas dans le projet au diagramme
     * @param className
     * @param type
     * @param x
     * @param y
     * @param attributs
     * @param methodes
     */
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
            classe.setNonHeritesAttributs(attributs);
            classe.setHeritesAttributs(new ArrayList<>());
            classe.setMethodes(methodes);

            classes.put(className, classe);
            updateRelations();
            UML = createUML();
            notifierObservateur();
        }
    }

    /**
     * Ajoute une liste de classes au diagramme
     * @param liste
     */
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

    // retourne true si la classe est dans le diagramme
    public boolean classeInDiagramme(String nom){
        return classes.containsKey(nom);
    }

    public String getPackage(Class<?> classe) {
        return classe.getPackage().getName();
    }

    /**
     * retourne une liste d'attributs de la classe, les attributs hérités et les attributs non hérités
     * @param classe
     * @return
     */
    public ArrayList<ArrayList<Attribut>> getAttributs(Class<?> classe){
        ArrayList<Attribut> attributsHerites = new ArrayList<>();
        ArrayList<Attribut> attributsNonHerites = new ArrayList<>();

        for (Field field : classe.getDeclaredFields()) {
            Class<?> type = field.getType();
            if (typePrimitif(type)) {
                Attribut attribut = new Attribut(field.getName(), field.getType().getName(), field.getModifiers());
                attributsNonHerites.add(attribut);
            }
            else if (!classeExiste(type.getName())) {
                Type genericType = field.getGenericType();
                if (genericType instanceof ParameterizedType) {
                    ParameterizedType paramT = (ParameterizedType) genericType;
                    Type[] typeArguments = paramT.getActualTypeArguments();
                    for (Type arg : typeArguments) {
                        if (arg instanceof Class) {
                            if (!classeExiste(arg.getTypeName())) {
                                Attribut attribut = getAttribut(field, paramT);
                                attributsNonHerites.add(attribut);
                            }
                        }
                    }
                }
            }
        }
        for (Field field : classe.getFields()) {
            Class<?> type = field.getType();
            if (typePrimitif(type)) {
                Attribut attribut = new Attribut(field.getName(), field.getType().getName(), field.getModifiers());
                attributsHerites.add(attribut);
            }
            else if (!classeExiste(type.getName())) {
                Type genericType = field.getGenericType();
                if (genericType instanceof ParameterizedType) {
                    ParameterizedType paramT = (ParameterizedType) genericType;
                    Type[] typeArguments = paramT.getActualTypeArguments();
                    for (Type arg : typeArguments) {
                        if (arg instanceof Class) {
                            if (!classeExiste(arg.getTypeName())) {
                                Attribut attribut = getAttribut(field, paramT);
                                attributsHerites.add(attribut);
                            }
                        }
                    }
                }
            }
        }
        ArrayList<ArrayList<Attribut>> listAttributs = new ArrayList<>();
        listAttributs.add(attributsHerites);
        listAttributs.add(attributsNonHerites);
        return listAttributs;
    }

    /**
     * retourne un attribut de la classe (attribut avec type paramétré)
     * @param field
     * @param paramT
     * @return
     */
    private static Attribut getAttribut(Field field, ParameterizedType paramT) {
        // on récupère le nom du type
        StringBuilder typeName = new StringBuilder(paramT.getTypeName());
        // on enlève les types génériques
        String[] split = typeName.toString().split("<");
        typeName = new StringBuilder(split[0].substring(split[0].lastIndexOf(".") + 1));
        String[] split2 = split[1].split(",");
        typeName.append("<");
        for (int i = 0; i < split2.length; i++) {
            typeName.append(split2[i].substring(split2[i].lastIndexOf(".") + 1));
            if (i != split2.length-1) {
                typeName.append(",");
            }
        }
        Attribut attribut = new Attribut(field.getName(), typeName.toString(), field.getModifiers());
        return attribut;
    }

    /**
     * Ajoute une relation entre 2 classes
     * @param parent
     * @param enfant
     * @param type
     * @param parentCardinalite
     * @param enfantCardinalite
     * @param nom
     */
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

    /**
     * retourne une liste de méthodes de la classe
     * @param classe
     * @return
     */
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
                Methode methode = getMethode(classe, method, parametres);

                methodes.add(methode);
            }
        }
        return methodes;
    }

    /**
     * creer un objet methode à partir d'une méthode et de la liste de paramètres
     * @param method
     * @return
     */
    private static Methode getMethode(Class<?> classe, Method method, List<Parametre> parametres) {
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
        return methode;
    }

    /**
     * met à jour les relations d'héritage entre les classes
     * @param classe
     */
    public void updateRelationHeritage(Classe classe) {
        String enfant;
        String parent;
        // on récupère la classe
        Class<?> className = ClasseLoader.getClasses().get(classe.getRealName());

        if (className != null) {
            // on récupère la classe parent
            Class<?> superClass = className.getSuperclass();

            enfant = className.getName();

            // si la classe parent n'est pas dans le diagramme
            if (superClass != null && superClass != Object.class) {
                parent = superClass.getName();
                if (!classeInDiagramme(parent)) {
                    // si la classe parent n'est pas dans le même package que la classe enfant
                    if(!parent.substring(0, parent.indexOf(".")).equals(enfant.substring(0, enfant.indexOf(".")))) {
                        // on ajoute la classe parent au diagramme
                        classe.ajouterClasseExterne(parent);
                    }
                }
                else {
                    // on ajoute la relation d'héritage
                    ajouterRelation(parent, enfant, Fleche.EXTENDS, null, null, "\"\"");
                }
            }

            // on récupère les interfaces implémentées par la classe
            Class<?>[] interfaces = className.getInterfaces();

            for (Class<?> interfaceClass : interfaces) {
                // si l'interface n'est pas dans le diagramme
                if (!classeInDiagramme(interfaceClass.getName())) {
                    // si l'interface n'est pas dans le même package que la classe enfant
                    if (!interfaceClass.getName().substring(0, interfaceClass.getName().indexOf(".")).equals(enfant.substring(0, enfant.indexOf(".")))) {
                        // on ajoute l'interface aux classes externes
                        classe.ajouterClasseExterne(interfaceClass.getName());
                    }
                }
                else {
                    // on ajoute la relation d'implémentation
                    parent = interfaceClass.getName();
                    ajouterRelation(parent, enfant, Fleche.IMPLEMENTS, null, null, "\"\"");
                }
            }
        }
    }

    /**
     * met à jour les relations d'attributs entre les classes
     * @param c
     */
    public void updateRelationAttributs(Classe c) {
        String className = c.getRealName();
        // on récupère la classe
        Class<?> classe = ClasseLoader.getClasses().get(className);
        if (classe!= null) {
            // on récupère les attributs de la classe
            for (Field field : classe.getDeclaredFields()) {
                // si l'attribut n'est pas un type primitif
                Class<?> type = field.getType();
                if (!typePrimitif(type)) {
                    // si la classe de l'attribut est dans le diagramme
                    if (classeInDiagramme(type.getName())) {
                        // on ajoute la relation de dépendance
                        ajouterRelation(type.getName(), className, Fleche.DEPENDANCE, "1", "1", field.getName());
                    } else {
                        // si la classe de l'attribut n'est pas dans le diagramme
                        // on traite les types paramétrés
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

    /**
     * met à jour les relations entre les classes
     */
    public void updateRelations(){
        // pour toutes les classes du diagramme
        for (Classe classe : classes.values()) {
            try {
                // on met à jour les relations d'héritage et d'attributs
                updateRelationHeritage(classe);
                updateRelationAttributs(classe);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // on vérifie les relations pour supprimer les relations entre les classes qui ne sont plus dans le diagramme
        checkRelations();
        notifierObservateur();
    }

    /**
     * retourne true si la classe est dans le diagramme
     * @param nom
     * @return
     */
    public boolean isInDiagram(String nom){
        return classes.containsKey(nom);
    }

    /**
     * vérifie les relations entre les classes
     */
    public void checkRelations(){
        // on parcourt les flèches
        Iterator<Fleche> it = relations.iterator();
        while (it.hasNext()){
            Fleche r = it.next();
            String nomParent = r.getParent().getRealName();
            String nomEnfant = r.getEnfant().getRealName();

            // si l'une des classes n'est plus dans le diagramme
            if (!isInDiagram(nomEnfant) || !isInDiagram(nomParent)){
                // on retire la relation
                it.remove();
                // on retire 1 au nombre de relations entre les 2 classes
                String keyEnfantParent = nomParent+nomEnfant;
                Fleche.retirer1Relation(keyEnfantParent);
            }
        }
    }

    /**
     * retourne le diagramme UML en format String
     * @return
     */
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

    /**
     * supprime une classe du diagramme
     * @param nom
     */
    public void supprimerClasse(String nom){
        classes.remove(nom);
        updateRelations();
        UML = createUML();
        notifierObservateur();
    }

    /**
     * supprime toutes les classes du diagramme
     */
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

    /**
     * sauvegarde le diagramme dans un fichier
     * @param fileP
     */
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

    /**
     * charge un diagramme depuis un fichier
     * @param file
     */
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
            for (String s : classesKey) {
                c = res.get(s);
                if (c.getAbsolutePath() == null) {
                    String type = "class";
                    if (c.isInterface()) {
                        type = "interface";
                    } else if (c.isAbstract()) {
                        type = "abstract";
                    }
                    this.ajouterClasseInexistante(c.getNom(), type, c.getX(), c.getY(), c.getAttributs(), c.getMethodes());
                } else {
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

    /**
     * génère le code source des classes du diagramme
     * @return
     */
    public boolean genererCodeSource() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            String dossier = "squelette/";
            supprimerDossier(dossier);
            for (Classe c : classes.values()) {
                try {
                    String nom = c.getNom();
                    String nomPackage = c.getNomPackage();
                    String fullChemin = dossier+nomPackage.replace(".", "/")+ "/";
                    File dossierFichier = new File(fullChemin);
                    dossierFichier.mkdirs();

                    File fichier = new File(fullChemin+nom + ".java");
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

    public void supprimerDossier(String dossier){
        File dossierFichier = new File(dossier);
        if (dossierFichier.exists()) {
            File[] fichiers = dossierFichier.listFiles();
            for (File f : fichiers) {
                if (f.isDirectory()) {
                    supprimerDossier(f.getAbsolutePath());
                } else {
                    f.delete();
                }
            }
            dossierFichier.delete();
        }
    }

    /**
     * ajoute une méthode à une classe donnée
     * @param nomClasse
     * @param nomMethode
     * @param typeRetour
     * @param typeParam
     * @param modifier
     */
    public void ajouterMethode(String nomClasse, String nomMethode, String typeRetour, String typeParam, int modifier){
        // on récupère la classe
        Classe classe = classes.get(nomClasse);
        if (classe != null) {
            // on récupère les paramètres de la méthode
            List<Parametre> param = new ArrayList<>();
            for (String s : typeParam.split(",")) {
                param.add(new Parametre(s));
            }
            // on ajoute la méthode à la classe
            Methode methode = new Methode(nomMethode, typeRetour, param, modifier);
            classe.ajouterMethode(methode);
            updateRelations();
            UML = createUML();
            notifierObservateur();
        }
    }

    /**
     * ajoute un constructeur à une classe donnée
     * @param nomClasse
     * @param typeParam
     * @param modifier
     */
    public void ajouterConstructeur(String nomClasse, String typeParam, int modifier){
        // on récupère la classe
        Classe classe = classes.get(nomClasse);
        if (classe != null) {
            // on récupère les paramètres du constructeur
            List<Parametre> param = new ArrayList<>();
            for (String s : typeParam.split(",")) {
                param.add(new Parametre(s));
            }
            String nomMethode = nomClasse.substring(nomClasse.lastIndexOf(".")+1);
            // on ajoute le constructeur à la classe
            Methode methode = new Methode(nomMethode, param, modifier);
            classe.ajouterMethode(methode);
            updateRelations();
            UML = createUML();
            notifierObservateur();
        }
    }

    /**
     * ajoute un attribut à une classe donnée
     * @param nomClasse
     * @param nomAttribut
     * @param typeAttribut
     * @param modifier
     */
    public void ajouterAttribut(String nomClasse, String nomAttribut, String typeAttribut, int modifier){
        // on récupère la classe
        Classe classe = classes.get(nomClasse);
        if (classe != null) {
            // on ajoute l'attribut à la classe
            Attribut attribut = new Attribut(nomAttribut, typeAttribut, modifier);
            classe.getAttributs().add(attribut);
            updateRelations();
            UML = createUML();
            notifierObservateur();
        }
    }

    public static boolean isVoirGetSet() {
        return voirGetSet;
    }

    public void setVoirGetSet(boolean voirGetSet) {
        this.voirGetSet = voirGetSet;
        notifierObservateur();
    }

    public static boolean isVoirFleches() {
        return voirFleches;
    }

    public void setVoirFleches(boolean voirFleches) {
        this.voirFleches = voirFleches;
        notifierObservateur();
    }

    public static boolean isVoirAttributsHerites() {
        return voirAttributsHerites;
    }

    public void setVoirAttributsHerites(boolean voirAttributsHerites) {
        this.voirAttributsHerites = voirAttributsHerites;
        notifierObservateur();
    }
}