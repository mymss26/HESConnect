package domaine;

import java.util.Arrays;
import java.util.List;

public class Evenement {
    String nomEvenement;
    List<String> thematique;

    public Evenement(String nomEvenement, List<String> thematique) {  // !!!!AJOUTER LA VILLE OU LIEU DE L EVENEMENT
        this.nomEvenement = nomEvenement;
        this.thematique = thematique;
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
