package com.websarva.wings.android.honmono_go;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class FavoriteActivity extends AppCompatActivity {

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
