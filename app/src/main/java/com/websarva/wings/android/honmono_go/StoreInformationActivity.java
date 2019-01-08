package com.websarva.wings.android.honmono_go;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class StoreInformationActivity extends AppCompatActivity {

    private String Name;
    private String store_place_id;
    private String storeLat;
    private String storeLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_information);

        Intent intent = getIntent();
        store_place_id = intent.getStringExtra("store_place_id");
        storeLat = intent.getStringExtra("storeLat");
        storeLng = intent.getStringExtra("storeLng");

        //GooglePlaceAPI取得用の非同期タスク
        final StoreInfoReceiver storeInfoReceiver = new StoreInfoReceiver(this);
        //クラスStoreInfoReceiverを実行
        storeInfoReceiver.execute(store_place_id);
        Log.d("Search1", "プレイスID:" + store_place_id);

        //登録ボタン
        Button insertButton = findViewById(R.id.bt_insert);
        InsertListener insertListener = new InsertListener();
        insertButton.setOnClickListener(insertListener);

        //☆と★
        ImageView view = findViewById(R.id.select_button);
    }
    //onPostExecuteで実行される関数
    public void resultJSON(String result){
        displayStoreInfo(result);
        Log.d("Search1", "みせけっか:" + result);
    }

    //取得したJSONから店情報をパース
    public void displayStoreInfo(String jsonData) {
            try {
                    JSONObject jsonObject = new JSONObject(jsonData);
                    JSONObject resultJSON = jsonObject.getJSONObject("result");

                    //店舗情報に表示するデータ
                    String store_name = resultJSON.getString("name");
                    String store_vicinity = resultJSON.getString("formatted_address");
                    String store_phone_number = resultJSON.getString("formatted_phone_number");
                    String store_openingHours = resultJSON.getJSONObject("opening_hours").getString("open_now");
                    Log.d("Search1", "とれてるん？:" + store_name);

                    //店名
                    Name = store_name;
                    TextView tv_storeName = findViewById(R.id.store_name);
                    tv_storeName.setText(Name);
                    Log.d("Search1", "とれてるのか:" + Name);
                    //住所
                    TextView tv_vicinity = findViewById(R.id.store_vicinity);
                    tv_vicinity.setText(store_vicinity);
                    //電話番号
                    TextView tv_phone_number = findViewById(R.id.store_phone_number);
                    tv_phone_number.setText(store_phone_number);
                    //営業時間
                    TextView tv_openingHour = findViewById(R.id.store_opening_hour);
                    tv_openingHour.setText(store_openingHours);
                    if (store_openingHours == "true"){
                        Log.d("Search1", "なんなん:" + store_openingHours);
                        tv_openingHour.setText("営業中");
                    }else {
                        tv_openingHour.setText("営業時間外");
                    }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
    }

    //登録ボタン押下処理
    private class InsertListener implements View.OnClickListener{
        @Override
        public void onClick(View view){

            view.setSelected(true); // -> 黄色の星に切り替わる
            view.setSelected(false); // -> 無色の星に切り替わる

            DatabaseHelper helper = new DatabaseHelper(getApplicationContext());
            SQLiteDatabase db = helper.getWritableDatabase();
            String storeName = Name;
            String placeId = store_place_id;
            String lat = storeLat;
            String lng = storeLng;
            Log.i("Search", "登録したいデータ:" + storeName+ "&" + placeId +"&" + lat +"&" + lng);

            String sqlselect = "select store_name, place_id, lat, lng from storeTable where place_id ='" + placeId + "'";
            Cursor cursor = db.rawQuery(sqlselect, null);

            Log.i("Search", "カーソル:" + cursor);
            boolean next = cursor.moveToFirst();
            if (next) {
                Toast.makeText(StoreInformationActivity.this, "登録済店舗", Toast.LENGTH_LONG).show();
                finish();
            } else {
            insertData(db, storeName, placeId, lat, lng);
            finish();
            }
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
    //閉じるボタン押下
    public void onFinishButtonClick(View view){
        finish();
    }
}



