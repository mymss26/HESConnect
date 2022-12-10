package domaine;

public class Assistant extends Personne{
    public Assistant(String nom, String prenom, String mail, String genre) {
        super(nom, prenom, mail, genre);
    }

    @Override
    public String toString() {
        return "Assistant : "+super.toString();
    }
}
