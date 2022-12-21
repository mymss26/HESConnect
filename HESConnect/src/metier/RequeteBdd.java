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
import org.w3c.dom.ls.LSOutput;


import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class RequeteBdd {

    private static Driver driver;

    // cette methode récupère de maniere aléatoire le mail d'un étudiant en heds et sera utilisé dans la requete #1
    public static Personne getMailAleatoireEtudiantHeds() {
        Random rand = new Random();
        List<Personne> listeEtudiants = Bdd.getListeEtudiant();
        Personne mailEtudiant = null;
        while (mailEtudiant == null || !mailEtudiant.getMail().contains("@heds.ch")) {
            mailEtudiant = listeEtudiants.get(rand.nextInt(listeEtudiants.size()));
        }
        return mailEtudiant;
    }

    public static Personne getMailAleatoireEtudiant() {
        Random rand = new Random();
        List<Personne> listeEtudiants = Bdd.getListeEtudiant();
        Personne mailEtudiant = null;
        while (mailEtudiant == null) {
            mailEtudiant = listeEtudiants.get(rand.nextInt(listeEtudiants.size()));
        }
        return mailEtudiant;
    }

    //Requête #1 :trouve le chemin le plus court afin d'aider l'étudiant X en heds à trouver un developper en IG pour dev. son projet informatique
    public static void cheminLePlusCourt(Session bdd) {
        try {
            System.out.println("1ere requête : Chemin le plus court");
            String email = getMailAleatoireEtudiantHeds().getMail();
            String rqte = "MATCH (etuHeds:PERSONNE{mail:'" + email + "'}), (heg:HES{nom:'HEG'}), (fi:FILIERE{nom:'Informatique de Gestion'}) " +
                    "MATCH (etuHeds) -[:ETUDIE]-> (r) " +
                    "MATCH a=shortestPath((fi)-[APPARTIENT*]-(heg)) " +
                    "MATCH p=shortestPath((etuHeds)-[t*]-(fi)) " +
                    "WITH apoc.path.elements(p) AS elements,etuHeds,r " +
                    "UNWIND size(elements)-3 AS indexAvantDernierNoeud " + " UNWIND size(elements)-2 AS indexDernierRelation " +
                    "RETURN elements[indexAvantDernierNoeud] as avantDernierNoeud, elements[indexDernierRelation] as dernierRelation, etuHeds,r, elements";

            Result result = bdd.run(rqte);

            if (!result.hasNext()) {
                throw new Neo4jException("Aucun résultat trouvé pour cette requête.");
            }

            // débute le parcours de la requête
            while (result.hasNext()) {
                while (result.hasNext()) {
                    Record record = result.next();
                    Node etuHeds = record.get("etuHeds").asNode();
                    Node ecole = record.get("r").asNode();
                    Node connaissance = record.get("avantDernierNoeud").asNode();


                    Relationship relationConnaissance = record.get("dernierRelation").asRelationship();
                    String typeRelation = "";

                    Personne personneEnHEDS = new Etudiant(etuHeds.get("nom").asString(), etuHeds.get("prenom").asString(), etuHeds.get("mail").asString(), etuHeds.get("genre").asString(), Integer.parseInt(etuHeds.get("codePostal").asString()));
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
            System.out.println("********************************\n");
        } catch (NullPointerException e) {
            System.out.println("L'étudiant ne connait personne dans son réseau pour contacter une personne d'IG");

        }
    }


    public static void requeteDeCalcul(Session bdd) {
        try {
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

            if (!result.hasNext()) {
                throw new Neo4jException("Erreur dans la requête");
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

                System.out.println("\tFilière : " + filiere.getNom() + ", nombre d'étudiants : " + nombreEtu);
            }
            System.out.println("La compétence la plus demandée (" + totalEtu + " personnes) est : " + nomCompetence.get("competence").asString());
            System.out.println("********************************\n");
        } catch (Exception e) {
            System.err.println("Une erreur s'est produite lors de l'exécution de la requête \nMessage : " + e.getMessage());
        }
    }


    public static void requeteSurLesEvenements(Session bdd) {
        System.out.println("3ème requête : Proposition d'événements en lien avec les compétences d'un etudiant et thématiques d'un événement ");
        String email = getMailAleatoireEtudiant().getMail();
        String rqte = "MATCH (p:PERSONNE {mail:'" + email + "'})-[:ETUDIE]->(fi:FILIERE)-[]-(c:COMPETENCE) " +
                "WITH c,p,fi " +
                "MATCH (e:EVENEMENT)-[:PROPOSE]-(hes:HES) " +
                "WITH c.competence as motC, e.thematique as motT,e,p,fi " + "where motT contains motC " +
                "RETURN motC,motT,e,p,fi ";
        Result result = bdd.run(rqte);

        List<String> listeCompetence = new ArrayList<>();
        List<String> listeEvenements = new ArrayList<>();
        List<String> listeThematique = new ArrayList<>();
        Node personne = null;
        Node filiere = null;
        if (!result.hasNext()) {
            throw new Neo4jException("Il est possible que cette requête n'ai permis de trouver aucun résultat. Essayez avec une nouvelle personne.");
        }
        while (result.hasNext()) {
            Record rec = result.next();
            Node evenement = rec.get("e").asNode();
            String comp = rec.get("motC").asString();
            String thematique = rec.get("motT").asString();
            personne = rec.get("p").asNode();
            filiere = rec.get("fi").asNode();
            if (!listeEvenements.contains(evenement.get("nom").asString())) {
                listeEvenements.add(evenement.get("nom").asString());
            }
            listeCompetence.add(comp);
            listeThematique.add(thematique);

        }
        if (!listeEvenements.isEmpty()) {
            System.out.println("Voici la liste d'événement(s) proposé(s) à " + personne.get("nom").asString() + " " + personne.get("prenom").asString() + "(" + filiere.get("nom").asString() + ")");
            for (int i = 0; i < listeEvenements.size(); i++) {
                System.out.println("- '" + listeEvenements.get(i) + "'");
            }
        }

    }


    public static void barreDeRecherche(String theme, Session bdd) {
        System.out.println("********************************");
        try {
            System.out.println("4ème requête : Barre de recherche");
            String rqte = "MATCH (e:EVENEMENT)-[]-(n) WHERE e.thematique CONTAINS $theme RETURN e, n ";
            Iterable<Record> result = bdd.run(rqte, Values.parameters(new Object[]{"theme", theme})).list();
            if (!result.iterator().hasNext()) {
                throw new Neo4jException("Aucun événement trouvé avec le thème '" + theme + "'");
            }

            String nomEcole = "";
            String adresseEcole = "";
            String nomEvenenement = "";
            int cpteEvt = 0;

            System.out.println("Le thème recherché est : '" + theme + "' :");

            Iterator it = result.iterator();

            while (it.hasNext()) {
                Record record = (Record) it.next();
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
            if (nomEcole.equalsIgnoreCase("heg")) {
                hes = new HEG(nomEcole, adresseEcole);
            } else if (nomEcole.equalsIgnoreCase("heds")) {
                hes = new HEDS(nomEcole, adresseEcole);
            } else if (nomEcole.equalsIgnoreCase("head")) {
                hes = new HEAD(nomEcole, adresseEcole);
            } else if (nomEcole.equalsIgnoreCase("hets")) {
                hes = new HETS(nomEcole, adresseEcole);
            }

            assert hes != null;
            if (cpteEvt == 1) {
                System.out.print("Cet événement est proposé ");
            } else {
                System.out.print("Ces événements sont proposés ");
            }
            System.out.println("par l' " + hes.getNom());
            System.out.println("********************************\n");
        } catch (Exception e) {
            System.err.println("Erreur lors de l'exécution de la requête \nMessage : " + e.getMessage());
        }

    }


    public static void lePlusDeFollowers(Session bdd) {
        try {
            System.out.println("5ème requête : Celui qui a le plus de followers");
            String rqte = "MATCH (p:PERSONNE)<-[:CONNAIT]-(c:PERSONNE) " +
                    "WITH p, COUNT(*) AS count " +
                    "ORDER BY count DESC " +
                    "LIMIT 1 " +
                    "MATCH (p)-[:CONNAIT]-(c) " +
                    "MATCH (fi:FILIERE)<-[]-(p) " +
                    "RETURN fi, p, c, count ";

            Result result = bdd.run(rqte);
            if (!result.hasNext()) {
                throw new Neo4jException("Erreur dans la requête");
            }
            while (result.hasNext()) {
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
