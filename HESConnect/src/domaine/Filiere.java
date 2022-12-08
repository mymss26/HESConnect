package domaine;

import java.util.List;

public class Filiere {

    private String nom;
    private List<String> competences; //TODO à check

    public Filiere(String nom) {
        this.nom = nom;
    }


    @Override
    public String toString() {
        return "Filiere :"+nom;
    }
}
