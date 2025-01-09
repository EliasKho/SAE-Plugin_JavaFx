package projet.classes;

public class Parametre {
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
