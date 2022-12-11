package dao;

import domaine.*;
import metier.FabriquePersonne;
import metier.FabriquePersonnes;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Session;
import org.neo4j.driver.Value;

import java.util.*;

public class Bdd {      // connection à la bdd

    static List<Personne> listeEtudiants = new ArrayList<>();
    static List<Personne> listeProfesseurs = new ArrayList<>();
    static List<Personne> listeAssistants = new ArrayList<>();


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

           bdd.run("CREATE (:"+data[5]+"{nom:'"+p.getNom()+"',prenom:'"+p.getPrenom()+"',mail:'"+p.getMail()+"',genre:'"+p.getGenre()+"', codePostal:'"+p.getCodePostal()+"'})");
           switch (data[5]){
               case "ETUDIANT": listeEtudiants.add(p);break;
               case "ASSISTANT": listeAssistants.add(p);break;
               case "PROFESSEUR": listeProfesseurs.add(p);break;
           }
       }
    }

    public static List<Personne> getListeEtudiant(){
        return listeEtudiants;
    }

    public static List<Personne> getListeProfesseurs(){
        return listeProfesseurs;
    }

    public static List<Personne> getListeAssistant(){
        return listeAssistants;
    }





    public static void chargerDataEvenement(Session bdd){
        List<Evenement> evenement = Data.listeEvenement();
        for(Evenement data : evenement){
            bdd.run("CREATE(:Evenement{titre:'"+data.getNomEvenement()+"',thematique:'"+data.getThematique()+"'})");
        }
    }



   public static void chargerDataHES(Session bdd){
        for (HES e : getListeEcoles()){
            bdd.run("CREATE(:HES{nom:'"+e.getNom()+"', adresse:'"+e.getAdresse()+"'})");
        }
    }

    public static List<HES> getListeEcoles(){
        List<HES> listEcoles = new ArrayList<>();
        List nomEcoles = List.of("HEAD", "HEDS", "HEG", "HETS");
        List adresseEcoles = List.of("Av. de Chatelaine 5, 1203 Geneve", "Av. de Champel 47, 1206 Geneve", "Rue de la Tambourine 17, 1227 Carouge", "Rue Prevost-Martin 28, 1211 Geneve");

        for(int i = 0; i<nomEcoles.size(); i++){
            switch ((String) nomEcoles.get(i)){
                case "HEAD": HES head = new HEAD((String) nomEcoles.get(i), (String) adresseEcoles.get(i));
                    listEcoles.add(head);
                    break;
                case "HEDS": HES heds = new HEDS((String) nomEcoles.get(i), (String) adresseEcoles.get(i));
                    listEcoles.add(heds);
                    break;
                case "HEG": HES heg = new HEG((String) nomEcoles.get(i), (String) adresseEcoles.get(i));
                    listEcoles.add(heg);
                    break;
                case "HETS": HES hets = new HETS((String) nomEcoles.get(i), (String) adresseEcoles.get(i));
                    listEcoles.add(hets);
                    break;
            }
        }
        return listEcoles;
    }


    /**
     *
     * Faires les relations
     * Peut etre on peut mieux automatiser tout ca...
     *
     * */
    public static void relationPersonnesAvecEcoles(Session bdd){

        for(Personne p : listeEtudiants){
            if (p.getMail().contains("@heg.ch")){
                bdd.run("MATCH (etu:ETUDIANT), (hes:HES) WHERE etu.mail ='"+p.getMail()+"' AND hes.nom='HEG' CREATE (etu) -[:ETUDIE]-> (hes)");
            } else if (p.getMail().contains("@head.ch")) {
                bdd.run("MATCH (etu:ETUDIANT), (hes:HES) WHERE etu.mail ='"+p.getMail()+"' AND hes.nom='HEAD' CREATE (etu) -[:ETUDIE]-> (hes)");
            } else if (p.getMail().contains("@heds.ch")) {
                bdd.run("MATCH (etu:ETUDIANT), (hes:HES) WHERE etu.mail ='"+p.getMail()+"' AND hes.nom='HEDS' CREATE (etu) -[:ETUDIE]-> (hes)");
            } else if (p.getMail().contains("@hets.ch")) {
                bdd.run("MATCH (etu:ETUDIANT), (hes:HES) WHERE etu.mail ='"+p.getMail()+"' AND hes.nom='HETS' CREATE (etu) -[:ETUDIE]-> (hes)");
            }
        }


        List<Personne> listProfesseursHEG = new ArrayList<>();  //15 profs
        List<Personne> listProfesseursHEAD = new ArrayList<>(); //15 profs
        List<Personne> listProfesseursHEDS = new ArrayList<>(); //10 profs
        List<Personne> listProfesseursHETS = new ArrayList<>(); //5 profs


        int compteurProf = 0;
        for (Personne p : listeProfesseurs){
            if((listProfesseursHEG.isEmpty() || listProfesseursHEG.size() <= 15) && compteurProf<=14){
                listProfesseursHEG.add(p);
                compteurProf++;
            } else if ((listProfesseursHEAD.isEmpty() || listProfesseursHEAD.size() <= 15) && (compteurProf >= 15 && compteurProf <= 29)){
                listProfesseursHEAD.add(p);
                compteurProf++;
            }else if ((listProfesseursHEDS.isEmpty() || listProfesseursHEDS.size() <= 10) && (compteurProf >= 30 && compteurProf <= 39)) {
                listProfesseursHEDS.add(p);
                compteurProf++;
            } else if ((listProfesseursHETS.isEmpty() || listProfesseursHETS.size() <= 5) && (compteurProf >= 40 && compteurProf <= 45)){
                listProfesseursHETS.add(p);
                compteurProf++;
            }
        }

        for(Personne p : listProfesseursHEG){
            bdd.run("MATCH (prof:PROFESSEUR), (hes:HES) WHERE prof.mail='"+p.getMail()+"' AND hes.nom='HEG' CREATE (prof) -[:TRAVAILLE]-> (hes)");
        }
        for(Personne p : listProfesseursHEAD){
            bdd.run("MATCH (prof:PROFESSEUR), (hes:HES) WHERE prof.mail='"+p.getMail()+"' AND hes.nom='HEAD' CREATE (prof) -[:TRAVAILLE]-> (hes)");
        }
        for(Personne p : listProfesseursHEDS){
            bdd.run("MATCH (prof:PROFESSEUR), (hes:HES) WHERE prof.mail='"+p.getMail()+"' AND hes.nom='HEDS' CREATE (prof) -[:TRAVAILLE]-> (hes)");
        }
        for(Personne p : listProfesseursHETS){
            bdd.run("MATCH (prof:PROFESSEUR), (hes:HES) WHERE prof.mail='"+p.getMail()+"' AND hes.nom='HETS' CREATE (prof) -[:TRAVAILLE]-> (hes)");
        }


        List<Personne> listAssistantsHEG = new ArrayList<>();  //7 assistants pour 15 profs
        List<Personne> listAssistantsHEAD = new ArrayList<>(); //7 assistants pour 15 profs
        List<Personne> listAssistantsHEDS = new ArrayList<>(); //5 assistants pour 10 profs
        List<Personne> listAssistantsHETS = new ArrayList<>(); //3 assistants pour 5 profs

        int compteurAssistant = 0;
        for(Personne p : listeAssistants){
            if((listAssistantsHEG.isEmpty() || listAssistantsHEG.size() <= 7) && compteurAssistant <=6){
                listAssistantsHEG.add(p);
                compteurAssistant++;
            } else if ((listAssistantsHEAD.isEmpty() || listAssistantsHEAD.size() <= 7) && (compteurAssistant >=7 && compteurAssistant <= 13)) {
                listAssistantsHEAD.add(p);
                compteurAssistant++;
            } else if ((listAssistantsHEDS.isEmpty() || listAssistantsHEDS.size() <= 5) && (compteurAssistant >= 14 && compteurAssistant <= 18)) {
                listAssistantsHEDS.add(p);
                compteurAssistant++;
            }else if((listAssistantsHETS.isEmpty() || listAssistantsHETS.size() <= 3) && (compteurAssistant >= 19 && compteurAssistant <= 22)){
                listAssistantsHETS.add(p);
                compteurAssistant++;
            }
        }

        for(Personne p : listAssistantsHEG){
            bdd.run("MATCH (assis:ASSISTANT), (hes:HES) WHERE assis.mail='"+p.getMail()+"' AND hes.nom='HEG' CREATE (assis) -[:TRAVAILLE]-> (hes)");
        }
        for(Personne p : listAssistantsHEAD){
            bdd.run("MATCH (assis:ASSISTANT), (hes:HES) WHERE assis.mail='"+p.getMail()+"' AND hes.nom='HEAD' CREATE (assis) -[:TRAVAILLE]-> (hes)");
        }
        for(Personne p : listAssistantsHEDS){
            bdd.run("MATCH (assis:ASSISTANT), (hes:HES) WHERE assis.mail='"+p.getMail()+"' AND hes.nom='HEDS' CREATE (assis) -[:TRAVAILLE]-> (hes)");
        }
        for(Personne p : listAssistantsHETS){
            bdd.run("MATCH (assis:ASSISTANT), (hes:HES) WHERE assis.mail='"+p.getMail()+"' AND hes.nom='HETS' CREATE (assis) -[:TRAVAILLE]-> (hes)");
        }



    }

    public static List<Personne> concatenerToutesLesListes(){
        List<Personne> allListe = new ArrayList<>();
        allListe.addAll(listeEtudiants);
        allListe.addAll(listeAssistants);
        allListe.addAll(listeProfesseurs);

        /**Est ce qu'on shuffle ?*/
        //Collections.shuffle(allListe);

        return allListe;
    }

    public static List<Personne> getRandomListeDePersonnes(int nbPersonnes, Random generator){

        List<Personne> allListe = concatenerToutesLesListes();
        List<Personne> listeAleatoire = new ArrayList<>();
        for(int i = 0 ; i<nbPersonnes; i++){
            int nb = generator.nextInt(allListe.size());
            listeAleatoire.add(allListe.get(nb));
            allListe.remove(nb);
        }
        return listeAleatoire;
    }

    public static void relationPersonnesAvecAutresPersonnes(Session bdd){

        Random generator = new Random();

        for(int i = 0; i< concatenerToutesLesListes().size(); i++){
            Personne personneCouranteDeLaListe = concatenerToutesLesListes().get(i);

            List<Personne> listeAleatoire = getRandomListeDePersonnes(generator.nextInt(15), generator); //je veux une liste avec un max de 15 personnes
            for(Personne personnesAleatoire : listeAleatoire){
                bdd.run("MATCH (p1:Personne), (p2:Personne) WHERE p1.mail='"+personneCouranteDeLaListe.getMail()+"' AND p2.mail='"+personnesAleatoire.getMail()+"' CREATE (p1) -[:CONNAIT]-> (p2)");
                bdd.run("MATCH (p1:Personne), (p2:Personne) WHERE p1.mail='"+personneCouranteDeLaListe.getMail()+"' AND p2.mail='"+personnesAleatoire.getMail()+"' CREATE (p1) <-[:CONNAIT]- (p2)");

            }

        }


    }









}
