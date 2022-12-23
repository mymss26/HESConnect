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
         System.out.println("****PROCESS EXECUTED****\n");

         try{
             System.out.println("****LES REQUETES****\n");

             System.out.println("1ere requête : Chemin le plus court");
             RequeteBdd.cheminLePlusCourt(bdd);
             System.out.println("********************************\n");

             System.out.println("2ème requête : Requête de calcul");
             RequeteBdd.requeteDeCalcul(bdd);
             System.out.println("********************************\n");


             System.out.println("3ème requête : Proposition d'événements en lien avec les compétences d'un etudiant et thématiques d'un événement ");
             RequeteBdd.requeteSurLesEvenements(bdd);
             System.out.println("********************************\n");


             System.out.println("4ème requête : Barre de recherche");
             //insérer un thème
             String theme = "ASDF";
             RequeteBdd.barreDeRecherche(theme.toUpperCase(), bdd);
             System.out.println("********************************\n");


             System.out.println("5ème requête : Celui qui a le plus de followers");
             RequeteBdd.lePlusDeFollowers(bdd);
             System.out.println("********************************\n");

         }catch (Exception e){
             e.printStackTrace();
         }

     }catch (Exception e){
         System.err.println("Erreur de connexion à la base de données \nMessage : " + e.getMessage());
     }
  }

}
