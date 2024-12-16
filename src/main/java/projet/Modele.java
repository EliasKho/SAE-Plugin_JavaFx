package projet;

import projet.arborescence.FileComposite;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
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

    public void saveIntrospection(String filename){
        try {
            System.out.println(filename);
            introspection = introspection(filename);
            System.out.println(introspection);
            System.out.println(getUML(introspection));
            String puml = getUML(introspection);
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("Diag.puml"))) {
                writer.write(puml);
            } catch (IOException e) {
                e.printStackTrace();
            }
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

        String s="----------------\nClasse: \n";

        s+=getInterface(classe,getAffichage(classe.getModifiers()))+getNom(classe)+"\n"
                +"----------------\nPackage: "+getPackage(classe)+"\n";
        s+="----------------\nAttributs:\n";
        for(Field field:classe.getDeclaredFields()){
            s+= getAttribut(field)+"\n";
        }
        s+="----------------\nConstructeurs:\n";
        for(Constructor<?> constructeur: classe.getDeclaredConstructors()){
            s+=getMethodeConstruct(constructeur)+"\n";
        }
        s+="----------------\nMéthodes:\n";
        for(Method method:classe.getDeclaredMethods()){
            s+=getMethodeConstruct(method)+"\n";
        }
        s+=getHeritage(classe);

        return s;
    }

    public String getNom(Class<?> classe) {
        return classe.getName().substring(classe.getName().lastIndexOf('.')+1);
    }

    public String getPackage(Class<?> classe) {
        return classe.getPackage().getName();
    }

    public String getAffichage(int mod){
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
        return modifier;
    }

    public String getInterface(Class<?> classe, String modifier){
        if(classe.isInterface()){
            modifier = modifier.replace("abstract ","interface ");
        }
        else{
            modifier+="class ";
        }
        return modifier;
    }

    public String getMethodeConstruct(Executable executable) {
        String s= getAffichage(executable.getModifiers());
        if (executable instanceof Constructor) {
            s+=getNom(((Constructor<?>) executable).getDeclaringClass())+"(";
        }
        else if (executable instanceof Method) {
            s+=((Method) executable).getName()+"(";
        }
        int n=executable.getParameterTypes().length;
        int i=1;
        for(Class<?> param :executable.getParameterTypes()){
            s+=getNom(param);
            if(i<n){
                s+=",";
            }
            i++;
        }
        s+="): ";
        if (executable instanceof Method) {
            s+=getNom(((Method) executable).getReturnType());
        }
        return s;
    }

    public String getAttribut(Field field) {
        return getAffichage(field.getModifiers())+getNom(field.getType())+" "+field.getName();
    }

    public String getHeritage(Class<?> classe) {
        String s = "";

        // Héritage direct (extends)
        Class<?> superClass = classe.getSuperclass();
        if (superClass != null && superClass != Object.class) {
            s+="----------------\nClasse Mère:\n extends "+getNom(superClass)+"\n";
        }

        // Interfaces implémentées (implements)
        Class<?>[] interfaces = classe.getInterfaces();
        if (interfaces.length > 0) {
            s+="----------------\nInterfaces:\n implements ";
            for (int i = 0; i < interfaces.length; i++) {
                s+=getNom(interfaces[i]);
                if (i < interfaces.length - 1) {
                    s+=", ";
                }
            }
        }
        return s.trim();
    }

    public String getUML(String introspection)throws ClassNotFoundException{
        StringBuilder mid = new StringBuilder("@startuml\n");
        String[] pull = introspection.split("\n");
        String lignes[] = new String[pull.length];

        int j = 0;
        for (int i=0;i<pull.length;i++){
            String ligne=pull[i];
            if(ligne.contains("-----")){
                i+=1;
            }
            else {
                if (ligne.contains("extends")) {
                    int l = lignes[0].lastIndexOf("{");
                    lignes[0] = lignes[0].substring(0, l) + ligne + "{";
                }
                else if (ligne.contains("implements")) {
                    int l = lignes[0].lastIndexOf("{");
                    lignes[0] = lignes[0].substring(0, l) + ligne + "{";
                }
                else {
                    if (ligne.contains("public")) {
                        ligne = ligne.replace("public ", "+");
                    } else if (ligne.contains("private")) {
                        ligne = ligne.replace("private ", "-");
                    } else if (ligne.contains("protected")) {
                        ligne = ligne.replace("protected ", "#");
                    }

                    if (i == 2) {
                        ligne = ligne + "{";
                        ligne = ligne.replace("+", "");
                    }
                    else {
                        if (ligne.contains("abstract")) {
                            ligne = ligne.replace("abstract ", "{abstract}");
                        } else if (ligne.contains("static")) {
                            ligne = ligne.replace("static ", "{static}");
                        }
                    }

                    lignes[j] = ligne;
                }
                j++;
            }
        }
        for (String l : lignes){
            if(l!=null){
                mid.append(l).append("\n");
            }
        }

        String res =  mid + "}\n@enduml";

        return res;
    }

}
