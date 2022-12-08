package dao;

import domaine.*;
import metier.FabriquePersonne;
import metier.FabriquePersonnes;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Session;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class Bdd {      // connection ä la bdd

    List<Personne> listeEtudiant = new ArrayList<>();
    List<Personne> listeAssitant = new ArrayList<>();
    List<Personne> listeProfesseur = new ArrayList<>();



    public static Session connect_db(){
            return GraphDatabase.driver("bolt://localhost:7687").session();
    }

    public static void setup_env(Session bdd) {
        delete_all(bdd);
    }
    public static void delete_all(Session bdd){
        bdd.run("match (a) -[r] -> () delete a, r");
        bdd.run("match (a) delete a");
    }

    public static void chargerDataPersonne(Session bdd){
        FabriquePersonne fabrique = new FabriquePersonnes();
        List<String[]> personnes = Data.listePersonnes();
       for(String[] data : personnes){
           Personne p = null;
           p = fabrique.nouvellePersonne(data);
           bdd.run("CREATE (:"+data[4]+"{nom:'"+p.getNom()+"',prenom:'"+p.getPrenom()+"',mail:'"+p.getMail()+"',genre:'"+p.getGenre()+"'})");
       }
    }


    /**TESTTTTTT**/
    public static List<Personne> chargerDataPersonneTEST(Session bdd){
        FabriquePersonne fabrique = new FabriquePersonnes();
        List<String[]> personnes = Data.listePersonnes();
        List<Personne> listeEtu = new ArrayList<>();
        List<Personne> listeProf = new ArrayList<>();
        List<Personne> listeAssitant = new ArrayList<>();
        for(String[] data : personnes){
            Personne p = null;
            p = fabrique.nouvellePersonne(data);
            bdd.run("CREATE (:"+data[4]+"{nom:'"+p.getNom()+"',prenom:'"+p.getPrenom()+"',mail:'"+p.getMail()+"',genre:'"+p.getGenre()+"'})");
            switch (data[4]){
                case "ASSISTANT":

                    break;
                case "ETUDIANT":
                    getListeEtudiant(p);
                    break;
                case "PROFESSEUR":
                    getListeProf(p);
                    break;
            }
        }
        return null;
    }


    public static List<Personne> getListeEtudiant(Personne personne){
        FabriquePersonne fabrique = new FabriquePersonnes();
        List<String[]> personnes = Data.listePersonnes();

        List<Personne> listeEtudiant = new ArrayList<>();
        listeEtudiant.add(personne);
        return listeEtudiant;
    }

    public static List<Personne> getListeProf(Personne personne){
        List<Personne> listeProf = new ArrayList<>();
        listeProf.add(personne);
        return listeProf;
    }

    public static List<Personne> getListeAssistant(Personne personne){
        List<Personne> listeAssistant = new ArrayList<>();
        listeAssistant.add(personne);
        return listeAssistant;
    }



    public static void chargerDataEvenement(Session bdd){
        List<Evenement> evenement = Data.listeEvenement();
        for(Evenement data : evenement){
            bdd.run("CREATE(:Evenement{titre:'"+data.getNomEvenement()+"',thematique:'"+data.getThematique()+"'})");
        }
    }

   /*public static void chargerDataHES(Session bdd){
            bdd.run("CREATE(:HES{nom:'"+new HES(null, null)+"'})");
    }*/
}
