package domaine;

import java.util.Arrays;

public class Evenement {
    String nomEvenement;
    String[] thematique;

    public Evenement(String nomEvenement, String[] thematique) {
        this.nomEvenement = nomEvenement;
        this.thematique = thematique;
    }

    public String getNomEvenement() {
        return nomEvenement;
    }

    public String[] getThematique() {
        return thematique;
    }

    @Override
    public String toString() {
        return "Evenement:" + nomEvenement + ", thematique :" + Arrays.toString(thematique) ;
    }
}
