package domaine;

public class Competence {
    String nom;

    public Competence(String nom) {
        this.nom = nom;
    }

    @Override
    public String toString() {
        return "Competence '" + nom;
    }

    public String getNom() {
        return nom;
    }




}
