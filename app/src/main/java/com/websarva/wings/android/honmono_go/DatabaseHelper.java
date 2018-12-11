package com.websarva.wings.android.honmono_go;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private final static int DB_VERSION = 1;
    private final static String DB_NAME = "HONMONO_GO.db";
    private final static String DB_TABLE = "StoreTable";

//    public final static String COL_ID = "_id";
//    public final static String COL_STORENAME = "store_name";
//    public final static String COL_PLACEID = "place_id";
//    public final static String COL_LAT = "lat";
//    public final static String COL_LNG = "lng";

    //テーブル作成
//    private static final String SQL_CREATE_ENTRIES =
//            "CREATE TABLE " + DB_TABLE + " (" +
//                    COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
//                    COL_STORENAME + " TEXT, " +
//                    COL_PLACEID + " TEXT, " +
//                    COL_LAT + " TEXT, " +
//                    COL_LNG + " TEXT)";

    //テーブルが存在しない場合でもエラーにならない
//    private static final String SQL_DELETE_ENTRIES =
//            "DROP TABLE IF EXISTS " + DB_TABLE;

    public DatabaseHelper(Context context){
        super(context,DB_NAME,null,DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE storeTable(");
        sb.append("_id INTEGER PRIMARY KEY AUTOINCREMENT,");
        sb.append("store_name TEXT,");
        sb.append("place_id TEXT,");
        sb.append("lat TEXT");
        sb.append("lng TEXT");
        sb.append(");");
        String sql = sb.toString();
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion){
        String deleteSQL = "DROP TABLE IF EXISTS HONMONO_GO";
        db.execSQL(deleteSQL);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db,int oldVersion,int newVersion){
        onUpgrade(db,oldVersion,newVersion);
    }
}
