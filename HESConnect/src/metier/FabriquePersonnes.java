package metier;

import domaine.Assistant;
import domaine.Etudiant;
import domaine.Personne;
import domaine.Prof;

import java.util.ArrayList;
import java.util.List;

public class FabriquePersonnes extends FabriquePersonne{
    @Override
    Personne newPersonne(String[] data) {
        Personne p = null;
        String[] getDomaine = data[2].split("@");

        switch (getDomaine[1]){
            case "ass.hes.ch":  p = new Assistant(data[0],data[1],data[2],data[3],Integer.parseInt(data[4]));  break;
            case "heg.ch":   p = new Etudiant(data[0],data[1],data[2],data[3],Integer.parseInt(data[4]));  break;
            case "head.ch":   p = new Etudiant(data[0],data[1],data[2],data[3],Integer.parseInt(data[4]));  break;
            case "heds.ch":   p = new Etudiant(data[0],data[1],data[2],data[3],Integer.parseInt(data[4]));  break;
            case "hets.ch":   p = new Etudiant(data[0],data[1],data[2],data[3],Integer.parseInt(data[4]));  break;
            case "prof.hes.ch": p = new Prof(data[0],data[1],data[2],data[3],Integer.parseInt(data[4]));  break;
        }
        return p;
    }

}
