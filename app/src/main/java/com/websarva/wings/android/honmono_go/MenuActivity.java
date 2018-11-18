package com.websarva.wings.android.honmono_go;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MenuActivity extends AppCompatActivity {

    //緯度フィールド
    private double latitude = 0;
    //経度フィールド
    private double longitude = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        //LocationManagerオブジェクトを取得
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        //位置情報が更新された際のリスナオブジェクトを生成
        GPSLocationListener locationListener = new GPSLocationListener();
        //位置情報の追跡を開始
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // ACCESS_FINE_LOCATIONの許可を求めるダイアログを表示。その後、リクエストコードを1000に設定
            String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};
            ActivityCompat.requestPermissions(MenuActivity.this,permissions,1000);
            //onCreateのメソッドを終了
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    }

    private class GPSLocationListener implements LocationListener{

        @Override
        public void onLocationChanged(Location location){
            //引数のLocationオブジェクトから緯度を取得
            latitude = location.getLatitude();
            //引数のLocationオブジェクトから経度を取得
            longitude = location.getLongitude();
        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras){}

        @Override
        public void onProviderEnabled(String provider){}

        @Override
        public void onProviderDisabled(String provider){}
    }

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


/*

//SKCircle円を表示する機能を取得
    @Override
    public void onSingleTap(SKScreenPointt skScreenPoint) {
        SKCircle skCircle = getSKCircle(skScreenPoint);
        mapView.addCircle(skCircle);
    }

    private SKCircle getSKCircle(SKScreenPoint skScreenPoint) {
        SKCircle skCircle = new SKCircle();
        //IDを設定する
        skCircle.setIdentifier(1);
        //中心点を設定する
        skCircle.setCircleCenter(mapView.pointToCoordinate(skScreenPoint));
        //半径を設定する.単位はメートルです。
        skCircle.setRadius(100f);
        //外周円の色を設定する
        skCircle.setOutlineColor(new float[] { 1f, 0f, 0f, 1f });
        //円内の色を設定する
        skCircle.setColor(new float[] { 1f, 1f, 1f, 1f });
        //円周を破線表示にし、間隔の長さを指定する。
        skCircle.setOutlineDottedPixelsSkip(100);
        //円周を破線表示にし、線の長さを指定する。
        skCircle.setOutlineDottedPixelsSolid(100);
        //円の中心部を空白にする、割合で指定する。
        skCircle.setMaskedObjectScale(0.1f);
        //調査1000→パーミッションのコード？？
        skCircle.setNumberOfPoints(1000);
        return skCircle;
    }

    */

    public void onCurrentButtonClick(View view){
        //フィールドの緯度と経度の値をもとにマップアプリと連携するURL文字列を生成
        String urlStr = "geo:" + latitude + "," + longitude;
        //URL文字列からURLオブジェクトを生成
        Uri uri = Uri.parse(urlStr);
        //インテントオブジェクトを用意
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        //Intent intent = new Intent(MenuActivity.this, SearchResultActivity.class);
        //第二画面に送るデータ作成
        //intent.putExtra("uri", uri);
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
