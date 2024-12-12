package projet.arborescence;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;

public class Analyseur {

    public String introspection(String nomClasse) throws ClassNotFoundException {

        // nom de la classe
        Class<?> classe = Class.forName(nomClasse);

        // constructeurs
        Constructor<?>[] constructeursDeclares = classe.getDeclaredConstructors();

        // attributs
        Field[] fieldsDeclares = classe.getDeclaredFields();

        // méthodes
        Method[] methodsDeclares = classe.getDeclaredMethods();

        // interfaces
        Class<?>[] interfaces = classe.getInterfaces();

        // classe héritée
        Class<?> superClass = classe.getSuperclass();
        
        String s="";

        s+=getVisiClass(classe)+" "+getEtatClass(classe)+" "+getNom(classe)+"\n"
                +getPackage(classe)+"\n";
        s+="----------------\n";
        for(Field field:getAttributs(classe)){
            s+=attributToString(field)+"\n";
        }
        s+="----------------\n";
        for(String constructeur:getConstucteurs(classe)){
            s+=constructeur+"\n";
        }
        for(Method method:getMethodes(classe)){
            s+=getMethode(method)+"\n";
        }

        return s;
    }

    public String getNom(Class<?> classe) {
        return classe.getName();
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
        }
        return s;
    }

    public List<String> getConstucteurs(Class<?> classe) {
        Constructor<?>[] constructeurs = classe.getDeclaredConstructors();
        List<String> list = new ArrayList<>();
        for (Constructor<?> constructeur : constructeurs) {
            String s="";
            s+=constructeur.getName()+"(";
            int n = constructeur.getParameterTypes().length;
            int i=1;
            for(Class<?> type:constructeur.getParameterTypes()){
                s+=type.getName();
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
        return classe.getFields();
    }

    public String attributToString(Field field) {
        return getVisiAttribut(field)+" "+getEtatAtribut(field)+" "+field.getType().getName()+" "+field.getName();
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
        return classe.getMethods();
    }

    public String getMethode(Method method) {
        String s= getVisiMethode(method)+" "+getEtatMethode(method)+" "+
                method.getName()+"(";
        int n=method.getParameterTypes().length;
        int i=1;
        for(Class<?> param :method.getParameterTypes()){
            s+=param.getTypeName();
            if(i<n){
                s+=",";
            }
            i++;
        }
        s+="): "+method.getReturnType().getName();
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

