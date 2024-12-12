package projet;

import java.util.ArrayList;
import java.util.List;

public class Modele {

    private String path;
    private List<Observateur> observateurs;

    Modele(String path) {
        this.path = path;
        this.observateurs = new ArrayList<Observateur>();
    }

    public String getPath() {
        return path;
    }

    public void notifierObservateurs(){
        for(Observateur observateur : observateurs){
            observateur.actualiser();
        }
    }

    public void enregistrerObservateur(Observateur observateur){
        observateurs.add(observateur);
    }

    public void supprimerObservateur(Observateur observateur){
        observateurs.remove(observateur);
    }

}
