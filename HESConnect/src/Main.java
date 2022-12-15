import dao.Bdd;
import dao.Data;
import domaine.*;
import metier.Applic;
import org.neo4j.driver.Session;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Main {
    public static void main(String[] args) {
        new Applic();


        Random rand = new Random();

        for(Personne p: Bdd.getListeEtudiant()){
            if(p.getMail().contains("@heds.ch")){
                System.out.println(p);
                Personne randonElement = Bdd.getListeEtudiant().get(rand.nextInt(Bdd.getListeEtudiant().size()));
                System.out.println(randonElement);
            }
        }




    }
}