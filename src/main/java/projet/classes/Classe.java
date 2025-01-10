package projet.classes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import projet.Modele;

public class Classe implements Serializable{
    private final String nom;
    private List<Methode> methodes;
    private List<Attribut> attributsHerites;
    private List<Attribut> attributsNonHerites;
    private boolean isInterface;
    private boolean isAbstract;
    private String nomPackage;
    private double x;
    private double y;
    private double longueur;
    private double largeur;
    private String absolutePath;
    private ArrayList<String> classesExternes;

    public Classe(String nom){
        this.nom = nom.substring(nom.lastIndexOf(".")+1);
        if (nom.contains(".")) {
            this.nomPackage = nom.substring(0, nom.lastIndexOf("."));
        }
        else {
            this.nomPackage = "";
        }
        methodes = new ArrayList<>();
        attributsHerites = new ArrayList<>();
        attributsNonHerites = new ArrayList<>();
        classesExternes = new ArrayList<>();
        isInterface = false;
        isAbstract = false;
        largeur = 200;
        longueur = 30;
    }

    public void ajouterMethode(Methode methode){
        methodes.add(methode);
    }

    public void ajouterClasseExterne(String classe){
        if (!classesExternes.contains(classe)) {
            classesExternes.add(classe);
        }
    }

    public ArrayList<String> getClassesExternes(){
        return classesExternes;
    }

    public String getAbsolutePath() {
        return absolutePath;
    }

    public void setAbsolutePath(String absolutePath) {
        this.absolutePath = absolutePath;
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
        if(Modele.isVoirAttributsHerites()){
            return attributsHerites;
        }
        return attributsNonHerites;
    }
    public void setHeritesAttributs(List<Attribut> attributs) {
        this.attributsHerites = attributs;
    }
    public void setNonHeritesAttributs(List<Attribut> attributs) {
        this.attributsNonHerites = attributs;
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

    public String getRealName(){
        if (this.nomPackage.isEmpty()){
            return this.nom;
        }
        return this.nomPackage + "." + this.nom;
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
