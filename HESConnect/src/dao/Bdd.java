package dao;

import domaine.*;
import metier.FabriquePersonne;
import metier.FabriquePersonnes;
import org.neo4j.driver.*;

import org.neo4j.driver.Record;
import org.neo4j.driver.internal.InternalRecord;
import org.neo4j.driver.internal.InternalRelationship;
import org.neo4j.driver.internal.value.RelationshipValue;
import org.neo4j.driver.types.Relationship;

import java.util.*;

public class Bdd {

    static List<Personne> listeEtudiants = new ArrayList<>();
    static List<Personne> listeProfesseurs = new ArrayList<>();
    static List<Personne> listeAssistants = new ArrayList<>();


    public static Session connect_db() {
        return GraphDatabase.driver("bolt://localhost:7687").session();
    }

    public static void setup_env(Session bdd) {
        delete_all(bdd);
        chargerDataPersonne(bdd);
        chargerDataEvenement(bdd);
        chargerDataHES(bdd);
        chargerDataFiliere(bdd);
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

           bdd.run("CREATE (:PERSONNE{nom:'"+p.getNom()+"',prenom:'"+p.getPrenom()+"',mail:'"+p.getMail()+"',genre:'"+p.getGenre()+"', codePostal:'"+p.getCodePostal()+"'})");
           switch (p.getClass().getSimpleName().toUpperCase()){
               case "ETUDIANT": listeEtudiants.add(p);break;
               case "ASSISTANT": listeAssistants.add(p);break;
               case "PROF": listeProfesseurs.add(p);break;
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
            bdd.run("CREATE(:EVENEMENT{nom:'"+data.getNomEvenement()+"',thematique:'"+data.getThematique()+"'})");
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

    public static List<Filiere> getListeFiliere(Session bdd){
        List<String[]> filiere = Data.listeFiliere();
        List<Filiere> lstFiliere = new ArrayList<>();

        for(String[] data : filiere){
            ArrayList<String> lstCompetences = new ArrayList<>();
            if(data.length>2) {
                for (int i = 2; i < data.length; i++) {
                    lstCompetences.add(data[i]);
                }
            }
            else{
                lstCompetences.add(data[2]);}
            lstFiliere.add(new Filiere(data[1],lstCompetences));
        }
        return lstFiliere;
        }

    private static void chargerDataFiliere(Session bdd) { // creation des noeud filiere et competence. Creation de la relation qui les lie
        Set<String> listeCompetences = new HashSet<>();
        for(Filiere data : getListeFiliere(bdd)){
           bdd.run("CREATE(:FILIERE{nom:'"+data.getNom()+"'})");
           for(String competence : data.getCompetences()){
               if(!listeCompetences.contains(competence)){
                   bdd.run("CREATE(:COMPETENCE{competence:'"+competence.toUpperCase()+"'})");
                    listeCompetences.add(competence.toUpperCase());
               }
               bdd.run("MATCH (fi:FILIERE), (com:COMPETENCE) WHERE fi.nom='" + data.getNom() + "' AND com.competence='"+competence.toUpperCase()+ "' CREATE (com) -[:DISPENSEE_DANS]-> (fi)");
           }

        }
    }


    public static void chargerDataHES(Session bdd){
        for (HES e : getListeEcoles()){
            bdd.run("CREATE(:HES{nom:'"+e.getNom()+"', adresse:'"+e.getAdresse()+"'})");
        }
    }
}

