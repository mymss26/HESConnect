package domaine;

import java.util.List;

public class Filiere {

    private String nom;

    public Filiere(String nom) {
        this.nom = nom;
    }

    public String getNom() {
        return nom;
    }


    @Override
    public String toString() {
        return "Filiere :"+nom;
    }
}
