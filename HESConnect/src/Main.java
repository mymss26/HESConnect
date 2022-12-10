import dao.Bdd;
import dao.Data;
import domaine.HEG;
import domaine.HES;
import domaine.Personne;
import metier.Applic;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        new Applic();

        List<Personne> testPersonnes = Bdd.getListeEtudiant();
        for(Personne p : testPersonnes){
            if(p.getMail().contains("@heg.ch")) {
                //System.out.println(p);
            }
        }


        List<HES> testEcole = Bdd.getListeEcoles();
        for(HES e : testEcole){
            //System.out.println(e);
        }


    }
}