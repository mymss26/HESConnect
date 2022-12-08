package metier;

import dao.Bdd;
import org.neo4j.driver.Session;

public class Applic {
  public Applic(){
     Session bdd = Bdd.connect_db();
     Bdd.setup_env(bdd);
     Bdd.chargerDataPersonne(bdd);
     Bdd.chargerDataEvenement(bdd);
     Bdd.chargerDataHES(bdd);
  }

}
