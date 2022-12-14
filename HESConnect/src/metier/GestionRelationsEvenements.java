package metier;

import dao.Bdd;
import dao.Data;
import domaine.Evenement;
import domaine.HES;
import org.neo4j.driver.Session;

public class GestionRelationsEvenements {

    public static void relationHESetEvenement(Session bdd){

        for(Evenement e : Data.listeEvenement()){
            if(e.getThematique().contains("droit") || e.getThematique().contains("numérique") || e.getThematique().contains("entreprenariat")){
                bdd.run("MATCH (hes:HES), (evt:EVENEMENT) WHERE hes.nom='HEG' AND evt.nom='"+e.getNomEvenement()+"' CREATE (hes) -[:PROPOSE]-> (evt)");
            }
            else if(e.getThematique().contains("éthique")){
                bdd.run("MATCH (hes:HES), (evt:EVENEMENT) WHERE hes.nom='HETS' AND evt.nom='"+e.getNomEvenement()+"' CREATE (hes) -[:PROPOSE]-> (evt)");
            }
            else if (e.getThematique().contains("santé")) {
                bdd.run("MATCH (hes:HES), (evt:EVENEMENT) WHERE hes.nom='HEDS' AND evt.nom='"+e.getNomEvenement()+"' CREATE (hes) -[:PROPOSE]-> (evt)");
            }
            else if (e.getThematique().contains("musique")) {
                bdd.run("MATCH (hes:HES), (evt:EVENEMENT) WHERE hes.nom='HEAD' AND evt.nom='"+e.getNomEvenement()+"' CREATE (hes) -[:PROPOSE]-> (evt)");
            }
        }



    }



}
