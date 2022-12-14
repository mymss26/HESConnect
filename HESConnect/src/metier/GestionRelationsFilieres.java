package metier;

import dao.Bdd;
import dao.Data;
import domaine.Filiere;
import domaine.Personne;
import org.neo4j.driver.Session;

import java.util.List;

public class GestionRelationsFilieres {

    public static void loadRelationFiliere(Session bdd){
        relationFiliereEtHES(bdd);
    }


    public static void relationFiliereEtHES(Session bdd) {
        List<String[]> filiere = Data.listeFiliere();
        try{
            for (String[] f : filiere) {
                String nomFiliere = f[1];
                switch (f[0]){
                    case "HEG":bdd.run("MATCH (fi:FILIERE), (hes:HES) WHERE hes.nom='HEG' AND fi.nom='"+nomFiliere+"' CREATE (fi) -[:APPARTIENT]-> (hes)");break;
                    case "HEAD":bdd.run("MATCH (fi:FILIERE), (hes:HES) WHERE hes.nom='HEAD' AND fi.nom='"+nomFiliere+"' CREATE (fi) -[:APPARTIENT]-> (hes)");break;
                    case "HEDS":bdd.run("MATCH (fi:FILIERE), (hes:HES) WHERE hes.nom='HEDS' AND fi.nom='"+nomFiliere+"' CREATE (fi) -[:APPARTIENT]-> (hes)");break;
                    case "HETS":bdd.run("MATCH (fi:FILIERE), (hes:HES) WHERE hes.nom='HETS' AND fi.nom='"+nomFiliere+"' CREATE (fi) -[:APPARTIENT]-> (hes)");break;
                }
            }
        }catch (Exception e){
            System.out.println("Liste vide");
        }


    }
}


