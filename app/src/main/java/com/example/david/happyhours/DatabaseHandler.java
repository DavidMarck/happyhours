package com.example.david.happyhours;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Valentin on 30/04/2016.
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "happy_hours.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }


    // Création de la base
    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(BarDAO.CREATE_TABLE_BAR);
        database.execSQL(TypeBiereDAO.CREATE_TABLE_TYPE_BIERE);
        database.execSQL(BiereDAO.CREATE_TABLE_BIERE);
        database.execSQL(CarteDAO.CREATE_TABLE_CARTE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(DatabaseHandler.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL(BarDAO.DROP_TABLE_BAR);
        db.execSQL(TypeBiereDAO.DROP_TABLE_TYPE_BIERE);
        db.execSQL(BiereDAO.DROP_TABLE_BIERE);
        db.execSQL(CarteDAO.DROP_TABLE_CARTE);
        onCreate(db);
    }

}