package domaine;

import org.neo4j.driver.Value;

public class Etudiant extends Personne{
    public Etudiant(String nom, String prenom, String mail, String genre, int codePostal) {
        super(nom, prenom, mail, genre, codePostal);
    }

    @Override
    public String toString() {
        return "Etudiant :"+super.toString();
    }
}
