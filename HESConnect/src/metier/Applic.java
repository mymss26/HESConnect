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
             RequeteBdd.cheminLePlusCourt(bdd);
             RequeteBdd.requeteDeCalcul(bdd);
             RequeteBdd.requeteSurLesEvenements(bdd);
             //ins�rer un th�me
             String theme = "droit";
             RequeteBdd.barreDeRecherche(theme.toUpperCase(), bdd);

             RequeteBdd.lePlusDeFollowers(bdd);
         }catch (Exception e){
             e.printStackTrace();
         }

     }catch (Exception e){
         System.err.println("Erreur de connexion � la base de donn�es \nMessage : " + e.getMessage());
     }
  }

}
