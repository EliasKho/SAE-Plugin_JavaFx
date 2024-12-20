package projet;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class VueUML implements Observateur{
    private Modele modele;

    public VueUML(Modele m) {
        this.modele = m;
        actualiser(m);
    }

    public void actualiser(Sujet s){
        Modele m = (Modele) s;
        String UML = m.getUML();
        //System.out.println(UML);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("Diag.puml"))) {
            writer.write(UML);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
