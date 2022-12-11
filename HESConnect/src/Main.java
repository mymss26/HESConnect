import dao.Bdd;
import dao.Data;
import domaine.HEG;
import domaine.HES;
import domaine.Personne;
import domaine.Prof;
import metier.Applic;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        new Applic();



        for(Personne p : Bdd.getListeEtudiant()){
            if(p.getMail().contains("@heg.ch")) {
                System.out.println(p);
            }
        }

/**
        for(HES e : Bdd.getListeEcoles()){
            //System.out.println(e);
        }


        List<Personne> listeProfesseurEtAssistant = new ArrayList<>();
        listeProfesseurEtAssistant.addAll(Bdd.getListeProfesseurs());
        listeProfesseurEtAssistant.addAll(Bdd.getListeAssistant());
        for(Personne p : listeProfesseurEtAssistant){
            if(p instanceof Prof){
                //System.out.println(p);
            }
            //System.out.println(p);
        }

        List<Personne> listProfesseursHEG = new ArrayList<>();
        List<Personne> listProfesseursHEAD = new ArrayList<>();
        List<Personne> listProfesseursHETS = new ArrayList<>();
        List<Personne> listProfesseursHEDS = new ArrayList<>();



        int compteur = 0;
        for (Personne p : Bdd.getListeProfesseurs()){
            if((listProfesseursHEG.isEmpty() || listProfesseursHEG.size() <= 15) && compteur<=14){
                listProfesseursHEG.add(p);
                compteur++;
            } else if ((listProfesseursHEAD.isEmpty() || listProfesseursHEAD.size() <= 15) && (compteur >= 15 && compteur <= 29)){
                listProfesseursHEAD.add(p);
                compteur++;
            }else if ((listProfesseursHEDS.isEmpty() || listProfesseursHEDS.size() <= 10) && (compteur >= 30 && compteur <= 39)) {
                listProfesseursHEDS.add(p);
                compteur++;
            } else if ((listProfesseursHETS.isEmpty() || listProfesseursHETS.size() <= 5) && (compteur >= 40 && compteur <= 45)){
                listProfesseursHETS.add(p);
                compteur++;
            }

        }

        List<Personne> listAssistantsHEG = new ArrayList<>();  //7 assistants pour 15 profs
        List<Personne> listAssistantsHEAD = new ArrayList<>(); //7 assistants pour 15 profs
        List<Personne> listAssistantsHEDS = new ArrayList<>(); //5 assistants pour 10 profs
        List<Personne> listAssistantssHETS = new ArrayList<>(); //3 assistants pour 5 profs

        int compteurAssistant = 0;
        for(Personne p : Bdd.getListeAssistant()){
            if((listAssistantsHEG.isEmpty() || listAssistantsHEG.size() <= 7) && compteurAssistant <=6){
                listAssistantsHEG.add(p);
                compteurAssistant++;
            } else if ((listAssistantsHEAD.isEmpty() || listAssistantsHEAD.size() <= 7) && (compteurAssistant >=7 && compteurAssistant <= 13)) {
                listAssistantsHEAD.add(p);
                compteurAssistant++;
            } else if ((listAssistantsHEDS.isEmpty() || listAssistantsHEDS.size() <= 5) && (compteurAssistant >= 14 && compteurAssistant <= 18)) {
                listAssistantsHEDS.add(p);
                compteurAssistant++;
            }else if((listAssistantssHETS.isEmpty() || listAssistantssHETS.size() <= 3) && (compteurAssistant >= 19 && compteurAssistant <= 22)){
                listAssistantssHETS.add(p);
                compteurAssistant++;
            }
        }
        //System.out.println(listAssistantsHEG.size());
        //System.out.println(listAssistantsHEAD.size());
        //System.out.println(listAssistantsHEDS.size());
        //System.out.println(listAssistantssHETS.size());

    */

    }
}