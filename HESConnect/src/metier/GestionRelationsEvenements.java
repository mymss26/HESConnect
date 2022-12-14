package metier;

import dao.Bdd;
import dao.Data;
import domaine.Evenement;
import domaine.HES;
import org.neo4j.driver.Session;

public class GestionRelationsEvenements {

    public static void relationHESetEvenement(Session bdd){

        //Boucler sur toutes les hes
        for(HES hes : Bdd.getListeEcoles()){
        }

        for(Evenement e : Data.listeEvenement()){
            if(e.getThematique().contains("droit") || e.getThematique().contains("numérique") || e.getThematique().contains("entreprenariat")){
                bdd.run("MATCH");
            }
            else if(e.getThematique().contains("éthique")){
                bdd.run("hets");
            }
            else if (e.getThematique().contains("santé")) {
                bdd.run("heds");
            }
            else if (e.getThematique().contains("musique")) {
                bdd.run("head");
            }
        }



    }



}
