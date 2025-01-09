package projet.classes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Classe implements Serializable{
    private final String nom;
    private List<Methode> methodes;
    private List<Attribut> attributs;
    private boolean isInterface;
    private boolean isAbstract;
    private String nomPackage;
    private double x;
    private double y;
    private double longueur;
    private double largeur;

    public Classe(String nom){
        this.nom = nom.substring(nom.lastIndexOf(".")+1);
        this.nomPackage = nom.substring(0, nom.lastIndexOf("."));
        methodes = new ArrayList<>();
        attributs = new ArrayList<>();
        isInterface = false;
        isAbstract = false;
        largeur = 200;
        longueur = 30;
    }

    public String getNom() {
        return nom;
    }
    public List<Methode> getMethodes() {
        return methodes;
    }
    public void setMethodes(List<Methode> methodes) {
        this.methodes = methodes;
    }

    public List<Attribut> getAttributs() {
        return attributs;
    }
    public void setAttributs(List<Attribut> attributs) {
        this.attributs = attributs;
    }

    public boolean isInterface() {
        return isInterface;
    }
    public void setInterface(boolean isInterface) {
        this.isInterface = isInterface;
    }

    public boolean isAbstract() {
        return isAbstract;
    }
    public void setAbstract(boolean isAbstract) {
        this.isAbstract = isAbstract;
    }

    public String getNomPackage() {
        return nomPackage;
    }
    public void setNomPackage(String nomPackage) {
        this.nomPackage = nomPackage;
    }

    public double getX() {
        return x;
    }
    public double getY() {
        return y;
    }
    public double getLongueur() {
        return longueur;
    }
    public double getLargeur() {
        return largeur;
    }

    public void setLongueur(double longueur) {
        this.longueur = longueur;
    }

    public void setLargeur(double largeur) {
        this.largeur = largeur;
    }

    public void setX(double x) {
        this.x = x;
    }
    public void setY(double y) {
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Classe classe)) return false;
        // si meme nom et meme package
        return getNom().equals(classe.getNom()) && getNomPackage().equals(classe.getNomPackage());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getNom(), getMethodes(), getAttributs(), isInterface());
    }

    public String toString(){
        return this.nomPackage + "." + this.nom + ":" + this.x + ":" + this.y + "\n";
    }
}
