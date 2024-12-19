package projet.classes;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Classe {
    private String nom;
    private List<Methode> methodes;
    private List<Attribut> attributs;
    private boolean isInterface;
    private boolean isAbstract;
    private String nomPackage;
    private double x;
    private double y;
//    private List<String> parents;

    private Classe(){
        nom = "";
        methodes = new ArrayList<>();
        attributs = new ArrayList<>();
        isInterface = false;
        isAbstract = false;
    }

    public Classe(String nom){
        this();
        this.nom = nom.substring(nom.lastIndexOf(".")+1);
    }

    public String getNom() {
        return nom;
    }
    public void setNom(String nom) {
        this.nom = nom;
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
        return isInterface() == classe.isInterface() && Objects.equals(getNom(), classe.getNom()) && Objects.equals(getMethodes(), classe.getMethodes()) && Objects.equals(getAttributs(), classe.getAttributs());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getNom(), getMethodes(), getAttributs(), isInterface());
    }
}
