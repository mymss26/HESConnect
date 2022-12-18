package metier;

import dao.Bdd;
import domaine.Assistant;
import domaine.Etudiant;
import domaine.Personne;
import domaine.Prof;
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
        System.out.println("1ere requ�te : Chemin le plus court");
        String email = getMailAleatoireEtudiantHeds().getMail();
        String rqte = "MATCH (etuHeds:PERSONNE{mail:'"+email+"'}), (heg:HES{nom:'HEG'}), (fi:FILIERE{nom:'Informatique de Gestion'}) "+
                      "MATCH (etuHeds) -[:ETUDIE]-> (r) "+
                      "MATCH a=shortestPath((fi)-[APPARTIENT*]-(heg)) "+
                      "MATCH p=shortestPath((etuHeds)-[t*]-(fi)) "+
                      "WITH apoc.path.elements(p) AS elements,etuHeds,r "+
                      "UNWIND size(elements)-3 AS indexAvantDernierNoeud "+ " UNWIND size(elements)-2 AS indexDernierRelation "+
                      "RETURN elements[indexAvantDernierNoeud] as avantDernierNoeud, elements[indexDernierRelation] as dernierRelation, etuHeds,r, elements";

        Result result = bdd.run(rqte);
        // d�bute le parcours de la requ�te
        while (result.hasNext()) {
            while (result.hasNext()) {
                Record record = result.next();
                Node personne = record.get("etuHeds").asNode();
                Node ecole = record.get("r").asNode();
                Node connaissance = record.get("avantDernierNoeud").asNode();
                Relationship relationConnaissance = record.get("dernierRelation").asRelationship();
                String typeRelation = "";

                Personne personneEnHEDS = new Etudiant(personne.get("nom").asString(), personne.get("prenom").asString(), personne.get("mail").asString(), personne.get("genre").asString(), Integer.parseInt(personne.get("codePostal").asString()));
                Personne personneEnIG = null; //on se ne sait pas encore si la personne que l'on recherche est un professeur, assistant ou un �l�ve

                switch (relationConnaissance.type()){
                    case "ETUDIE": typeRelation="etudiant";
                        personneEnIG = new Etudiant(connaissance.get("nom").asString(), connaissance.get("prenom").asString(), connaissance.get("mail").asString(), connaissance.get("genre").asString(), Integer.parseInt(connaissance.get("codePostal").asString()));break;
                    case "ENSEIGNE_POUR": typeRelation="enseignant";
                        personneEnIG = new Prof(connaissance.get("nom").asString(), connaissance.get("prenom").asString(), connaissance.get("mail").asString(), connaissance.get("genre").asString(), Integer.parseInt(connaissance.get("codePostal").asString()));break;
                    case "ASSISTE_POUR": typeRelation="assistant";
                        personneEnIG = new Assistant(connaissance.get("nom").asString(), connaissance.get("prenom").asString(), connaissance.get("mail").asString(), connaissance.get("genre").asString(), Integer.parseInt(connaissance.get("codePostal").asString()));break;
                }

                assert personneEnIG != null;
                if(personneEnIG.getMail() == null){ // test pour voir si l'�tudiant de la heds n'aurait personne dans son r�seaux pour contacter une personne en IG
                    System.out.println("L'�tudiant "+ personneEnHEDS.getNom() + " " + personneEnHEDS.getPrenom() + " " + " ne connait personne dans son r�seau pour contacter une personne d'IG");
                }else {
                    System.out.println("L'�tudiant " + personneEnHEDS.getNom() + " " + personneEnHEDS.getPrenom() + " qui �tudie dans la fili�re " + ecole.get("nom").asString());
                    System.out.println("Peut contacter " + personneEnIG.getNom() + " " + personneEnIG.getPrenom() + " (" + typeRelation + ") � ce mail : '" + personneEnIG.getMail()+"'");
                }
            }
        }
        System.out.println("********************************\n");
    }


    public static void requeteDeCalcul(Session bdd){
        System.out.println("2�me requ�te : Requ�te de calcul");
        String rqte = "MATCH a= (e:EVENEMENT)<-[prop:PROPOSE]-(hes:HES)<-[ap:APPARTIENT]-(fi:FILIERE)<-[inconnu]-(p:PERSONNE) " +
                      "WHERE p.codePostal='1203' OR p.codePostal='1227' OR p.codePostal='1206' OR p.codePostal='1211' " +
                      "RETURN a";
        Result result = bdd.run(rqte);



        System.out.println("********************************\n");
    }


    public static void requeteSurLesEvenements(Session bdd){
        System.out.println("3�me requ�te : Requ�te sur les �v�nements");
        String rqte = "";
        //Result result = bdd.run(rqte);
        /**
         * Afficher chaque �vts et afficher ou elles auront lieu
         * Afficher par �vts une liste de personne qui habitent ou l'�vt aura lieu
         * */



        System.out.println("********************************\n");
    }




}
