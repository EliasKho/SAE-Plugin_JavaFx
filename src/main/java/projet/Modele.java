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

        s+=getVisiClass(classe)+getEtatClass(classe)+getNom(classe)+"\n"
                +"Package: "+getPackage(classe)+"\n";
        s+="----------------\nAttributs:\n";
        for(Field field:getAttributs(classe)){
            s+=attributToString(field)+"\n";
        }
        s+="----------------\nConstructeurs:\n";
        for(String constructeur:getConstucteurs(classe)){
            s+=constructeur+"\n";
        }
        s+="----------------\nMéthodes:\n";
        for(Method method:getMethodes(classe)){
            s+=getMethode(method)+"\n";
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

    public String getVisiClass(Class<?> classe) {
        int modifiers = classe.getModifiers();
        String s="";

        if (Modifier.isPublic(modifiers)) {
            s= "public";
        } else if (Modifier.isProtected(modifiers)) {
            s= "protected";
        } else if (Modifier.isPrivate(modifiers)) {
            s= "private";
        }
        return s;
    }

    public String getEtatClass(Class<?> classe) {
        int modifiers = classe.getModifiers();
        String s=" ";

        if (Modifier.isStatic(modifiers))
            s+= "static ";
        if (Modifier.isFinal(modifiers))
            s+= "final ";
        if (classe.isInterface())
            s+="interface ";
        else {
            if (Modifier.isAbstract(modifiers))
                s += "abstract ";
            s+="class ";
        }
        return s;
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

    public Field[] getAttributs(Class<?> classe) {
        return classe.getDeclaredFields();
    }

    public String attributToString(Field field) {
        return getVisiAttribut(field)+getEtatAtribut(field)+getNom(field.getType())+" "+field.getName();
    }

    public String getVisiAttribut(Field field) {
        int modifiers = field.getModifiers();
        String s=" ";

        if (Modifier.isPublic(modifiers)) {
            s= "public ";
        } else if (Modifier.isProtected(modifiers)) {
            s= "protected ";
        } else if (Modifier.isPrivate(modifiers)) {
            s= "private ";
        }
        return s;
    }

    public String getEtatAtribut(Field field) {
        int modifiers = field.getModifiers();
        String s="";

        if (Modifier.isAbstract(modifiers)) {
            s= "abstract ";
        } else if (Modifier.isStatic(modifiers)) {
            s= "static ";
        } else if (Modifier.isFinal(modifiers)) {
            s= "final ";
        }
        return s;
    }

    public Method[] getMethodes(Class<?> classe){
        return classe.getDeclaredMethods();
    }

    public String getMethode(Method method) {
        String s= getVisiMethode(method)+getEtatMethode(method)+
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
        s+="):"+getNom(method.getReturnType());
        return s;
    }

    public String getVisiMethode(Method method) {
        int modifiers = method.getModifiers();
        String s=" ";

        if (Modifier.isPublic(modifiers)) {
            s= "public ";
        } else if (Modifier.isProtected(modifiers)) {
            s= "protected ";
        } else if (Modifier.isPrivate(modifiers)) {
            s= "private ";
        }
        return s;
    }

    public String getEtatMethode(Method method) {
        int modifiers = method.getModifiers();
        String s="";

        if (Modifier.isAbstract(modifiers)) {
            s= "abstract ";
        } else if (Modifier.isStatic(modifiers)) {
            s= "static ";
        } else if (Modifier.isFinal(modifiers)) {
            s= "final ";
        }
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
