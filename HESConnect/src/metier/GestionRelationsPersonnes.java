package metier;

import dao.Bdd;
import dao.Data;
import domaine.Filiere;
import domaine.Personne;
import org.neo4j.driver.Session;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class GestionRelationsPersonnes {

    public static void creerRelations(Session bdd){
        relationAssistantTravailHes(bdd);
        relationPersonneQuiEtudieHes(bdd);
        relationProfTravailHes(bdd);
        relationPersonnesAvecAutresPersonnes(bdd);
    }

    public static void relationPersonneQuiEtudieHes(Session bdd) {
        List<String[]> listFiliere = Data.listeFiliere();
        for (Personne p : Bdd.getListeEtudiant()) { // parcours les personnes
            List<String> l = new ArrayList<>(); // on créé une liste qui recuperera le nom des filiere correspodant à HEx
            assert listFiliere != null;
            for (String[] f:listFiliere){ // on parcours la liste des filiere entiere
                String nomEcole = f[0];
                if(nomEcole.equals(determineHES(p.getMail()))){ // si f[0] correspoect
                    l.add(f[1]); // on ajoute le nom de la filiere à notre liste
                }
            }
            Random randomFiliere = new Random();
                bdd.run("MATCH (etu:PERSONNE), (fi:FILIERE) WHERE etu.mail ='" + p.getMail() + "' AND fi.nom='"+l.get(randomFiliere.nextInt(l.size()))+"' CREATE (etu) -[:ETUDIE]-> (fi)");
        }
    }

    public static String determineHES(String mail){
        if(mail.contains("@heg.ch")){return "HEG"; }
        else if(mail.contains("@head.ch")){return "HEAD"; }
        else if(mail.contains("@heds.ch")){return "HEDS"; }
        else if(mail.contains("@hets.ch")){return "HETS"; }
        return null;
    }


    public static void relationProfTravailHes(Session bdd) {
        List<Personne> listProfesseursHEG = new ArrayList<>(); // 9 profs
        List<Personne> listProfesseursHEAD = new ArrayList<>(); // 9 profs
        List<Personne> listProfesseursHEDS = new ArrayList<>(); // 6 profs
        List<Personne> listProfesseursHETS = new ArrayList<>(); // 4 profs


        int compteurProf = 0;
        for (Personne p : Bdd.getListeProfesseurs()){
            if((listProfesseursHEG.isEmpty() || listProfesseursHEG.size() <= 9)){
                listProfesseursHEG.add(p);
                compteurProf++;
            } else if ((listProfesseursHEAD.isEmpty() || listProfesseursHEAD.size() <= 9)){
                listProfesseursHEAD.add(p);
                compteurProf++;
            }else if ((listProfesseursHEDS.isEmpty() || listProfesseursHEDS.size() <= 6)) {
                listProfesseursHEDS.add(p);
                compteurProf++;
            } else if ((listProfesseursHETS.isEmpty() || listProfesseursHETS.size() <= 4)){
                listProfesseursHETS.add(p);
                compteurProf++;
            }
        }
    }

    /**
     * Ici inclure les différentes filières
     * */
    public static void relationAssistantTravailHes(Session bdd) {

        List<Personne> listAssistantsHEG = new ArrayList<>(); //4 assistants
        List<Personne> listAssistantsHEAD = new ArrayList<>(); //3 assistants
        List<Personne> listAssistantsHEDS = new ArrayList<>(); //2 assistants
        List<Personne> listAssistantsHETS = new ArrayList<>(); //1 assistants

        int compteurAssistant = 0;
        for (Personne p : Bdd.getListeAssistant()) {
            if ((listAssistantsHEG.isEmpty() || listAssistantsHEG.size() <= 4)){
                listAssistantsHEG.add(p);
                compteurAssistant++;
            } else if ((listAssistantsHEAD.isEmpty() || listAssistantsHEAD.size() <= 3)){
                listAssistantsHEAD.add(p);
                compteurAssistant++;
            } else if ((listAssistantsHEDS.isEmpty() || listAssistantsHEDS.size() <= 2)){
                listAssistantsHEDS.add(p);
                compteurAssistant++;
            } else if ((listAssistantsHETS.isEmpty() || listAssistantsHETS.size() <= 1)){
                listAssistantsHETS.add(p);
                compteurAssistant++;
            }
        }

        for (Personne p : listAssistantsHEG) {
            bdd.run("MATCH (assis:PERSONNE), (hes:HES) WHERE assis.mail='" + p.getMail() + "' AND hes.nom='HEG' CREATE (assis) -[:TRAVAILLE]-> (hes)");
        }
        for (Personne p : listAssistantsHEAD) {
            bdd.run("MATCH (assis:PERSONNE), (hes:HES) WHERE assis.mail='" + p.getMail() + "' AND hes.nom='HEAD' CREATE (assis) -[:TRAVAILLE]-> (hes)");
        }
        for (Personne p : listAssistantsHEDS) {
            bdd.run("MATCH (assis:PERSONNE), (hes:HES) WHERE assis.mail='" + p.getMail() + "' AND hes.nom='HEDS' CREATE (assis) -[:TRAVAILLE]-> (hes)");
        }
        for (Personne p : listAssistantsHETS) {
            bdd.run("MATCH (assis:PERSONNE), (hes:HES) WHERE assis.mail='" + p.getMail() + "' AND hes.nom='HETS' CREATE (assis) -[:TRAVAILLE]-> (hes)");
        }
    }

    public static List<Personne> concatenerToutesLesListes() {
        List<Personne> allListe = new ArrayList<>();
        allListe.addAll(Bdd.getListeEtudiant());
        allListe.addAll(Bdd.getListeAssistant());
        allListe.addAll(Bdd.getListeProfesseurs());

        return allListe;
    }

    public static List<Personne> getRandomListeDePersonnes(int nbPersonnes, Random generator) {
        List<Personne> allListe = concatenerToutesLesListes();
        List<Personne> listeAleatoire = new ArrayList<>();
        for (int i = 0; i < nbPersonnes; i++) {
            int nb = generator.nextInt(allListe.size());
            listeAleatoire.add(allListe.get(nb));
            allListe.remove(nb);
        }
        return listeAleatoire;
    }

    public static void relationPersonnesAvecAutresPersonnes(Session bdd) {
        Random generator = new Random();
        for (int i = 0; i < concatenerToutesLesListes().size(); i++) {
            Personne personneCouranteDeLaListe = concatenerToutesLesListes().get(i);
            List<Personne> listeAleatoire = getRandomListeDePersonnes(generator.nextInt(15), generator); //je veux une liste avec un max de 15 personnes
            for (Personne personnesAleatoire : listeAleatoire) {
                bdd.run("MATCH (p1:PERSONNE), (p2:PERSONNE) WHERE p1.mail='" + personneCouranteDeLaListe.getMail() + "' AND p2.mail='" + personnesAleatoire.getMail() + "' CREATE (p1) -[:CONNAIT]-> (p2)");
                bdd.run("MATCH (p1:PERSONNE), (p2:PERSONNE) WHERE p1.mail='" + personneCouranteDeLaListe.getMail() + "' AND p2.mail='" + personnesAleatoire.getMail() + "' CREATE (p1) <-[:CONNAIT]- (p2)");
            }

        }

    }
}