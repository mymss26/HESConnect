package domaine;

import java.util.Arrays;
import java.util.List;

public class Evenement {
    String nomEvenement;
    List<String> thematique;

    public Evenement(String nomEvenement, List<String> thematique) {
        this.nomEvenement = nomEvenement;
        this.thematique = thematique;
    }

    public Evenement(String nomEvenement){
        this.nomEvenement = nomEvenement;
    }

    public String getNomEvenement() {
        return nomEvenement;
    }

    public List<String> getThematique() {
        return thematique;
    }

    @Override
    public String toString() {
        return "Evenement:" + nomEvenement + ", thematique :" + thematique ;
    }
}
