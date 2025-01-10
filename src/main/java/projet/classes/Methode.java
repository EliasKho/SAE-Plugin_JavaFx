package projet.classes;

import java.io.Serializable;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Objects;

public class Methode implements Serializable {
    private String nom;
    private String typeRetour;
    private List<Parametre> parametres;
    private int modifier;
    private boolean constructeur;

    /**
     * Constructeur d'une méthode
     * @param nom
     * @param typeRetour
     * @param parametres
     * @param modifier
     */
    public Methode(String nom, String typeRetour, List<Parametre> parametres, int modifier) {
        // le string recu correspond au package de la classe du type, on ne veut que le nom de la classe
        if (typeRetour.contains(".")){
            this.typeRetour = typeRetour.substring(typeRetour.lastIndexOf(".")+1);
        }
        else {
            // si le type n'a pas de package, c'est un type primitif ou bien c'est une méthode ajoutées par l'utilisateur
            this.typeRetour = typeRetour;
        }
        this.nom = nom;
        this.parametres = parametres;
        this.modifier = modifier;
        this.constructeur = false;
    }

    /**
     * Constructeur d'un constructeur
     * @param nom
     * @param parametres
     * @param modifier
     */
    public Methode(String nom, List<Parametre> parametres, int modifier) {
        // dans le cas d'un constructeur, le nom est donné par le nom de la classe et donc avec son package
        this.nom = nom.substring(nom.lastIndexOf(".")+1);
        this.parametres = parametres;
        this.modifier = modifier;
        this.constructeur = true;
    }

    // GETTERS ET SETTERS

    public String getNom() {
        return nom;
    }
    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getTypeRetour() {
        return typeRetour;
    }

    public List<Parametre> getParametres() {
        return parametres;
    }

    public int getModifier() {
        return modifier;
    }

    public boolean isConstructeur() {
        return constructeur;
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

    public String getStringCode(){
        StringBuilder affichage= new StringBuilder(getModifierString());
        if(!constructeur){
            affichage.append(getTypeRetourString()).append(" ");
        }
        affichage.append(nom).append("(");
        if (parametres != null) {
            int i=1;
            String initialeType="";
            for (Parametre parametre : parametres) {
                if (initialeType.equals(String.valueOf(parametre.getType().charAt(0)))){
                    affichage.append(parametre.getType()).append(" ").append(initialeType.toLowerCase()).append(i).append(",");
                    i++;
                }
                else {
                    affichage.append(parametre.getType()).append(" ").append(String.valueOf(parametre.getType().charAt(0)).toLowerCase()).append(",");
                    i = 1;
                }
                initialeType = String.valueOf(parametre.getType().charAt(0));
            }
            //supprimer la derniere virgule
            if(!parametres.isEmpty()){
                affichage = new StringBuilder(affichage.substring(0, affichage.length() - 1));
            }
        }
        affichage.append(")");
        return affichage.toString();
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

    private String getParametersString() {
        StringBuilder affichage= new StringBuilder(nom + "(");
        if (parametres != null) {
            for (Parametre parametre : parametres) {
                affichage.append(parametre.getType()).append(",");
            }
            if(!parametres.isEmpty()){
                affichage = new StringBuilder(affichage.substring(0, affichage.length() - 1));
            }
        }
        affichage.append(")");
        return affichage.toString();
    }

    public String getTypeRetourString(){
        return typeRetour;
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
