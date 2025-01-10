package projet.vues;

import projet.Modele;
import projet.Sujet;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class VueUML implements Observateur {

    public VueUML(Modele m) {
        actualiser(m);
    }

    public void actualiser(Sujet s){
        Modele m = (Modele) s;
        String UML = m.getUML();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("Diag.puml"))) {
            writer.write(UML);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
