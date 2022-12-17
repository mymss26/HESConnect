package metier;

import org.neo4j.driver.Result;
import org.neo4j.driver.Session;

public class AffichageRequetes {

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
                "MATCH p=shortestPath((etuHeds)-[ETUDIE*]-(fi)) "+
                "RETURN p,a,r";

        Result result = bdd.run(rqte);
        while(result.hasNext()){
            /**on va recup le result => le transformer en java => et afficher le toString*/
            System.out.println(result);
            System.out.println(result.peek()); //=> ca m'affiche le record des 3 returns
            System.out.println(result.peek().get("p"));
            result.next();
        }

        System.out.println("Test print rqtes - END METHOD");
    }


}
