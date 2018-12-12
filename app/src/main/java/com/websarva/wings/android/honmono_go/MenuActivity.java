package com.websarva.wings.android.honmono_go;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;

public class MenuActivity extends AppCompatActivity {

    //緯度フィールド
    private double currentLatitude = 0;
    //経度フィールド
    private double currentLongitude = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        //LocationManagerオブジェクトを取得
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        //位置情報が更新された際のリスナオブジェクトを生成
        GPSLocationListener locationListener = new GPSLocationListener();
        //ACCESS_FINE_LOCATIONの許可が下りていない場合
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // ACCESS_FINE_LOCATIONの許可を求めるダイアログを表示。その後、リクエストコードを1000に設定
            String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};
            ActivityCompat.requestPermissions(MenuActivity.this,permissions,1000);
            //onCreateのメソッドを終了
            return;
        }
        //位置情報の追跡開始
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    }
    //位置情報が更新された際のリスナオブジェクトのメンバクラス
    private class GPSLocationListener implements LocationListener{

        @Override
        public void onLocationChanged(Location location){
            //引数のLocationオブジェクトから緯度を取得
            currentLatitude = location.getLatitude();
            //引数のLocationオブジェクトから経度を取得
            currentLongitude = location.getLongitude();
        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras){}

        @Override
        public void onProviderEnabled(String provider){}

        @Override
        public void onProviderDisabled(String provider){}
    }

    //パーミッションダイアログにて「許可」あるいは「許可しない」を選択したときの処理
    @Override
    public void onRequestPermissionsResult(int requestCode, String[]permissions, int[]grantResults){
        //パーミッションダイアログで許可を選択
        if(requestCode == 1000 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            //Locationオブジェクトを取得
            LocationManager locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
            //位置情報が更新された際のリスナオブジェクトを生成
            GPSLocationListener locationListener = new GPSLocationListener();
            //再度許可が下りていないかチェックし、下りていなければ処理を中止
            if(ActivityCompat.checkSelfPermission(MenuActivity.this,Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
                return;
            }
            //位置情報の追跡を開始
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
        }
    }

    public void onCurrentButtonClick(View view){

        String curLat = String.valueOf(currentLatitude);
        String curLng = String.valueOf(currentLongitude);
        //インテントオブジェクトを用意
        Intent intent = new Intent(MenuActivity.this, MapsActivity.class);
        intent.putExtra("currentLatitude",curLat);
        intent.putExtra("currentLongitude",curLng);
        intent.putExtra("page_param",1);
        Log.d("Search1","ここだよ:"+ curLat+ "&" +curLng);
        //現在地から検索アクティビティを起動
        startActivity(intent);
    }

    public void onFavoriteButtonClick(View view){
        //インテントオブジェクトを用意
        Intent intent = new Intent(MenuActivity.this, FavoriteActivity.class);
        //駅名から検索アクティビティを起動
        startActivity(intent);
    }
    public void onStationButtonClick(View view){
        //インテントオブジェクトを用意
        Intent intent = new Intent(MenuActivity.this, SearchFromStationActivity.class);
        //お気に入りから検索アクティビティを起動
        startActivity(intent);
    }
}
