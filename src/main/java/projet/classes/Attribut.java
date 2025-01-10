package projet.classes;

import java.io.Serializable;
import java.lang.reflect.Modifier;
import java.util.Objects;

public class Attribut implements Serializable {
    private String nom;
    private String type;
    private int modifier;

    public Attribut(String nom, String type, int modifier) {
        this.nom = nom;
        this.type = type.substring(type.lastIndexOf(".")+1);
        this.modifier = modifier;
    }

    public String getNom() {
        return nom;
    }
    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    public int getModifier() {
        return modifier;
    }

    public String getUMLString() {
        String affichage = getModifierUMLString();
        return affichage+nom+":"+ type;
    }

    public String getString() {
        String affichage = getModifierString();
        return affichage+nom+":"+ type;
    }

    public String getStringCode() {
        String affichage = getModifierString();
        return affichage+type+" "+nom+";";
    }

    public String getModifierUMLString(){
        String affichage="";
        if(Modifier.isPublic(modifier)){
            affichage="+";
        }
        if(Modifier.isProtected(modifier)){
            affichage="#";
        }
        if(Modifier.isPrivate(modifier)){
            affichage="-";
        }
        if(Modifier.isStatic(modifier)){
            affichage="+{static} ";
        }
        if(Modifier.isAbstract(modifier)){
            affichage="+{abstract} ";
        }
        return affichage;
    }

    public String getModifierString(){
        String affichage="";
        if(Modifier.isPublic(modifier)){
            affichage+="public ";
        }
        if(Modifier.isProtected(modifier)){
            affichage+="protected ";
        }
        if(Modifier.isPrivate(modifier)){
            affichage+="private ";
        }
        if(Modifier.isStatic(modifier)){
            affichage+="static ";
        }
        if(Modifier.isAbstract(modifier)){
            affichage+="abstract ";
        }
        return affichage;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Attribut attribut)) return false;
        return getModifier() == attribut.getModifier() && Objects.equals(getNom(), attribut.getNom()) && Objects.equals(getType(), attribut.getType());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getNom(), getType(), getModifier());
    }
}
