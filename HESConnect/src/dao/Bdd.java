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


    public static Session connect_db(){
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
                for (int i = 1; i < data.length; i++) {
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
        for(Filiere data : getListeFiliere(bdd)){
           bdd.run("CREATE(:FILIERE{nom:'"+data.getNom()+"',competences:'"+data.getCompetences()+"'})");
           for(String competence : data.getCompetences()){
               bdd.run("CREATE(:COMPETENCE{competence:'"+competence+"'})");
               bdd.run("MATCH (fi:FILIERE), (com:COMPETENCE) WHERE fi.nom='" + data.getNom() + "' AND com.competence='"+competence+ "' CREATE (com) -[:DISPENSEE_DANS]-> (fi)");
           }

        }
    }



    public static void chargerDataHES(Session bdd){
        for (HES e : getListeEcoles()){
            bdd.run("CREATE(:HES{nom:'"+e.getNom()+"', adresse:'"+e.getAdresse()+"'})");
        }
    }

    //Query 1
    public static void cheminLePlusCourt(Session bdd){
        /**
        Random rand = new Random();
        for(Personne p: listeEtudiants){
            if(p.getMail().contains("@heds.ch")){
                Personne randonElement = listeEtudiants.get(rand.nextInt(listeEtudiants.size()));
            }
        }
        */

        System.out.println("Test print rqtes - START METHOD");
        String email = "lgabalaa4@heds.ch";
        String rqte = "MATCH (etuHeds:PERSONNE{mail:'lgabalaa4@heds.ch'}), (heg:HES{nom:'HEG'}), (fi:FILIERE{nom:'Informatique de Gestion'}) " +
                      "MATCH (etuHeds) -[:ETUDIE]-> (r) " +
                      "MATCH a=shortestPath((fi)-[APPARTIENT*]-(heg)) " +
                      "MATCH p=shortestPath((etuHeds)-[t*]-(fi)) "+
                      "RETURN nodes(p)[-2] as m, t[-1], a, r, etuHeds, heg, fi";

        String rqteTest = "MATCH (etuHeds:PERSONNE{mail:'lgabalaa4@heds.ch'}) -[t]-> (etu2:PERSONNE) return t limit 1";

        //nodes(p)[-2] pour recup l'avant dernier qui est le noeud d'une personne de la personne qu'on cherche
        //t[-1] pour recup la derniere relation entre la personne qu'on cherche et la filère IG pour voir si c'est un prof, etudiant ou assistant

        Result result = bdd.run(rqteTest);
        while(result.hasNext()){
            /**on va recup le result => le transformer en java => et afficher le toString*/
            Record rec = result.next();
            if((rec.get("t").get("type")).equals("CONNAIT")){
                System.out.println("il connait");
            }else if((rec.get("t").get("type")).equals("ENSEIGNE_POUR")){
                System.out.println("il enseigne pour");
            }else if((rec.get("t").get("type")).equals("ASSISTE_POUR")){
                System.out.println("Il assiste");
            }else{
                System.out.println("marche pas");
            }


            System.out.println("    "+ rec.get("t"));


            /**
            System.out.println("etuHeds  : " + rec.get("etuHeds").get("nom").asString()+" "+rec.get("etuHeds").get("prenom").asString());
            System.out.println("etuHeds filiere etudie a : " + rec.get("r").get("nom").asString());

            System.out.println("cherche qqun etudiant a : "+rec.get("heg").get("nom").asString());
            System.out.println("qui appartient a la filiere : "+rec.get("fi").get("nom").asString());

            System.out.println("Elle a trouvee : ");
            System.out.println(rec.get("t[-1]").get("name"));
            System.out.println("L'etudiant : " + rec.get("m").get("nom").asString()+" "+rec.get("m").get("prenom").asString());
             */
            //System.out.println(rec.get("m"));
            //for(Object s : rec.get("m").get("nom").asList()){
            //    System.out.println(s);
            //}

            System.out.println("-----------------------------------------");
            System.out.println("2eme try --------------------------------");

            /*
            Map<String, Object> row = rec.get("m").get("nom").asMap();
            String rows = "";
            for(Map.Entry<String, Object> column : row.entrySet()){
                rows+=column.getKey()+":"+column.getValue();
                System.out.println(rows);
            }
            */

            /*
            System.out.println("result " + result);
            System.out.println("result.peek() " + result.peek()); //=> affiche le record des 3 returns
            System.out.println("result.peek().get('p')" + result.peek().get("p"));
            //System.out.println(String.valueOf());
            */
        }

        System.out.println("Test print rqtes - END METHOD");



    }




}

