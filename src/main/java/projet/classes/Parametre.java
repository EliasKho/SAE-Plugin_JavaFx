package projet.classes;

import java.io.Serializable;

public class Parametre implements Serializable {
    private String type;

    public Parametre(String type) {
        this.type = type.substring(type.lastIndexOf(".")+1);
    }

    public String getType() {
        return type;
    }
}
