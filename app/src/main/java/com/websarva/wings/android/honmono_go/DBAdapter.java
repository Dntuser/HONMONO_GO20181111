package com.websarva.wings.android.honmono_go;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBAdapter extends SQLiteOpenHelper {
    private final static int DB_VERSION = 1;

    private final static String DB_NAME = "HONMONO_GO.db";
    private final static String DB_TABLE = "StoreTable";

    public final static String COL_ID = "id";
    public final static String COL_STORENAME = "store_name";
    public final static String COL_PLACEID = "place_id";
    public final static String COL_LAT = "latitude";
    public final static String COL_LNG = "longitude";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + DB_TABLE + " (" +
                    COL_ID + " INTEGER PRIMARY KEY, " +
                    COL_STORENAME + " TEXT, " +
                    COL_PLACEID + "TEXT, " +
                    COL_LAT + "REAL, " +
                    COL_LNG + "REAL)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + DB_TABLE;

    DBAdapter(Context context){
        super(context,DB_NAME,null,DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){

        db.execSQL(
                SQL_CREATE_ENTRIES
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion){
        db.execSQL(
                SQL_DELETE_ENTRIES
        );
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db,int oldVersion,int newVersion){
        onUpgrade(db,oldVersion,newVersion);
    }
}