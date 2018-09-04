package com.developpement.ogawi.keeptherythm.bdd;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class ScoreDAO extends DAOBase {
    public static  final String TABLE_NAME="scores";
    public static final String KEY="id";
    public static final String NIVEAU="niveau";
    public static  final String VALEUR="valeur";

    public ScoreDAO(Context pContext){
        super(pContext);
        this.open();

    }

    public void ajouter(Score s){
        ContentValues value = new ContentValues();
        value.put(ScoreDAO.NIVEAU,s.getNiveau());
        value.put(ScoreDAO.VALEUR,s.getValeur());

        getDb().insert(ScoreDAO.TABLE_NAME,null,value);


    }
    public void modifier(Score s){
        ContentValues value = new ContentValues();
        //vérification du score déjà enregistré pour le niveau
        String ancienScore=obtenirScoreNiveau(s.getNiveau());
        if(Integer.valueOf(ancienScore)<Integer.valueOf(s.getValeur())){
            //le nouveau score est supérieur donc on actualise la base des scores du joueur

            value.put(ScoreDAO.NIVEAU,s.getNiveau());
            value.put(ScoreDAO.VALEUR,s.getValeur());
            getDb().update(TABLE_NAME,value,NIVEAU+" = ?",new String[]{String.valueOf(s.getNiveau())});

        }




    }

    public String obtenirScoreNiveau(String niveau){
        String score="";
        Cursor c = getDb().rawQuery("select * from " + TABLE_NAME + " where niveau = ?", new String[]{niveau});
        while (c.moveToNext()) {
             score=c.getString(2);
        }
        c.close();

        return  score;
    }


    public long getIdMax() {
        long id=1;
        Cursor c = getDb().rawQuery("select " + KEY + " from " + TABLE_NAME+" where id >?", new String[]{"0"});

        while (c.moveToNext()) {

            id=c.getInt(0);

        }

        c.close();
        return id;


    }
}
