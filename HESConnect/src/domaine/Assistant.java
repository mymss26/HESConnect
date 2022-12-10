package domaine;

public class Assistant extends Personne{


    public Assistant(String nom, String prenom, String mail, String genre, int codePostal) {
        super(nom, prenom, mail, genre, codePostal);
    }

    @Override
    public String toString() {
        return "Assistant : "+super.toString();
    }
}
