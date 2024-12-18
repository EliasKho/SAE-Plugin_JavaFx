package projet;

import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class VueIntrospection implements Observateur{
    private Modele modele;

    public VueIntrospection(Modele m) {
        this.modele = m;
        actualiser(m);
    }

    public void actualiser(Sujet s){
        Modele m = (Modele) s;
        String UML = m.getUML();
        System.out.println(UML);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("Diag.puml"))) {
            writer.write(UML);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
