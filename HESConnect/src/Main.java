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
        //new Applic();

        for(HES hes : Bdd.getListeEcoles()){
            System.out.println(hes);
        }


        for(Evenement f : Data.listeEvenement()){
            if(f.getThematique().contains("droit")) {
                System.out.println(f);
                System.out.println("=========");
            }

            else if(f.getThematique().contains("numérique")||f.getThematique().contains("entreprenariat")){
                System.out.println(f);
                System.out.println("=========");
            }
            else if (f.getThematique().contains("éthique")) {
                System.out.println(f);
                System.out.println("=========");
            }
            else if (f.getThematique().contains("santé")) {
                System.out.println(f);
                System.out.println("=========");
            }
            else if (f.getThematique().contains("musique")) {
                System.out.println(f);
                System.out.println("=========");
            }
        }



    }
}