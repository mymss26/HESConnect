package domaine;

public abstract class Personne {
    String nom,prenom,mail,genre;
    int codePostal;

    public Personne(String nom, String prenom, String mail, String genre/*, int codePostal*/) {
        this.nom = nom;
        this.prenom = prenom;
        this.mail = mail;
        this.genre = genre;
        //this.codePostal = codePostal;
    }

    public String getNom() {
        return nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public String getMail() {
        return mail;
    }

    public String getGenre() {
        return genre;
    }

    public int getCodePostal() {
        return codePostal;
    }

    @Override
    public String toString() {
        return "nom: " + nom +", prenom:" + prenom +", mail:" + mail +", genre:" + genre +", code postal :"+codePostal;
    }
}
