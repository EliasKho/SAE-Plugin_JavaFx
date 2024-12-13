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

        s+=getVisiClass(classe)+" "+getEtatClass(classe)+" class "+getNom(classe)+"\n"
                +getPackage(classe)+"\n";
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
        String s="";

        if (Modifier.isAbstract(modifiers)) {
            s= "abstract";
        } else if (Modifier.isStatic(modifiers)) {
            s= "static";
        } else if (Modifier.isFinal(modifiers)) {
            s= "final";
        } else if (Modifier.isInterface(modifiers)){
            s= "interface";
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
        return getVisiAttribut(field)+" "+getEtatAtribut(field)+" "+getNom(field.getType())+" "+field.getName();
    }

    public String getVisiAttribut(Field field) {
        int modifiers = field.getModifiers();
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

    public String getEtatAtribut(Field field) {
        int modifiers = field.getModifiers();
        String s="";

        if (Modifier.isAbstract(modifiers)) {
            s= "abstract";
        } else if (Modifier.isStatic(modifiers)) {
            s= "static";
        } else if (Modifier.isFinal(modifiers)) {
            s= "final";
        }
        return s;
    }

    public Method[] getMethodes(Class<?> classe){
        return classe.getDeclaredMethods();
    }

    public String getMethode(Method method) {
        String s= getVisiMethode(method)+" "+getEtatMethode(method)+" "+
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

    public String getVisiMethode(Method method) {
        int modifiers = method.getModifiers();
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

    public String getEtatMethode(Method method) {
        int modifiers = method.getModifiers();
        String s="";

        if (Modifier.isAbstract(modifiers)) {
            s= "abstract";
        } else if (Modifier.isStatic(modifiers)) {
            s= "static";
        } else if (Modifier.isFinal(modifiers)) {
            s= "final";
        }
        return s;
    }

}
