package metier;

import dao.Bdd;
import org.neo4j.driver.Session;

import java.util.Scanner;

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

             System.out.println("1ere requ�te : Chemin le plus court");
             System.out.println("CETTE REQU�TE NECESSISTE L'INSTALLATION EN AMONT DE LA LIBRARIE APOC DANS LE DOSSIER CONF");
             RequeteBdd.cheminLePlusCourt(bdd);
             System.out.println("********************************\n");

             System.out.println("2�me requ�te : Requ�te de calcul");
             RequeteBdd.requeteDeCalcul(bdd);
             System.out.println("********************************\n");


             System.out.println("3�me requ�te : Proposition d'�v�nements en lien avec les comp�tences d'un etudiant et th�matiques d'un �v�nement ");
             RequeteBdd.requeteSurLesEvenements(bdd);
             System.out.println("********************************\n");


             System.out.println("4�me requ�te : Barre de recherche");
             Scanner scanner = new Scanner(System.in);
             //ins�rer un th�me : (droit, num�rique, communication, �thidque, gestion de projet, id�e, sant�, programmation, sociabiliser, Gestion des situations critiques)
             System.out.println("Veuillez inserer un th�me : ");
             String input = scanner.nextLine();
             System.out.println("Vous avez choisi le th�me : "+input);
             RequeteBdd.barreDeRecherche(input.toUpperCase(), bdd);
             System.out.println("********************************\n");


             System.out.println("5�me requ�te : Celui qui a le plus de followers");
             RequeteBdd.lePlusDeFollowers(bdd);
             System.out.println("********************************\n");

         }catch (Exception e){
             e.printStackTrace();
         }

     }catch (Exception e){
         System.err.println("Erreur de connexion � la base de donn�es \nMessage : " + e.getMessage());
     }
  }

}
