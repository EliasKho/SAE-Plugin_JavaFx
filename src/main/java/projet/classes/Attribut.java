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

    public String getStringCode() {
        String affichage = getModifierString();
        return affichage+type+" "+nom+";";
    }

    public String getModifierUMLString(){
        String affichage="";
        switch (modifier) {
            case 1:
                affichage += "+";
                break;
            case 2:
                affichage += "-";
                break;
            case 4:
                affichage += "#";
                break;
            case 8, 9:
                affichage += "+{static} ";
                break;
            case 16, 17:
                affichage += "+{final} ";
                break;
            case 1024, 1025:
                affichage += "+{abstract} ";
                break;
            case 25:
                affichage += "+{static final} ";
                break;
            case 10:
                affichage += "-{static} ";
                break;
            case 18:
                affichage += "-{final} ";
                break;
            case 26:
                affichage += "-{static final} ";
                break;
            case 1026:
                affichage += "-{abstract} ";
                break;
            case 12:
                affichage += "# {static} ";
                break;
            case 20:
                affichage += "# {final} ";
                break;
            case 28:
                affichage += "# {static final} ";
                break;
            case 1028:
                affichage += "# {abstract} ";
                break;
            case 1033:
                affichage += "+{static abstract} ";
                break;
            case 1041:
                affichage += "+{final abstract} ";
                break;
            case 1049:
                affichage += "+{static final abstract} ";
                break;
            case 1050:
                affichage += "-{static final abstract} ";
                break;
            case 1052:
                affichage += "# {static final abstract} ";
                break;
            default:
                // Optionnel : gérer les cas non définis
        }

        return affichage;
    }

    public String getModifierString(){
        String affichage="";
        switch (modifier) {
            case 1:
                affichage += "public ";
                break;
            case 2:
                affichage += "private ";
                break;
            case 4:
                affichage += "protected ";
                break;
            case 8:
                affichage += "static ";
                break;
            case 16:
                affichage += "final ";
                break;
            case 1024:
                affichage += "abstract ";
                break;
            case 9:
                affichage += "public static ";
                break;
            case 17:
                affichage += "public final ";
                break;
            case 25:
                affichage += "public static final ";
                break;
            case 1025:
                affichage += "public abstract ";
                break;
            case 10:
                affichage += "private static ";
                break;
            case 18:
                affichage += "private final ";
                break;
            case 26:
                affichage += "private static final ";
                break;
            case 1026:
                affichage += "private abstract ";
                break;
            case 12:
                affichage += "protected static ";
                break;
            case 20:
                affichage += "protected final ";
                break;
            case 28:
                affichage += "protected static final ";
                break;
            case 1028:
                affichage += "protected abstract ";
                break;
            case 1033:
                affichage += "public static abstract ";
                break;
            case 1041:
                affichage += "public final abstract ";
                break;
            case 1049:
                affichage += "public static final abstract ";
                break;
            case 1050:
                affichage += "private static final abstract ";
                break;
            case 1052:
                affichage += "protected static final abstract ";
                break;
            default:
                // Optionnel : gérer les cas non définis
                // affichage += "Modificateur non reconnu";
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
