package metier;

import dao.Bdd;
import domaine.Personne;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.types.Path;
import org.neo4j.driver.types.Relationship;

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
        System.out.println("Print rqtes - START METHOD");
        String email = getMailAleatoireEtudiantHeds().getMail();
        String rqte = "MATCH (etuHeds:PERSONNE{mail:'"+email+"'}), (heg:HES{nom:'HEG'}), (fi:FILIERE{nom:'Informatique de Gestion'}) "+
                      "MATCH (etuHeds) -[:ETUDIE]-> (r) "+
                      "MATCH a=shortestPath((fi)-[APPARTIENT*]-(heg)) "+
                      "MATCH p=shortestPath((etuHeds)-[t*]-(fi)) "+
                      "WITH apoc.path.elements(p) AS elements,etuHeds,r "+
                      "UNWIND size(elements)-3 AS indexAvantDernierNoeud "+ " UNWIND size(elements)-2 AS indexDernierRelation "+
                      "RETURN elements[indexAvantDernierNoeud] as avantDernierNoeud, elements[indexDernierRelation] as dernierRelation, etuHeds,r";

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
                switch (relationConnaissance.type()){
                    case "ETUDIE":typeRelation="etudiant";break;
                    case "CONNAIT":typeRelation="connaissance";break;
                    case "ENSEIGNE_POUR":typeRelation="enseignant";break;
                    case "ASSISTE_POUR":typeRelation="assistant";break;
                }
                System.out.println("L'étudiant "+ personne.get("nom").asString()+" "+personne.get("prenom").asString()+" qui étudie dans la filière "+ecole.get("nom").asString());
                System.out.println("Peut contacter "+connaissance.get("nom").asString()+" "+connaissance.get("prenom").asString()+" ("+typeRelation+") à ce mail :"+connaissance.get("mail"));
            }
        }
    }


    }
