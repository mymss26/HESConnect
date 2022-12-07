package domaine;

public abstract class Personne {
    String nom,prenom,mail,genre;

    public Personne(String nom, String prenom, String mail, String genre) {
        this.nom = nom;
        this.prenom = prenom;
        this.mail = mail;
        this.genre = genre;
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

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    @Override
    public String toString() {
        return "nom" + nom +", prenom:" + prenom +", mail:" + mail +", genre:" + genre;
    }
}
