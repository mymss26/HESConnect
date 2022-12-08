package domaine;

public class HEAD extends HES{


    public HEAD(String nom, String adresse) {
        super(nom, adresse);
    }

    @Override
    public String toString() {
        return "HEAD "+super.toString();
    }
}
