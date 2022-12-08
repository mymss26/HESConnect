package domaine;

public class HEG extends HES{


    public HEG(String nom, String adresse) {
        super(nom, adresse);
    }

    @Override
    public String toString() {
        return "HEG "+super.toString();
    }
}
