package metier;

import dao.Bdd;
import org.neo4j.driver.Session;

public class Applic {
  public Applic(){

     Session bdd = Bdd.connect_db();
     Bdd.setup_env(bdd);
     GestionRelationsPersonnes.creerRelations(bdd);
     GestionRelationsFilieres.loadRelationFiliere(bdd);

     GestionRelationsEvenements.relationHESetEvenement(bdd);

     System.out.println("Process executed");


  }

}
