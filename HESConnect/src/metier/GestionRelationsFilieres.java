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
        for (String[] f : filiere) {
            switch (f[0]){
                case "HEG":bdd.run("MATCH (fi:FILIERE), (hes:HEG) WHERE hes.nom='HEG'CREATE (fi) -[:APPARTIENT]-> (hes)");
                case "HEAD":bdd.run("MATCH (fi:FILIERE), (hes:HEAD) WHERE hes.nom='HEAD' CREATE (fi) -[:APPARTIENT]-> (hes)");
                case "HEDS":bdd.run("MATCH (fi:FILIERE), (hes:HEDS) WHERE hes.nom='HEDS' CREATE (fi) -[:APPARTIENT]-> (hes)");
                case "HETS":bdd.run("MATCH (fi:FILIERE), (hes:HETS) WHERE hes.nom='HETS' CREATE (fi) -[:APPARTIENT]-> (hes)");
            }

        }


    }
}


