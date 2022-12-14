package domaine;

import java.util.List;

public class Filiere {

    private String nom;
    private List<String> competences;

    public Filiere(String nom, List<String> competences) {
        this.nom = nom;
        this.competences = competences;
    }

    public Filiere(String nom){
        this.nom = nom;
    }


    public String getNom() {
        return nom;
    }

    public List<String> getCompetences() {
        return competences;
    }

    @Override
    public String toString() {
        return "Filiere :"+nom;
    }
}
