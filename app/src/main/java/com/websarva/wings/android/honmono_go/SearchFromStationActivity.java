package com.websarva.wings.android.honmono_go;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class SearchFromStationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_from_station);
    }
    public void onSearchButton(View view) {
        //インテントオブジェクトを用意(検索結果画面がないため保留→mapが開いたらactivity遷移できない？)
        //Intent intent = new Intent(SearchFromStationActivity.this, SearchResultActivity.class);
        //お気に入りから検索アクティビティを起動
        //startActivity(intent);
    }
    public void onMenuButtonClick(View view){
        //メインメニュー画面に戻る
        finish();
    }
}
