package projet;

import projet.vues.Observateur;

public interface Sujet {
    void enregistrerObservateur(Observateur o);
    void supprimerObservateur(Observateur o);
    void notifierObservateur();
}
