package metier;

import dao.Bdd;
import org.neo4j.driver.Session;

public class Applic {
  public Applic()  {

     try{
         Session bdd = Bdd.connect_db();
         Bdd.setup_env(bdd);
         GestionRelationsPersonnes.creerRelations(bdd);
         GestionRelationsFilieres.loadRelationFiliere(bdd);

         GestionRelationsEvenements.relationHESetEvenement(bdd);

         System.out.println("Process executed");

         System.out.println("Les requêtes : ");
         RequeteBdd.cheminLePlusCourt(bdd);
         RequeteBdd.requeteDeCalcul(bdd);

         //insérer un thème
         String theme = "musique";
         RequeteBdd.barreDeRecherche(theme.toUpperCase(), bdd);

     }catch (Exception e){
         System.err.println("Erreur de connexion à la base de données \nMessage : " + e.getMessage());
     }
  }

}
