package projet.classes;

import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;

public class Methode {
    private String nom;
    private Type typeRetour;
    private List<Parametre> parametres;
    private int modifier;
    private boolean constructeur;

    public Methode(String nom, Type typeRetour, List<Parametre> parametres, int modifier) {
        this.nom = nom;
        this.typeRetour = typeRetour;
        this.parametres = parametres;
        this.modifier = modifier;
        this.constructeur = false;
    }

    public Methode(String nom, List<Parametre> parametres, int modifier) {
        this.nom = nom.substring(nom.lastIndexOf(".")+1);
        this.parametres = parametres;
        this.modifier = modifier;
        this.constructeur = true;
    }

    public String getNom() {
        return nom;
    }
    public void setNom(String nom) {
        this.nom = nom;
    }

    public Type getTypeRetour() {
        return typeRetour;
    }
    public void setTypeRetour(Type typeRetour) {
        this.typeRetour = typeRetour;
    }

    public List<Parametre> getParametres() {
        return parametres;
    }
    public void setParametres(List<Parametre> parametres) {
        this.parametres = parametres;
    }

    public int getModifier() {
        return modifier;
    }
    public void setModifier(int modifier) {
        this.modifier = modifier;
    }

    public boolean isConstructeur() {
        return constructeur;
    }
    public void setConstructeur(boolean constructeur) {
        this.constructeur = constructeur;
    }

    public String getUMLString() {
        String affichage=getModifierUMLString();
        affichage+=getParametersString();
        if(!constructeur){
            affichage+= ":"+getTypeRetourString();
        }
        return affichage;
    }

    public String getString(){
        String affichage=getModifierString();
        affichage+=getParametersString();
        if(!constructeur){
            affichage+= ":"+getTypeRetourString();
        }
        return affichage;
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

    private String getParametersString() {
        String affichage=nom+"(";
        if (parametres != null) {
            for (Parametre parametre : parametres) {
                affichage+= parametre.getType()+",";
            }
            if(parametres.size()>0){
                affichage=affichage.substring(0, affichage.length()-1);
            }
        }
        affichage+= ")";
        return affichage;
    }

    public String getTypeRetourString(){
        return typeRetour.getTypeName().substring(typeRetour.getTypeName().lastIndexOf(".")+1);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Methode methode)) return false;
        return getModifier() == methode.getModifier() && isConstructeur() == methode.isConstructeur() && Objects.equals(getNom(), methode.getNom()) && Objects.equals(getTypeRetour(), methode.getTypeRetour()) && Objects.equals(getParametres(), methode.getParametres());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getNom(), getTypeRetour(), getParametres(), getModifier(), isConstructeur());
    }
}
