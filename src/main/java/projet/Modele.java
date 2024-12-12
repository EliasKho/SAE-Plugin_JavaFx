package projet;

import java.util.ArrayList;
import java.util.List;

public class Modele implements Sujet{

    private String path;
    private List<Observateur> observateurs;

    Modele(String path) {
        this.path = path;
        this.observateurs = new ArrayList<Observateur>();
    }

    public String getPath() {
        return path;
    }

    public void notifierObservateur(){
        for(Observateur observateur : observateurs){
            observateur.actualiser(this);
        }
    }

    public void enregistrerObservateur(Observateur observateur){
        observateurs.add(observateur);
    }

    public void supprimerObservateur(Observateur observateur){
        observateurs.remove(observateur);
    }

}
