package projet.classes;

import java.io.Serializable;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
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
    public void setModifier(int modifier) {
        this.modifier = modifier;
    }

    public String getUMLString() {
        String affichage = getModifierUMLString();
        return affichage+nom+":"+ type;
    }

    public String getString() {
        String affichage = getModifierString();
        return affichage+nom+":"+ type;
    }

    public String getModifierUMLString(){
        String affichage="";
        if(modifier == 1){
            affichage+="+";
        }
        if(modifier == 4){
            affichage+="#";
        }
        if(modifier == 2){
            affichage+="-";
        }
        if(modifier == 9){
            affichage+="+{static} ";
        }
        if(modifier == 1025){
            affichage+="+{abstract} ";
        }
        return affichage;
    }

    public String getModifierString(){
        String affichage="";
        if(modifier == 1){
            affichage+="public ";
        }
        if(modifier == 4){
            affichage+="protected ";
        }
        if(modifier == 2){
            affichage+="private ";
        }
        if(modifier == 9){
            affichage+="public static ";
        }
        if(modifier == 1025){
            affichage+="public abstract ";
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
