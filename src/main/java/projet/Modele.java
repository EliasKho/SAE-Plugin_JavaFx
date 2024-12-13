package projet;

import projet.arborescence.Analyseur;
import projet.arborescence.FileComposite;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;

public class Modele implements Sujet{

    private FileComposite file;
    private List<Observateur> observateurs;
    private String introspection;

    public Modele(FileComposite f) {
        this.file = f;
        this.observateurs = new ArrayList<Observateur>();
    }

    public FileComposite getPath() {
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

    public void saveIntrospection(String filename){
        try {
            System.out.println(filename);
            introspection = introspection(filename);
            System.out.println(introspection);
            notifierObservateur();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public String getIntrospection(){
        return introspection;
    }
    
    
    //Introspection

    public String introspection(String nomClasse) throws ClassNotFoundException {

        // nom de la classe
        Class<?> classe = Class.forName(nomClasse);

        String s="Classe: ";

        s+=verifClasse(getAffichage(classe,classe.getModifiers()))+getNom(classe)+"\n"
                +"Package: "+getPackage(classe)+"\n";
        s+="----------------\nAttributs:\n";
        for(Field field:classe.getDeclaredFields()){
            s+=attributToString(classe,field)+"\n";
        }
        s+="----------------\nConstructeurs:\n";
        for(String constructeur:getConstucteurs(classe)){
            s+=constructeur+"\n";
        }
        s+="----------------\nMéthodes:\n";
        for(Method method:classe.getDeclaredMethods()){
            s+=getMethode(classe,method)+"\n";
        }
        s+="----------------\nHéritage:\n"+getHeritage(classe);

        return s;
    }

    public String getNom(Class<?> classe) {
        return classe.getName().substring(classe.getName().lastIndexOf('.')+1);
    }

    public String getPackage(Class<?> classe) {
        return classe.getPackage().getName();
    }

    public String getAffichage(Class<?> classe, int mod){
        String modifier="";

        if(Modifier.isPublic(mod)){
            modifier+="public ";
        }
        if(Modifier.isProtected(mod)){
            modifier+="protected ";
        }
        if(Modifier.isPrivate(mod)){
            modifier+="private ";
        }
        if(Modifier.isFinal(mod)){
            modifier+="final ";
        }
        if(Modifier.isStatic(mod)){
            modifier+="static ";
        }
        if(Modifier.isAbstract(mod)){
            modifier+="abstract ";
        }
        if(classe.isInterface()){
            modifier = modifier.replace("abstract ","interface ");
        }
        return modifier;
    }

    public String verifClasse(String input){
        if(!input.contains("interface")){
            input += "class ";
        }
        return input;
    }

    public List<String> getConstucteurs(Class<?> classe) {
        Constructor<?>[] constructeurs = classe.getDeclaredConstructors();
        List<String> list = new ArrayList<>();
        for (Constructor<?> constructeur : constructeurs) {
            String s="";
            s+=constructeur.getName().substring(classe.getName().lastIndexOf('.')+1) +"(";
            int n = constructeur.getParameterTypes().length;
            int i=1;
            for(Class<?> type:constructeur.getParameterTypes()){
                s+=getNom(type);
                if(i<n){
                    s+=",";
                }
                i++;
            }
            s+=")";
            list.add(s);
        }
        return list;
    }

    public String attributToString(Class<?> classe,Field field) {
        return getAffichage(classe,field.getModifiers())+getNom(field.getType())+" "+field.getName();
    }


    public String getMethode(Class<?> classe,Method method) {
        String s= getAffichage(classe,method.getModifiers())+
                method.getName()+"(";
        int n=method.getParameterTypes().length;
        int i=1;
        for(Class<?> param :method.getParameterTypes()){
            s+=getNom(param);
            if(i<n){
                s+=",";
            }
            i++;
        }
        s+="): "+getNom(method.getReturnType());
        return s;
    }

    public String getHeritage(Class<?> classe) {
        String s = "";

        // Héritage direct (extends)
        Class<?> superClass = classe.getSuperclass();
        if (superClass != null && superClass != Object.class) {
            s+="Classe Mère:\n   "+getNom(superClass)+"\n";
        }

        // Interfaces implémentées (implements)
        Class<?>[] interfaces = classe.getInterfaces();
        if (interfaces.length > 0) {
            s+="Interfaces:\n";
            for (int i = 0; i < interfaces.length; i++) {
                s+="   "+getNom(interfaces[i]);
                if (i < interfaces.length - 1) {
                    s+="\n";
                }
            }
        }
        return s.trim();
    }

    public String getUML(Class<?> classe)throws ClassNotFoundException{
        String res = "@startuml\n";
        String visiClass = null;
        switch (this.getVisiClass(classe)){
            case "public ":
                visiClass = "+";
                break;
            case "private ":
                visiClass = "-";
                break;
            case "protected ":
                visiClass = "#";
                break;
            default:
                visiClass = "+";
        }
        res += visiClass + getEtatClass(classe) + " class " + getNom(classe) + " {\n";

        String visiAttribut = null;
        String etatAttribut = null;
        Field[] attributs = getAttributs(classe);

        for(int i = 0; i<attributs.length; i++) {
            switch (this.getVisiAttribut(attributs[i])) {
                case "public ":
                    visiAttribut = "+";
                    break;
                case "private ":
                    visiAttribut = "-";
                    break;
                case "protected ":
                    visiAttribut = "#";
                    break;
                case "package private ":
                    visiAttribut = "~";
                    break;
                default:
                    visiAttribut = "+";
            }
            switch (this.getEtatAtribut(attributs[i])) {
                case "abstract ":
                    etatAttribut = "{abstract}";
                    break;
                case "static ":
                    etatAttribut = "{static}";
                    break;
                case "final ":
                    etatAttribut = "";
                    break;
                default:
                    etatAttribut = "";
            }
            res += visiAttribut + etatAttribut + attributs[i].getName() + " : \n";
        }

        String visiMethod = null;
        String etatMethod = null;
        Method[] methode = getMethodes(classe);

        for(int j=0; j< methode.length; j++) {
            switch (this.getVisiMethode(methode[j])) {
                case "public ":
                    visiMethod = "+";
                    break;
                case "private ":
                    visiMethod = "-";
                    break;
                case "protected ":
                    visiMethod = "#";
                    break;
                default:
                    visiMethod = "+";
            }
            switch(this.getEtatMethode(methode[j])) {
                case "abstract ":
                    etatMethod = "{abstract}";
                    break;
                case "static ":
                    etatMethod = "{static}";
                    break;
                case "final ":
                    etatMethod = "";
                    break;
                default:
                    etatMethod = "";
            }

            String parameterMethode = getMethode(methode[j]);
            res += visiMethod + etatMethod + methode[j].getName() + "(" + parameterMethode + ") :" + "\n";
        }

        res += "}\n@enduml";
        return res;
    }

}
