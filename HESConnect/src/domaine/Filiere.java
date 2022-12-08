package domaine;

public class Filiere {

    private String nom;

    public Filiere(String nom) {
        this.nom = nom;
    }


    @Override
    public String toString() {
        return "Filiere :"+nom;
    }
}
