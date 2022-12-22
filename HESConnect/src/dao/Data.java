package dao;

import domaine.Evenement;
import domaine.Filiere;


import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Data {

    private static final String FILENAME_Personne = "Personne_2.csv";
    private static final String FILENAME_Evenement = "EVENEMENTS.csv";
    private static final String FILENAME_Filiere = "FILIERES_HES.csv";

    private static List<String> constructionMail() {
        List<String> lstMail = new ArrayList<>();
        lstMail.add("@heg.ch");
        lstMail.add("@head.ch");
        lstMail.add("@prof.hes.ch");
        lstMail.add("@ass.hes.ch");
        lstMail.add("@heds.ch");
        lstMail.add("@hets.ch");
        return lstMail;
    }

    public static List<String[]> listePersonnes() {
        try {
            Random randomMail = new Random();
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(FILENAME_Personne), "UTF-8"));
            List<String[]> lstPersonne = new ArrayList<>();
            String ligne;
            int countHES = 0;
            while ((ligne = reader.readLine()) != null) {
                String[] data = ligne.split(",");
                String ancienMail = data[2];
                if (ancienMail.contains("@")) {
                    String[] mail = ancienMail.split("@");
                    List<String> constructionMailList = constructionMail();
                    String valeurListeMail = "";
                    try {
                        valeurListeMail = constructionMailList.get(randomMail.nextInt(constructionMailList.size()));
                        if (valeurListeMail.equals("@prof.hes.ch")) { // dans le cas où la valeur récupéré est @prof.hes.ch
                            countHES++;
                            if (countHES < 50) { // si le nombre de mail ayant utilisé @prof.hets.ch reste inferieur à 50
                                data[2] = mail[0] + valeurListeMail;
                            } // on peu concaténer
                            else { // sinon, on relance le random pour avoir un autre mail
                                valeurListeMail = constructionMailList.get(randomMail.nextInt(constructionMailList.size()));
                            }
                        }
                        data[2] = mail[0] + valeurListeMail;
                    } catch (IllegalArgumentException e) {
                    }
                    data[2] = mail[0] + valeurListeMail;
                    lstPersonne.add(data);
                }
            }
            reader.close();
            return lstPersonne;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static List<Evenement> listeEvenement() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(FILENAME_Evenement));
            List<Evenement> lstEvenement = new ArrayList<>();
            String ligne;
            while ((ligne = reader.readLine()) != null) {
                String[] data = ligne.split(";");
                ArrayList<String> lstThematique = new ArrayList<>();
                if (data.length > 1) {
                    for (int i = 1; i < data.length; i++) {
                        lstThematique.add(data[i].toUpperCase());
                    }
                } else {
                    lstThematique.add(data[1].toUpperCase());
                }
                lstEvenement.add(new Evenement(data[0], lstThematique));
            }
            reader.close();
            return lstEvenement;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<String[]> listeFiliere() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(FILENAME_Filiere));
            List<String[]> lstFiliere = new ArrayList<>();
            String ligne;
            while ((ligne = reader.readLine()) != null) {
                String[] data = ligne.split(";");
                lstFiliere.add(data);
            }
            reader.close();
            return lstFiliere;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


}
