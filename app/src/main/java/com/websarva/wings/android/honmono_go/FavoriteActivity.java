package com.websarva.wings.android.honmono_go;

import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FavoriteActivity extends AppCompatActivity {

    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        Intent intent = getIntent();
        //画面品Listview
        listView = findViewById(R.id.list_view);
        readData();
    }
    private void readData(){
        DatabaseHelper helper = new DatabaseHelper(getApplicationContext());
        SQLiteDatabase db = helper.getWritableDatabase();

        // レコードの件数を確認
        long recordCount = DatabaseUtils.queryNumEntries(db,"storeTable");

        // レコードが存在しない場合
        if (recordCount == 0){
            Toast.makeText(FavoriteActivity.this,"お気に入りが登録されていません",Toast.LENGTH_SHORT).show();
            return;
        }

        Cursor cursor = db.query(
                "storeTable",
                new String[]{"store_name","place_id","lat","lng"},
                null,
                null,
                null,
                null,
                null
        );
        cursor.moveToFirst();

        List<Map<String,String>> favoriteList = new ArrayList<>();
        Map<String,String> favorite;
        favorite = new HashMap<>();

        for (int i = 0; i < cursor.getCount(); i++){
            favorite = new HashMap<>();
            favorite.put("store_name",(cursor.getString(0)));
            favoriteList.add(favorite);

            cursor.moveToNext();
        }
        cursor.close();
        String[] items;
        int count = favoriteList.size();
        items = new String[count];

        for (int i = 0; i < count; i++){
            items[i] = favoriteList.get(i).get("store_name");
        }

//        ListAdapter adapter = new ListAdapter(FavoriteActivity.this,R.layout.row,items);
//        listView.setAdapter(adapter);
//        listView.setOnItemClickListener(new ListItemClickListener());
    }


    //フッターボタン押下
    public void onMenuButtonClick(View view){
        //メインメニュー画面に戻る
        finish();
    }
}
