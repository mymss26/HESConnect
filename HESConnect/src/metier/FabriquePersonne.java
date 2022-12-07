package metier;

import domaine.Personne;

public abstract class FabriquePersonne {

    public Personne nouvellePersonne(String... data){
        Personne p = null;
        p = newPersonne(data);
        return p;
    }

     abstract Personne newPersonne(String[] data);

}
