package dao;

import domaine.Competence;
import domaine.Evenement;
import domaine.Filiere;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Data {

    private static final String FILENAME_Personne = "PERSONNES.csv";
    private static final String FILENAME_Evenement= "EVENEMENTS.csv";
    private static final String FILENAME_Filiere= "FILIERES_HES.csv";
    private static final String FILENAME_Competences="COMPETENCES.csv";

    public static List<String[]> listePersonnes() {
        try {
            List<String> lstMail = new ArrayList<>();
            lstMail.add("@heg.ch");
            lstMail.add("@head.ch");
            lstMail.add("@hes.ch");
            lstMail.add("@heds.ch");
            lstMail.add("@hets.ch");

            Random random = new Random();
            BufferedReader reader  = new BufferedReader(new FileReader(FILENAME_Personne));
            List<String[]> lstPersonne = new ArrayList<>();
            String ligne;
            while ((ligne = reader.readLine()) != null) {
                String[] data = ligne.split(";");
                String ancienMail = data[2];
                if(ancienMail.contains("@")){
                    String[] mail = ancienMail.split("@");
                    String newmail = mail[0]+lstMail.get(random.nextInt(lstMail.size()));
                    data[2]=data[2].replace(ancienMail,newmail);
                }

                lstPersonne.add(data);
            }
            reader.close();
            return lstPersonne;
        } catch (IOException e) {  e.printStackTrace(); return null; }
    }

    public static List<Evenement> listeEvenement() {
        try {
            BufferedReader reader  = new BufferedReader(new FileReader(FILENAME_Evenement));
            List<Evenement> lstEvenement= new ArrayList<>();
            String ligne;
            while ((ligne = reader.readLine()) != null) {
                String[] data = ligne.split(";");
                ArrayList<String> lstThematique = new ArrayList<>();
                if(data.length>1) {
                    for (int i = 1; i < data.length; i++) {
                        lstThematique.add(data[i]);
                    }
                }
                else{
                lstThematique.add(data[1]);}
                lstEvenement.add(new Evenement(data[0],lstThematique));
            }
            reader.close();
            return lstEvenement;
        } catch (IOException e) {  e.printStackTrace(); return null; }
    }

    public static List<String[]> listeFiliere() {
        try {
            BufferedReader reader  = new BufferedReader(new FileReader(FILENAME_Filiere));
            List<String[]> lstFiliere= new ArrayList<>();
            String ligne;
            while ((ligne = reader.readLine()) != null) {
                String[] data = ligne.split(";");
                lstFiliere.add(data);
            }
            reader.close();
            return lstFiliere;
        } catch (IOException e) {  e.printStackTrace(); return null; }
    }

    public static List<Competence> listeCompetences(){
        try {
            BufferedReader reader  = new BufferedReader(new FileReader(FILENAME_Competences));
            ArrayList<Competence> listeCompetences = new ArrayList<>();
            String ligne;
            while ((ligne = reader.readLine()) != null) {
                String[] data = ligne.split(";");
                listeCompetences.add(new Competence(data[0]));
            }
            reader.close();
            return listeCompetences;
        } catch (IOException e) { e.printStackTrace(); return null; }
    }

}
