package projet.classes;

import java.io.Serializable;

public class Parametre implements Serializable {
    private String type;

    public Parametre(String type) {
        System.out.println("Parametre : "+type);
        this.type = type.substring(type.lastIndexOf(".")+1);
        System.out.println("Parametre : "+this.type);
    }

    public String getType() {
        return type;
    }
}
