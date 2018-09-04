package com.developpement.ogawi.keeptherythm.bdd;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandler extends SQLiteOpenHelper {
    public static final String SCORE_KEY = "id";// contient l'identifiant du score
    public static final String SCORE_NIVEAU = "niveau";// contient le niveau où a été atteint le score
    public static final String SCORE_VALEUR = "valeur";// contient la valeur du score
    public static final String SCORE_TABLE_NAME = "scores";
    public static final String SCORE_TABLE_CREATE =
            "CREATE TABLE " + SCORE_TABLE_NAME + " (" +
                    SCORE_KEY + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    SCORE_NIVEAU + " TEXT, " +
                   SCORE_VALEUR+ " TEXT);";
    public static final String SCORE_TABLE_DROP = "DROP TABLE IF EXISTS " + SCORE_TABLE_NAME + ";";

    public DatabaseHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SCORE_TABLE_CREATE);
    }
    @Override

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL(SCORE_TABLE_DROP);

        onCreate(db);

    }
}

