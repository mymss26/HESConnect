package domaine;

public class Prof extends Personne{

    public Prof(String nom, String prenom, String mail, String genre, int codePostal) {
        super(nom, prenom, mail, genre, codePostal);
    }

    @Override
    public String toString() {
        return "Prof: "+ super.toString();
    }
}
