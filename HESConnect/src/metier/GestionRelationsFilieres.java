package metier;

import dao.Bdd;
import dao.Data;
import domaine.Competence;
import domaine.Filiere;
import domaine.Personne;
import org.neo4j.driver.Session;

import java.util.ArrayList;
import java.util.List;

public class GestionRelationsFilieres {

    public static void loadRelationFiliere(Session bdd) {
        relationFiliereEtHES(bdd);
        relationFiliereEtCompetences(bdd); }

    public static void relationFiliereEtHES(Session bdd) {
        List<String[]> filiere = Data.listeFiliere();
        assert filiere != null;
        for (String[] f : filiere) {
            String nomEcole = f[0];
            String nomFiliere = f[1];
            switch (f[0]) {
                case "HEG" -> bdd.run("MATCH (fi:FILIERE), (hes:HES)  WHERE hes.nom='HEG' AND fi.nom='" + nomFiliere + "' CREATE (fi) -[:APPARTIENT]-> (hes)");
                case "HEAD" -> bdd.run("MATCH (fi:FILIERE), (hes:HES) WHERE hes.nom='HEAD' AND fi.nom='" + nomFiliere + "' CREATE (fi) -[:APPARTIENT]-> (hes)");
                case "HEDS" -> bdd.run("MATCH (fi:FILIERE), (hes:HES) WHERE hes.nom='HEDS' AND fi.nom='" + nomFiliere + "' CREATE (fi) -[:APPARTIENT]-> (hes)");
                case "HETS" -> bdd.run("MATCH (fi:FILIERE), (hes:HES) WHERE hes.nom='HETS' AND fi.nom='" + nomFiliere + "' CREATE (fi) -[:APPARTIENT]-> (hes)");}
        }
    }
    public static void relationFiliereEtCompetences(Session bdd) {
        String[] Comp_IG = {"Gestion de projet","Communication","Programmation","Economie"};
        String[] Comp_IBM = {"Gestion de projet","Communication","Economie"};
        String[] Comp_EE = {"Gestion de projet","Communication","Economie"};
        String[] Comp_AV = {"Gestion de projet","Communication","Art numérique","Design objet"};
        String[] Comp_AI = {"Gestion de projet","Communication","Fondamentaux techniques","Economie"};
        String[] Comp_SF = {"Obstétrique","Communication","Anatomie"};
        String[] Comp_ND = {"Gestion de projet","Communication","Nutrition et Diététique","Anatomie;"};

        List<Competence> competences = Data.listeCompetences();
        for (Competence comp : competences){
            if(comp.getNom().contains(Comp_IG[0])||comp.getNom().contains(Comp_IG[1])||comp.getNom().contains(Comp_IG[2])||comp.getNom().contains(Comp_IG[3])){
                bdd.run("MATCH (fi:FILIERE),(com:COMPETENCE) WHERE fi.nom='Informatique de Gestion' AND com.nom='"+comp.getNom()+"'CREATE (fi)-[:DISPENSE]->(com)");
                bdd.run("MATCH (fi:FILIERE),(com:COMPETENCE) WHERE fi.nom='Informatique de Gestion' AND com.nom='"+comp.getNom()+"' CREATE (com)-[:DISPENSE_EN]->(fi)");
            }
           for(String ibm:Comp_IBM){
               if(comp.getNom().contains(ibm)){
                   bdd.run("MATCH (fi:FILIERE),(com:COMPETENCE) WHERE fi.nom='International Business Management' AND com.nom='"+comp.getNom()+"'CREATE (fi)-[:DISPENSE]->(com)");
                   bdd.run("MATCH (fi:FILIERE),(com:COMPETENCE) WHERE fi.nom='International Business Management' AND com.nom='"+comp.getNom()+"' CREATE (com)-[:DISPENSE_EN]->(fi)");
               }
           }
            for(String ibm:Comp_EE){
                if(comp.getNom().contains(ibm)){
                    bdd.run("MATCH (fi:FILIERE),(com:COMPETENCE) WHERE fi.nom='Economie d Entreprise' AND com.nom='"+comp.getNom()+"'CREATE (fi)-[:DISPENSE]->(com)");
                    bdd.run("MATCH (fi:FILIERE),(com:COMPETENCE) WHERE fi.nom='Economie d Entreprise' AND com.nom='"+comp.getNom()+"' CREATE (com)-[:DISPENSE_EN]->(fi)");
                }
            }
            for(String ibm:Comp_AV){
                if(comp.getNom().contains(ibm)){
                    bdd.run("MATCH (fi:FILIERE),(com:COMPETENCE) WHERE fi.nom='Art Visuel' AND com.nom='"+comp.getNom()+"'CREATE (fi)-[:DISPENSE]->(com)");
                    bdd.run("MATCH (fi:FILIERE),(com:COMPETENCE) WHERE fi.nom='Art Visuel' AND com.nom='"+comp.getNom()+"' CREATE (com)-[:DISPENSE_EN]->(fi)");
                }
            }
        }

    }


}


