package projet.classes;

import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Objects;

public class Attribut {
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
        String affichage="";
        if(Modifier.isPublic(modifier)){
            affichage+="+";
        }
        if(Modifier.isProtected(modifier)){
            affichage+="#";
        }
        if(Modifier.isPrivate(modifier)){
            affichage+="-";
        }
        if(Modifier.isStatic(modifier)){
            affichage+="{static} ";
        }
        if(Modifier.isAbstract(modifier)){
            affichage+="{abstract} ";
        }
        return affichage+nom+":"+ type;
    }

    public String getString() {
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
        return affichage+nom+":"+ type;
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
