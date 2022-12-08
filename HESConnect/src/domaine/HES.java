package domaine;

import java.util.Objects;

public abstract class HES {


    private String nom;
    private String adresse;

    public HES(String nom, String adresse) {
        this.nom = nom;
        this.adresse = adresse;
    }

    public String getNom() {
        return nom;
    }

    public String getAdresse() {
        return adresse;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HES hes = (HES) o;
        return Objects.equals(nom, hes.nom) && Objects.equals(adresse, hes.adresse);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nom, adresse);
    }

    @Override
    public String toString() {
        return  "nom : " + nom +", adresse : " + adresse ;
    }
}
