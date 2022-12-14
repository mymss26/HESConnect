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


        for(Evenement f : Data.listeEvenement()){

            System.out.println(f);
        }



    }
}