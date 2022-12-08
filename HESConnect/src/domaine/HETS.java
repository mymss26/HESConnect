package domaine;

public class HETS extends HES{


    public HETS(String nom, String adresse) {
        super(nom, adresse);
    }

    @Override
    public String toString() {
        return "HETS "+super.toString();
    }
}
