package projet.classes;

import java.util.Objects;

public class Relation {

    public static final String IMPLEMENTS = ".u.|>";
    public static final String EXTENDS = "-u-|>";
    public static final String DEPENDANCE = "-->";

    private Classe parent;
    private Classe enfant;
    private String type;
    private String nom;

    private String parentCardinalite;
    private String enfantCardinalite;

    public Relation(Classe parent, Classe enfant, String type) {
        this.parent = parent;
        this.enfant = enfant;
        this.type = type;
        this.nom = "\"\"";
        this.parentCardinalite = null;
        this.enfantCardinalite = null;
    }

    public Classe getParent() {
        return parent;
    }
    public void setParent(Classe parent) {
        this.parent = parent;
    }

    public Classe getEnfant() {
        return enfant;
    }
    public void setEnfant(Classe enfant) {
        this.enfant = enfant;
    }

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    public String getUMLString() {
        if (enfantCardinalite == null || parentCardinalite == null) {
            return enfant.getNom() + " " + type + " " + parent.getNom() + " : " + nom;
        }
        return enfant.getNom() + " \""+enfantCardinalite+"\" " + type + " \""+parentCardinalite+"\" " + parent.getNom()+ " : " + nom;
    }

    public String getNom() {
        return nom;
    }
    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getParentCardinalite() {
        return parentCardinalite;
    }
    public void setParentCardinalite(String parentCardinalite) {
        this.parentCardinalite = parentCardinalite;
    }

    public String getEnfantCardinalite() {
        return enfantCardinalite;
    }
    public void setEnfantCardinalite(String enfantCardinalite) {
        this.enfantCardinalite = enfantCardinalite;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Relation relation)) return false;
        return Objects.equals(getParent(), relation.getParent()) && Objects.equals(getEnfant(), relation.getEnfant()) && Objects.equals(getType(), relation.getType());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getParent(), getEnfant(), getType());
    }
}
