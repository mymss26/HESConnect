package metier;

import dao.Bdd;
import domaine.Assistant;
import domaine.Etudiant;
import domaine.Personne;
import domaine.Prof;
import io.netty.util.collection.IntObjectMap;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.types.Path;
import org.neo4j.driver.types.Relationship;

import javax.swing.text.html.parser.Parser;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class RequeteBdd {

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
        String email = getMailAleatoireEtudiantHeds().getMail();
        String rqte = "MATCH (etuHeds:PERSONNE{mail:'"+email+"'}), (heg:HES{nom:'HEG'}), (fi:FILIERE{nom:'Informatique de Gestion'}) "+
                      "MATCH (etuHeds) -[:ETUDIE]-> (r) "+
                      "MATCH a=shortestPath((fi)-[APPARTIENT*]-(heg)) "+
                      "MATCH p=shortestPath((etuHeds)-[t*]-(fi)) "+
                      "WITH apoc.path.elements(p) AS elements,etuHeds,r "+
                      "UNWIND size(elements)-3 AS indexAvantDernierNoeud "+ " UNWIND size(elements)-2 AS indexDernierRelation "+
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

                switch (relationConnaissance.type()){
                    case "ETUDIE": typeRelation="etudiant";
                        personneEnIG = new Etudiant(connaissance.get("nom").asString(), connaissance.get("prenom").asString(), connaissance.get("mail").asString(), connaissance.get("genre").asString(), Integer.parseInt(connaissance.get("codePostal").asString()));break;
                    case "ENSEIGNE_POUR": typeRelation="enseignant";
                        personneEnIG = new Prof(connaissance.get("nom").asString(), connaissance.get("prenom").asString(), connaissance.get("mail").asString(), connaissance.get("genre").asString(), Integer.parseInt(connaissance.get("codePostal").asString()));break;
                    case "ASSISTE_POUR": typeRelation="assistant";
                        personneEnIG = new Assistant(connaissance.get("nom").asString(), connaissance.get("prenom").asString(), connaissance.get("mail").asString(), connaissance.get("genre").asString(), Integer.parseInt(connaissance.get("codePostal").asString()));break;
                }

                assert personneEnIG != null;
                if(personneEnIG.getMail() == null){ // test pour voir si l'étudiant de la heds n'aurait personne dans son réseaux pour contacter une personne en IG
                    System.out.println("L'étudiant "+ personneEnHEDS.getNom() + " " + personneEnHEDS.getPrenom() + " " + " ne connait personne dans son réseau pour contacter une personne d'IG");
                }else {
                    System.out.println("L'étudiant " + personneEnHEDS.getNom() + " " + personneEnHEDS.getPrenom() + " qui étudie dans la filière " + ecole.get("nom").asString());
                    System.out.println("Peut contacter " + personneEnIG.getNom() + " " + personneEnIG.getPrenom() + " (" + typeRelation + ") à ce mail : '" + personneEnIG.getMail()+"'");
                }
            }
        }
        System.out.println("********************************\n");
    }


    public static void requeteDeCalcul(Session bdd){
        System.out.println("2ème requête : Requête de calcul");

        //Cette rqte nous retourne la compétence la plus demandée parmi les étudiants
        String rqtePourGetCompetence = "MATCH (etu:PERSONNE)-[r:ETUDIE]->(n:FILIERE)-[:DISPENSEE_DANS]-(c:COMPETENCE) " +
                                       "WITH  count(etu) as num_etu, c.competence as competence " +
                                       "MATCH (etu:PERSONNE)-[r:ETUDIE]->() " +
                                       "WITH  num_etu, count(etu) as total_etu, competence " +
                                       "WITH num_etu, competence " +
                                       "MATCH (n:FILIERE)-[:DISPENSEE_DANS]-(c:COMPETENCE {competence: competence}) " +
                                       "RETURN  c.competence as competence, num_etu " +
                                       "LIMIT 1";


        //Cette rqte nous retourne la filière ainsi que le pourcentage de personne qui ont la compétence de la requête ci-dessus
        String rqteFilierePourcentage = "MATCH (etu:PERSONNE)-[r:ETUDIE]->(n:FILIERE) " +
                                        "WITH n, count(etu) as num_etu " +
                                        "MATCH (etu:PERSONNE)-[r:ETUDIE]->() " +
                                        "WITH n, num_etu, count(etu) as total_etu " +
                                        "RETURN n.nom as filiere, round(100.0 * num_etu / total_etu) as percentage  " +
                                        "ORDER BY percentage DESC";

        Result result1 = bdd.run(rqtePourGetCompetence);
        Result result2 = bdd.run(rqteFilierePourcentage);

        //1er MATCH
        while(result1.hasNext()){
            Record rec = result1.next();
            String nomCompetence = rec.get("competence").asString();
            int nombreEtu = rec.get("num_etu").asInt();
            System.out.println("La compétence la plus demandée ("+nombreEtu+" personnes) est : "+ nomCompetence);
        }

        //2e MATCH
        while(result2.hasNext()){
            Record rec = result2.next();
            String nomFiliere = rec.get("filiere").asString();
            int pourcentage = rec.get("percentage").asInt();

            System.out.printf("Filière : %s, Pourcentage: %d%%\n", nomFiliere, pourcentage); /**print f*/
        }



        System.out.println("********************************\n");
    }


    public static void requeteSurLesEvenements(Session bdd){
        System.out.println("3ème requête : Requête sur les événements");
        String rqte = "";
        //Result result = bdd.run(rqte);
        /**
         * Afficher chaque évts et afficher ou elles auront lieu
         * Afficher par évts une liste de personne qui habitent ou l'évt aura lieu
         * */



        System.out.println("********************************\n");
    }




}
