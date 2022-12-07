package dao;

import domaine.Personne;
import metier.FabriquePersonne;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Data {

    private static final String FILENAME = "PERSONNES.csv";
    public static List<String[]> listePersonnes() {
        try {
            BufferedReader reader  = new BufferedReader(new FileReader(FILENAME));
            List<String[]> lstPersonne = new ArrayList<>();
            String ligne;
            while ((ligne = reader.readLine()) != null) {
                String[] data = ligne.split(";");
                lstPersonne.add(data);
            }
            reader.close();
            return lstPersonne;
        } catch (IOException e) {  e.printStackTrace(); return null; }
    }
}
