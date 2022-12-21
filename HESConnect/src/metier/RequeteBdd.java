package metier;

import dao.Bdd;
import domaine.Assistant;
import domaine.Etudiant;
import domaine.Personne;
import domaine.Prof;
import org.neo4j.driver.*;
import org.neo4j.driver.Record;
import org.neo4j.driver.exceptions.Neo4jException;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.types.Relationship;


import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

public class RequeteBdd {

    private static Driver driver;

    // cette methode récupère de maniere aléatoire le mail d'un étudiant en heds et sera utilisé dans la requete #1
    public static Personne getMailAleatoireEtudiantHeds() {
        Random rand = new Random();
        List<Personne> listeEtudiants = Bdd.getListeEtudiant();
        Personne mailEtudiant=null;
        while(mailEtudiant==null || !mailEtudiant.getMail().contains("@heds.ch")) {
             mailEtudiant = listeEtudiants.get(rand.nextInt(listeEtudiants.size()));
        }
        return mailEtudiant;
        }

    //Requête #1 :trouve le chemin le plus court afin d'aider l'étudiant X en heds à trouver un developper en IG pour dev. son projet informatique

    public static void cheminLePlusCourt(Session bdd) {
        System.out.println("1ere requête : Chemin le plus court");

        try {
            String email = getMailAleatoireEtudiantHeds().getMail();
            String rqte = "MATCH (etuHeds:PERSONNE{mail:'" + email + "'}), (heg:HES{nom:'HEG'}), (fi:FILIERE{nom:'Informatique de Gestion'}) " +
                    "MATCH (etuHeds) -[:ETUDIE]-> (r) " +
                    "MATCH a=shortestPath((fi)-[APPARTIENT*]-(heg)) " +
                    "MATCH p=shortestPath((etuHeds)-[t*]-(fi)) " +
                    "WITH apoc.path.elements(p) AS elements,etuHeds,r " +
                    "UNWIND size(elements)-3 AS indexAvantDernierNoeud " + " UNWIND size(elements)-2 AS indexDernierRelation " +
                    "RETURN elements[indexAvantDernierNoeud] as avantDernierNoeud, elements[indexDernierRelation] as dernierRelation, etuHeds,r, elements";

            Result result = bdd.run(rqte);
            // débute le parcours de la requête
            while (result.hasNext()) {
                while (result.hasNext()) {
                    Record record = result.next();
                    Node personne = record.get("etuHeds").asNode();
                    Node ecole = record.get("r").asNode();
                    Node connaissance = record.get("avantDernierNoeud").asNode();
                    Relationship relationConnaissance = record.get("dernierRelation").asRelationship();
                    String typeRelation = "";

                    Personne personneEnHEDS = new Etudiant(personne.get("nom").asString(), personne.get("prenom").asString(), personne.get("mail").asString(), personne.get("genre").asString(), Integer.parseInt(personne.get("codePostal").asString()));
                    Personne personneEnIG = null; //on se ne sait pas encore si la personne que l'on recherche est un professeur, assistant ou un élève

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

                    System.out.println("L'étudiant " + personneEnHEDS.getNom() + " " + personneEnHEDS.getPrenom() + " qui étudie dans la filière " + ecole.get("nom").asString());
                    System.out.println("Peut contacter " + personneEnIG.getNom() + " " + personneEnIG.getPrenom() + " (" + typeRelation + ") à ce mail : '" + personneEnIG.getMail() + "'");

                }
            }
        }catch (NullPointerException e){
            System.out.println("L'étudiant ne connait personne dans son réseau pour contacter une personne d'IG");

        }
        System.out.println("********************************\n");
    }


    public static void requeteDeCalcul(Session bdd){
        System.out.println("2ème requête : Requête de calcul");


        String rqte = "MATCH (etu:PERSONNE)-[r:ETUDIE]->(n:FILIERE)-[:DISPENSEE_DANS]-(c:COMPETENCE) " +
                      "WITH c, count(etu) as num_etu " +
                      "ORDER BY num_etu DESC " +
                      "LIMIT 1 " +
                      "MATCH (etu:PERSONNE)-[r:ETUDIE]->(n:FILIERE)-[:DISPENSEE_DANS]-(c) " +
                      "WITH n, count(etu) as num_etu, c " +
                      "RETURN n, c, num_etu  " +
                      "order by num_etu DESC";


        Result result = bdd.run(rqte);

        Node nomCompetence = null;
        Node nomFiliere = null;
        int nombreEtu = 0;
        int totalEtu = 0;

        while(result.hasNext()){
            Record rec = result.next();
            nomFiliere = rec.get("n").asNode();
            nomCompetence = rec.get("c").asNode();
            nombreEtu = rec.get("num_etu").asInt();
            totalEtu+=nombreEtu;
            System.out.println("\tFilière : "+nomFiliere.get("nom").asString()+", nombre d'étudiants : "+nombreEtu);
        }
        System.out.println("La compétence la plus demandée ("+totalEtu+" personnes) est : "+ nomCompetence.get("competence").asString());
        System.out.println("********************************\n");
    }


    public static void requeteSurLesEvenements(Session bdd){
        System.out.println("3ème requête : Requête sur les événements");
        String rqte = "";
        //Result result = bdd.run(rqte);
        /**
         * Afficher chaque évts et afficher ou elles auront lieu
         * Afficher par évts une liste de personne qui habitent ou l'évt aura lieu
         **/

        System.out.println("********************************\n");
    }


    public static void barreDeRecherche(String theme, Session bdd){
        try {
            String rqte = "MATCH (e:EVENEMENT)-[]-(n) WHERE e.thematique CONTAINS $theme RETURN e, n ";
            Iterable<Record> result = bdd.run(rqte, Values.parameters(new Object[]{"theme", theme})).list();
            if (!result.iterator().hasNext()) {
                throw new Neo4jException("Aucun événement trouvé avec le thème '" + theme + "'");
            }

            String nomEcole = "";
            System.out.println("Le thème recherché est : '" + theme + "' :");
            Iterator var5 = result.iterator();

            while(var5.hasNext()) {
                Record record = (Record)var5.next();
                Node neoudEvt = record.get("e").asNode();
                Node noeudN = record.get("n").asNode();
                nomEcole = noeudN.get("nom").asString();
                System.out.println("Evenement : " + neoudEvt.get("nom").asString());
            }

            System.out.println("Ces événements sont proposé par l' " + nomEcole);
        } catch (Exception var9) {
            System.err.println("Erreur lors de l'exécution de la requête \nMessage : " + var9.getMessage());
        }

    }
}
