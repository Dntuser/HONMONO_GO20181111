package com.websarva.wings.android.honmono_go;

import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

public class FavoriteActivity extends AppCompatActivity {

    private ListView listView;
    private SQLiteDatabase db;

    //private DBAdapter helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
    }
    public void onMenuButtonClick(View view){
        //メインメニュー画面に戻る
        finish();
    }
}
