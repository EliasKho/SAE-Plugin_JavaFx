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
        // le string recu correspond au package de la classe du type, on ne veut que le nom de la classe
        this.type = type.substring(type.lastIndexOf(".")+1);
        this.modifier = modifier;
    }

    /**
     * Retourne une chaine de caractère représentant l'attribut en UML
     * @return
     */
    public String getUMLString() {
        String affichage = getModifierUMLString();
        return affichage+nom+":"+ type;
    }

    /**
     * Retourne une chaine de caractère représentant l'attribut pour l'affichage sur l'interface
     * @return
     */
    public String getString() {
        String affichage = getModifierString();
        return affichage+nom+":"+ type;
    }

    /**
     * Retourne une chaine de caractère représentant l'attribut en code
     * @return
     */
    public String getStringCode() {
        String affichage = getModifierString();
        return affichage+type+" "+nom+";";
    }

    /**
     * Retourne une chaine de caractère représentant le modificateur de l'attribut en UML
     * @return
     */
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

    /**
     * Retourne une chaine de caractère représentant le modificateur de l'attribut pour l'affichage sur l'interface
     * @return
     */
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

    // GETTERS & SETTERS
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
