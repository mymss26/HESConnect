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
            if(e.getThematique().contains("droit") || e.getThematique().contains("num�rique") || e.getThematique().contains("entreprenariat")){
                bdd.run("MATCH");
            }
            else if(e.getThematique().contains("�thique")){
                bdd.run("hets");
            }
            else if (e.getThematique().contains("sant�")) {
                bdd.run("heds");
            }
            else if (e.getThematique().contains("musique")) {
                bdd.run("head");
            }
        }



    }



}
