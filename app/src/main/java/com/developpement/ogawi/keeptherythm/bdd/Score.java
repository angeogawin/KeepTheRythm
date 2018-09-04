package com.developpement.ogawi.keeptherythm.bdd;

public class Score {
    private long id;
    private String niveau;
    private String valeur;

    public Score(long id, String niveau ,String valeur){
        this.setId(id);
        this.setNiveau(niveau);
        this.setValeur(valeur);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNiveau() {
        return niveau;
    }

    public void setNiveau(String niveau) {
        this.niveau = niveau;
    }

    public String getValeur() {
        return valeur;
    }

    public void setValeur(String valeur) {
        this.valeur = valeur;
    }
}
