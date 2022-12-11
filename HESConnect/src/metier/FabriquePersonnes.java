package metier;

import domaine.Assistant;
import domaine.Etudiant;
import domaine.Personne;
import domaine.Prof;

public class FabriquePersonnes extends FabriquePersonne{
    @Override
    Personne newPersonne(String[] data) {
        Personne p = null;
        switch (data[5].toUpperCase()){
            case "ASSISTANT":  p = new Assistant(data[0],data[1],data[2],data[3],Integer.parseInt(data[4]));  break;
            case "ETUDIANT":   p = new Etudiant(data[0],data[1],data[2],data[3],Integer.parseInt(data[4]));  break;
            case "PROFESSEUR": p = new Prof(data[0],data[1],data[2],data[3],Integer.parseInt(data[4]));  break;
        }
        return p;
    }
}
