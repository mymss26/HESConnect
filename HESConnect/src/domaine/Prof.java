package domaine;

public class Prof extends Personne{
    public Prof(String nom, String prenom, String mail, String genre) {
        super(nom, prenom, mail, genre);
    }

    @Override
    public String toString() {
        return "Prof: "+ super.toString();
    }
}
