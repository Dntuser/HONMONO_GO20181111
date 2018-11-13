package com.websarva.wings.android.honmono_go;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class SearchFromStationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_from_station);
    }
    public void onMenuButtonClick(View view){
        //メインメニュー画面に戻る
        finish();
    }
}
