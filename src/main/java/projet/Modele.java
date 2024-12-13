package projet;

import projet.arborescence.Analyseur;
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
//            String puml = fonctionPuml(filename);
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("Diag.puml"))) {
                writer.write(introspection);
//                writer.write(puml);
            } catch (IOException e) {
                e.printStackTrace();
            }
            notifierObservateur();
            System.out.println(getUML(introspection));
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

        s+=verifClasse(getAffichage(classe,classe.getModifiers()))+getNom(classe)+"\n"
                +"----------------\nPackage: "+getPackage(classe)+"\n";
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
        s+=getHeritage(classe);

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
            s+="----------------\nClasse Mère:\n   "+getNom(superClass)+"\n";
        }

        // Interfaces implémentées (implements)
        Class<?>[] interfaces = classe.getInterfaces();
        if (interfaces.length > 0) {
            s+="----------------\nInterfaces:\n";
            for (int i = 0; i < interfaces.length; i++) {
                s+="   "+getNom(interfaces[i]);
                if (i < interfaces.length - 1) {
                    s+="\n";
                }
            }
        }
        return s.trim();
    }

    public String getUML(String introspection)throws ClassNotFoundException{
        StringBuilder mid = new StringBuilder();
        String[] pull = introspection.split("\n");
        String resStart = "@startuml\n";

        int i = 0;
        for (i=0;i<pull.length;i++){
            String ligne=pull[i];
            if(ligne.contains("-----")){
                i+=1;
            }
            else {
                if (ligne.contains("public")) {
                    ligne=ligne.replace("public ", "+");
                } else if (ligne.contains("private")) {
                    ligne=ligne.replace("private ", "-");
                } else if (ligne.contains("protected")) {
                    ligne=ligne.replace("protected ", "#");
                }

                if (ligne.contains("abstract")) {
                    ligne=ligne.replace("abstract ", "{abstract}");
                } else if (ligne.contains("static")) {
                    ligne=ligne.replace("static ", "{static}");
                }

                mid.append(ligne).append("\n");
                if(i==2){
                    mid.append("{\n");
                }
            }
        }



        String res = resStart + mid + "}\n@enduml";

        return res;
    }

}
