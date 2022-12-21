package metier;

import dao.Bdd;
import domaine.*;
import org.neo4j.driver.*;
import org.neo4j.driver.Record;
import org.neo4j.driver.exceptions.Neo4jException;
import org.neo4j.driver.exceptions.NoSuchRecordException;
import org.neo4j.driver.exceptions.TransientException;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.types.Relationship;


import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

public class RequeteBdd {

    private static Driver driver;

    // cette methode r�cup�re de maniere al�atoire le mail d'un �tudiant en heds et sera utilis� dans la requete #1
    public static Personne getMailAleatoireEtudiantHeds() {
        Random rand = new Random();
        List<Personne> listeEtudiants = Bdd.getListeEtudiant();
        Personne mailEtudiant=null;
        while(mailEtudiant==null || !mailEtudiant.getMail().contains("@heds.ch")) {
             mailEtudiant = listeEtudiants.get(rand.nextInt(listeEtudiants.size()));
        }
        return mailEtudiant;
        }

    //Requ�te #1 :trouve le chemin le plus court afin d'aider l'�tudiant X en heds � trouver un developper en IG pour dev. son projet informatique
    public static void cheminLePlusCourt(Session bdd) {
        try {
            System.out.println("1ere requ�te : Chemin le plus court");
            String email = getMailAleatoireEtudiantHeds().getMail();
            String rqte = "MATCH (etuHeds:PERSONNE{mail:'" + email + "'}), (heg:HES{nom:'HEG'}), (fi:FILIERE{nom:'Informatique de Gestion'}) " +
                    "MATCH (etuHeds) -[:ETUDIE]-> (r) " +
                    "MATCH a=shortestPath((fi)-[APPARTIENT*]-(heg)) " +
                    "MATCH p=shortestPath((etuHeds)-[t*]-(fi)) " +
                    "WITH apoc.path.elements(p) AS elements,etuHeds,r " +
                    "UNWIND size(elements)-3 AS indexAvantDernierNoeud " + " UNWIND size(elements)-2 AS indexDernierRelation " +
                    "RETURN elements[indexAvantDernierNoeud] as avantDernierNoeud, elements[indexDernierRelation] as dernierRelation, etuHeds,r, elements";

            Result result = bdd.run(rqte);

            if(!result.hasNext()){
                throw new Neo4jException("Aucun r�sultat trouv� pour cette requ�te.");
            }

            // d�bute le parcours de la requ�te
            while (result.hasNext()) {
                while (result.hasNext()) {
                    Record record = result.next();
                    Node etuHeds = record.get("etuHeds").asNode();
                    Node ecole = record.get("r").asNode();
                    Node connaissance = record.get("avantDernierNoeud").asNode();



                    Relationship relationConnaissance = record.get("dernierRelation").asRelationship();
                    String typeRelation = "";

                    Personne personneEnHEDS = new Etudiant(etuHeds.get("nom").asString(), etuHeds.get("prenom").asString(), etuHeds.get("mail").asString(), etuHeds.get("genre").asString(), Integer.parseInt(etuHeds.get("codePostal").asString()));
                    Personne personneEnIG = null; //on se ne sait pas encore si la personne que l'on recherche est un professeur, assistant ou un �l�ve

                    switch (relationConnaissance.type()) {
                        case "ETUDIE":
                            typeRelation = "etudiant";
                            personneEnIG = new Etudiant(connaissance.get("nom").asString(), connaissance.get("prenom").asString(), connaissance.get("mail").asString(), connaissance.get("genre").asString(), Integer.parseInt(connaissance.get("codePostal").asString()));
                            break;
                        case "ENSEIGNE_POUR":
                            typeRelation = "enseignant";
                            personneEnIG = new Prof(connaissance.get("nom").asString(), connaissance.get("prenom").asString(), connaissance.get("mail").asString(), connaissance.get("genre").asString(), Integer.parseInt(connaissance.get("codePostal").asString()));
                            break;
                        case "ASSISTE_POUR":
                            typeRelation = "assistant";
                            personneEnIG = new Assistant(connaissance.get("nom").asString(), connaissance.get("prenom").asString(), connaissance.get("mail").asString(), connaissance.get("genre").asString(), Integer.parseInt(connaissance.get("codePostal").asString()));
                            break;
                    }

                    System.out.println("L'�tudiant " + personneEnHEDS.getNom() + " " + personneEnHEDS.getPrenom() + " qui �tudie dans la fili�re " + ecole.get("nom").asString());
                    System.out.println("Peut contacter " + personneEnIG.getNom() + " " + personneEnIG.getPrenom() + " (" + typeRelation + ") � ce mail : '" + personneEnIG.getMail() + "'");

                }
            }
            System.out.println("********************************\n");
        }catch (NullPointerException e){
            System.out.println("L'�tudiant ne connait personne dans son r�seau pour contacter une personne d'IG");

        }
    }


    public static void requeteDeCalcul(Session bdd) {
        try {
            System.out.println("2�me requ�te : Requ�te de calcul");
            String rqte = "MATCH (etu:PERSONNE)-[r:ETUDIE]->(n:FILIERE)-[:DISPENSEE_DANS]-(c:COMPETENCE) " +
                    "WITH c, count(etu) as num_etu " +
                    "ORDER BY num_etu DESC " +
                    "LIMIT 1 " +
                    "MATCH (etu:PERSONNE)-[r:ETUDIE]->(n:FILIERE)-[:DISPENSEE_DANS]-(c) " +
                    "WITH n, count(etu) as num_etu, c " +
                    "RETURN n, c, num_etu  " +
                    "order by num_etu DESC";


            Result result = bdd.run(rqte);

            if (!result.hasNext()) {
                throw new Neo4jException("Erreur dans la requ�te");
            }

            Node nomCompetence = null;
            Node nomFiliere = null;
            int nombreEtu = 0;
            int totalEtu = 0;

            while (result.hasNext()) {
                Record rec = result.next();
                nomFiliere = rec.get("n").asNode();
                nomCompetence = rec.get("c").asNode();
                nombreEtu = rec.get("num_etu").asInt();
                totalEtu += nombreEtu;

                Filiere filiere = new Filiere(nomFiliere.get("nom").asString());

                System.out.println("\tFili�re : " + filiere.getNom() + ", nombre d'�tudiants : " + nombreEtu);
            }
            System.out.println("La comp�tence la plus demand�e (" + totalEtu + " personnes) est : " + nomCompetence.get("competence").asString());
            System.out.println("********************************\n");
        } catch (Exception e) {
            System.err.println("Une erreur s'est produite lors de l'ex�cution de la requ�te \nMessage : "+e.getMessage());
        }
    }


    public static void requeteSurLesEvenements(Session bdd){
        System.out.println("3�me requ�te : Requ�te sur les �v�nements");
        String rqte = "";
        //Result result = bdd.run(rqte);
        /**
         * Afficher chaque �vts et afficher ou elles auront lieu
         * Afficher par �vts une liste de personne qui habitent ou l'�vt aura lieu
         **/

        System.out.println("********************************\n");
    }


    public static void barreDeRecherche(String theme, Session bdd){
        try {
            System.out.println("4�me requ�te : Barre de recherche");
            String rqte = "MATCH (e:EVENEMENT)-[]-(n) WHERE e.thematique CONTAINS $theme RETURN e, n ";
            Iterable<Record> result = bdd.run(rqte, Values.parameters(new Object[]{"theme", theme})).list();
            if (!result.iterator().hasNext()) {
                throw new Neo4jException("Aucun �v�nement trouv� avec le th�me '" + theme + "'");
            }

            String nomEcole = "";
            String adresseEcole = "";
            String nomEvenenement = "";
            int cpteEvt = 0;

            System.out.println("Le th�me recherch� est : '" + theme + "' :");

            Iterator it = result.iterator();

            while(it.hasNext()) {
                Record record = (Record)it.next();
                Node neoudEvt = record.get("e").asNode();
                Node noeudN = record.get("n").asNode();

                nomEvenenement = neoudEvt.get("nom").asString();
                Evenement evenement = new Evenement(nomEvenenement);

                nomEcole = noeudN.get("nom").asString();
                adresseEcole = noeudN.get("adresse").asString();

                System.out.println("Evenement : " + evenement.getNomEvenement());
                cpteEvt++;
            }
            HES hes = null;
            if(nomEcole.equalsIgnoreCase("heg")){
                hes = new HEG(nomEcole, adresseEcole);
            }else if (nomEcole.equalsIgnoreCase("heds")){
                hes = new HEDS(nomEcole, adresseEcole);
            }else if(nomEcole.equalsIgnoreCase("head")){
                hes = new HEAD(nomEcole, adresseEcole);
            } else if (nomEcole.equalsIgnoreCase("hets")) {
                hes = new HETS(nomEcole, adresseEcole);
            }

            assert hes != null;
            if(cpteEvt==1){
                System.out.print("Cet �v�nement est propos� ");
            }else{
                System.out.print("Ces �v�nements sont propos�s ");
            }
            System.out.println("par l' " + hes.getNom());
            System.out.println("********************************\n");
        } catch (Exception e) {
            System.err.println("Erreur lors de l'ex�cution de la requ�te \nMessage : " + e.getMessage());
        }

    }


    public static void lePlusDeFollowers(Session bdd) {
        try {
            System.out.println("5�me requ�te : Celui qui a le plus de followers");
            String rqte = "MATCH (p:PERSONNE)<-[:CONNAIT]-(c:PERSONNE) " +
                          "WITH p, COUNT(*) AS count " +
                          "ORDER BY count DESC " +
                          "LIMIT 1 " +
                          "MATCH (p)-[:CONNAIT]-(c) " +
                          "MATCH (fi:FILIERE)<-[]-(p) " +
                          "RETURN fi, p, c, count ";

            Result result = bdd.run(rqte);
            if (!result.hasNext()) {
                throw new Neo4jException("Erreur dans la requ�te");
            }
            while(result.hasNext()){
                Record record = result.next();
                Node filiereNode = record.get("fi").asNode();
                String filiereName = filiereNode.get("name").asString();
                Node personNode = record.get("p").asNode();
                String personName = personNode.get("name").asString();
                Node connectionNode = record.get("c").asNode();
                String connectionName = connectionNode.get("name").asString();
                int count = record.get("count").asInt();

                System.out.println("FILIERE: " + filiereName);
                System.out.println("PERSON: " + personName);
                System.out.println("CONNECTION: " + connectionName);
                System.out.println("COUNT: " + count);

            }


        } catch (Exception e) {
            e.printStackTrace();
        }


    }


}
