package domaine;

public class Etudiant extends Personne{
    public Etudiant(String nom, String prenom, String mail, String genre) {
        super(nom, prenom, mail, genre);
    }

    @Override
    public String toString() {
        return "Etudiant :"+super.toString();
    }
}
