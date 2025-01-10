package projet.classes;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

public class Fleche implements Serializable {

    public static final String IMPLEMENTS = ".u.|>";
    public static final String EXTENDS = "-u-|>";
    public static final String DEPENDANCE = "-->";

    private static final HashMap<String,Integer> nbRelations = new HashMap<>();

    private final Classe parent;
    private final Classe enfant;
    private final String type;
    private String nom;

    private String parentCardinalite;
    private String enfantCardinalite;

    private final int indexEnfantParent;

    private double[] p1, p2;

    public Fleche(Classe parent, Classe enfant, String type) {
        this.parent = parent;
        this.enfant = enfant;
        this.type = type;
        this.nom = "\"\"";
        this.parentCardinalite = null;
        this.enfantCardinalite = null;

        String nomParent = parent.getRealName();
        String nomEnfant = enfant.getRealName();
        String keyEnfantParent = nomParent+nomEnfant;
        if (nbRelations.containsKey(keyEnfantParent)) {
            nbRelations.put(keyEnfantParent, nbRelations.get(keyEnfantParent)+1);
        } else {
            nbRelations.put(keyEnfantParent, 0);
        }

        this.indexEnfantParent = nbRelations.get(keyEnfantParent);

        calculerPosition();
    }

    private void calculerPosition() {
        // Coins et dimensions des rectangles
        double x1Min = enfant.getX();
        double y1Min = enfant.getY();
        double x1Max = x1Min + enfant.getLargeur();
        double y1Max = y1Min + enfant.getLongueur();

        double x2Min = parent.getX();
        double y2Min = parent.getY();
        double x2Max = x2Min + parent.getLargeur();
        double y2Max = y2Min + parent.getLongueur();

        // Identifier les relations spatiales
        boolean parentAuDessus = y1Max < y2Min; // Parent au-dessus de l'enfant
        boolean parentEnDessous = y1Min > y2Max; // Parent en dessous de l'enfant
        boolean parentAGauche = x1Max < x2Min; // Parent à gauche de l'enfant
        boolean parentADroite = x1Min > x2Max; // Parent à droite de l'enfant

        // Centre des rectangles
        double[] centre1, centre2;
        if ((parentADroite||parentAGauche) && !parentAuDessus && !parentEnDessous) {
            if (y1Max < y2Max){
                centre1 = new double[]{(x1Min + x1Max) / 2, (y1Min + y1Max) / 2 - 10 * indexEnfantParent};
                centre2 = new double[]{(x2Min + x2Max) / 2, (y2Min + y2Max) / 2 - 10 * indexEnfantParent};
            }
            else{
                centre1 = new double[]{(x1Min + x1Max) / 2, (y1Min + y1Max) / 2 + 10 * indexEnfantParent};
                centre2 = new double[]{(x2Min + x2Max) / 2, (y2Min + y2Max) / 2 + 10 * indexEnfantParent};
            }
        }
        else {
            if (parentAGauche || x1Max<x2Max){
                centre1 = new double[]{(x1Min + x1Max) / 2 - 10 * indexEnfantParent, (y1Min + y1Max) / 2};
                centre2 = new double[]{(x2Min + x2Max) / 2 - 10 * indexEnfantParent, (y2Min + y2Max) / 2};
            }
            else{
                centre1 = new double[]{(x1Min + x1Max) / 2 + 10 * indexEnfantParent, (y1Min + y1Max) / 2};
                centre2 = new double[]{(x2Min + x2Max) / 2 + 10 * indexEnfantParent, (y2Min + y2Max) / 2};
            }
        }

        if (parentAGauche && !parentAuDessus && !parentEnDessous) {
            // Cas : Parent à gauche
            p1 = new double[]{x1Max, centre1[1]};
            p2 = new double[]{x2Min, centre2[1]};
        } else if (parentADroite && !parentAuDessus && !parentEnDessous) {
            // Cas : Parent à droite
            p1 = new double[]{x1Min, centre1[1]};
            p2 = new double[]{x2Max, centre2[1]};
        } else if (parentAuDessus) {
            // Cas : Parent au-dessus
            p1 = new double[]{centre1[0], y1Max};
            p2 = new double[]{centre2[0], y2Min};
        } else if (parentEnDessous) {
            // Cas : Parent en dessous
            p1 = new double[]{centre1[0], y1Min};
            p2 = new double[]{centre2[0], y2Max};
        } else {
            // Chevauchement : Choisir le chemin minimal (priorité verticale)
            double deltaX = Math.abs(centre1[0] - centre2[0]);
            double deltaY = Math.abs(centre1[1] - centre2[1]);
            if (deltaX > deltaY) {
                // Priorité horizontale
                if (centre1[0] < centre2[0]) {
                    p1 = new double[]{x1Max, centre1[1]};
                    p2 = new double[]{x2Min, centre2[1]};
                } else {
                    p1 = new double[]{x1Min, centre1[1]};
                    p2 = new double[]{x2Max, centre2[1]};
                }
            } else {
                // Priorité verticale
                if (centre1[1] < centre2[1]) {
                    p1 = new double[]{centre1[0], y1Max};
                    p2 = new double[]{centre2[0], y2Min};
                } else {
                    p1 = new double[]{centre1[0], y1Min};
                    p2 = new double[]{centre2[0], y2Max};
                }
            }
        }
    }

    public double[] getPosition(){
        calculerPosition();
        return new double[]{p1[0], p1[1], p2[0], p2[1]};
    }

    public Classe getParent() {
        return parent;
    }

    public Classe getEnfant() {
        return enfant;
    }

    public String getType() {
        return type;
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

    public static void retirer1Relation(String key){
        if (nbRelations.containsKey(key)) {
            nbRelations.put(key, nbRelations.get(key)-1);
        }
    }

    public static void reinitialiserNbRelations(){
        nbRelations.clear();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Fleche fleche)) return false;
        return Objects.equals(getParent(), fleche.getParent()) && Objects.equals(getEnfant(), fleche.getEnfant()) && Objects.equals(getType(), fleche.getType()) && Objects.equals(getNom(), fleche.getNom());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getParent(), getEnfant(), getType());
    }

    @Override
    public String toString() {
        return "Fleche{" +
                "parent=" + parent +
                ", enfant=" + enfant +
                ", type='" + type + '\'' +
                ", nom='" + nom + '\'' +
                ", parentCardinalite='" + parentCardinalite + '\'' +
                ", enfantCardinalite='" + enfantCardinalite + '\'' +
                ", indexEnfantParent=" + indexEnfantParent +
                ", p1=" + Arrays.toString(p1) +
                ", p2=" + Arrays.toString(p2) +
                '}';
    }
}
