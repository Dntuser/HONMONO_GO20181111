package com.websarva.wings.android.honmono_go;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class StoreInformationActivity extends AppCompatActivity {

    private String store_name;
    private String store_vicinity;
    private String store_place_id;
    private String store_openingHours;
    private String storeLat;
    private String storeLng;
//    private SQLiteDatabase db;
//    private DatabaseHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_information);



        Intent intent = getIntent();
        store_name = intent.getStringExtra("store_name");
        store_vicinity = intent.getStringExtra("store_vicinity");
        store_place_id = intent.getStringExtra("store_place_id");
        store_openingHours = intent.getStringExtra("store_openingHours");
        storeLat = intent.getStringExtra("storeLat");
        storeLng = intent.getStringExtra("storeLng");

        //店名
        TextView tv_storeName = findViewById(R.id.store_name);
        tv_storeName.setText(store_name);
        //住所
        TextView tv_vicinity = findViewById(R.id.store_vicinity);
        tv_vicinity.setText(store_vicinity);
        //営業時間
        TextView tv_openingHour = findViewById(R.id.store_opening_hour);
        tv_openingHour.setText(store_openingHours);
        if (store_openingHours == "true"){
            tv_openingHour.setText("営業中");
        }else {
            tv_openingHour.setText("営業時間外");
        }
        Button insertButton = findViewById(R.id.bt_insert);
        InsertListener insertListener = new InsertListener();
        insertButton.setOnClickListener(insertListener);
}
private class InsertListener implements View.OnClickListener{
        @Override
        public void onClick(View view){

            DatabaseHelper helper = new DatabaseHelper(getApplicationContext());
            SQLiteDatabase db = helper.getWritableDatabase();
            String storeName = store_name;
            String placeId = store_place_id;
            String lat = storeLat;
            String lng = storeLng;
            insertData(db, storeName, placeId, lat, lng);
            finish();
        }
    }
    private void insertData(SQLiteDatabase db,String storeName,String placeId,String lat,String lng){
        ContentValues values = new ContentValues();
        values.put("store_name", storeName);
        values.put("place_id",placeId);
        values.put("lat", lat);
        values.put("lng", lng);
        try {
        db.insert("storeTable",null,values);
        } finally {
            db.close();
        }
    }
}
