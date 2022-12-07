package metier;

import dao.Bdd;
import org.neo4j.driver.Session;

public class Applic {
  public Applic(){
     Session bdd = Bdd.connect_db();
     Bdd.chargerDataPersonne(bdd);
  }

}
