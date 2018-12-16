package com.websarva.wings.android.honmono_go;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import static java.lang.Double.parseDouble;

import org.json.JSONArray;
import org.json.JSONObject;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
         GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener, GoogleMap.OnMyLocationButtonClickListener, LocationSource {
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest locationRequest;
    //緯度・経度を設定するLocationインスタンス
    private OnLocationChangedListener onLocationChangedListener = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // LocationRequest を生成して精度、インターバルを設定
        locationRequest = LocationRequest.create();
        //GoogleMapオブジェクト取得
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        //Googleが提供しているサービスに対して接続するためのクライアント
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                //.addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    // onResumeフェーズに入ったら接続
    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    // onPauseで切断
    @Override
    public void onPause() {
        super.onPause();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // パーミッションチェック
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
//                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//            Log.d("debug", "permission granted");

            mMap = googleMap;
            // 設定した緯度・経度を表示する
            mMap.setLocationSource(this);
//            mMap.setMyLocationEnabled(true);
//        } else {
//            Log.d("debug", "permission error");
//            return;
//        }

        //インテントオブジェクトを取得
        Intent intent = getIntent();
        //現在地から検索画面から渡された緯度と経度を取得
        String lat = intent.getStringExtra("currentLatitude");
        String lng = intent.getStringExtra("currentLongitude");

        //駅名から検索画面から渡された緯度と経度を取得
        String lat_s = intent.getStringExtra("stationLatitude");
        String lng_s = intent.getStringExtra("stationLongitude");

        //画面パラメータ：1現在地,2お気に入り,3駅名
        int activity_param = intent.getIntExtra("page_param", 0);

        //画面ごとの表示処理
        switch (activity_param) {
            //現在地
            case 1:
            // 中心地を指定した場所にする
            LatLng newLocation = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
            Log.d("Search1", "中心:" + newLocation);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(newLocation));
            zoomMap(Double.parseDouble(lat), Double.parseDouble(lng));
            // 円を描く
            mMap.addCircle(new CircleOptions()
                    .center(newLocation)
                    .radius(500)
                    .strokeColor(Color.RED));
            //GooglePlaceAPI取得用の非同期タスク
            final SearchStoreReceiver searchStoreReceiver = new SearchStoreReceiver(this);
            //クラスSearchStoreReceiverを実行
            searchStoreReceiver.execute(lat + "," + lng);
            Log.d("Search1", "現在地:" + lat + "&" + lng);
            break;

            //駅名
            case 3:
                // 中心地を指定した駅の場所にする
                LatLng stationLocation = new LatLng(Double.parseDouble(lat_s), Double.parseDouble(lng_s));
                Log.d("Search1", "中心:" + stationLocation);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(stationLocation));
                zoomMap(Double.parseDouble(lat_s), Double.parseDouble(lng_s));
                // 円を描く
                mMap.addCircle(new CircleOptions()
                        .center(stationLocation)
                        .radius(500)
                        .strokeColor(Color.RED));
                Log.d("Search1", "えきのえん:" + lat_s + "&" + lng_s);
                //GooglePlaceAPI取得用の非同期タスク
                final SearchStoreReceiver searchStoreReceiver_s = new SearchStoreReceiver(this);
                //クラスSearchStoreReceiverを実行
                searchStoreReceiver_s.execute(lat_s + "," + lng_s);
                Log.d("Search1", "えき:" + lat_s + "&" + lng_s);
                break;
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("debug", "onLocationChanged");
        if (onLocationChangedListener != null) {
            onLocationChangedListener.onLocationChanged(location);
//
//            //Intent intent = getIntent();
//            double lat = location.getLatitude();
//            double lng = location.getLongitude();
//
//            // 中心地を指定した場所にする
//            LatLng newLocation = new LatLng(lat, lng);
//            mMap.moveCamera(CameraUpdateFactory.newLatLng(newLocation));
//            zoomMap(lat,lng);
//
//            // 円を描く
//            mMap.addCircle(new CircleOptions()
//                    .center(newLocation)
//                    .radius(500)
//                    .strokeColor(Color.RED));
//
//            //GooglePlaceAPI取得用の非同期タスク
//            final SearchStoreReceiver searchStoreReceiver = new SearchStoreReceiver(this);
//            //String.valueOf(lat)
//            //String.valueOf(lng)
//            //クラスSearchStoreReceiverを実行
//            searchStoreReceiver.execute(String.valueOf(lat) + "," + String.valueOf(lng));
//            Log.d("Search1","現在地:"+ String.valueOf(lat)+ "&" + String.valueOf(lng));
        }
    }

//    @Override
//    public void onConnected(Bundle bundle) {
//        // check permission
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
//                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//            Log.d("debug", "permission granted");
//
//            // FusedLocationApi
//            LocationServices.FusedLocationApi.requestLocationUpdates(
//                    mGoogleApiClient, locationRequest, this);
//        } else {
//            Log.d("debug", "permission error");
//            return;
//        }
//    }

//    @Override
//    public void onConnectionSuspended(int i) {
//        Log.d("debug", "onConnectionSuspended");
//    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }

    //現在地ボタン押下時
    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    // OnLocationChangedListener calls activate() method
    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        this.onLocationChangedListener = onLocationChangedListener;
    }

    //無効化
    @Override
    public void deactivate() {
        this.onLocationChangedListener = null;
    }

    //ズーム
    private void zoomMap(double latitude, double longitude){
        // 表示する東西南北の緯度経度を設定
        double south = latitude * (1-0.00005);
        double west = longitude * (1-0.00005);
        double north = latitude * (1+0.00005);
        double east = longitude * (1+0.00005);

        // LatLngBounds (LatLng southwest, LatLng northeast)
        LatLngBounds bounds = LatLngBounds.builder()
                .include(new LatLng(south , west))
                .include(new LatLng(north, east))
                .build();

        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;

        // static CameraUpdate.newLatLngBounds(LatLngBounds bounds, int width, int height, int padding)
        mMap.moveCamera(CameraUpdateFactory.
                newLatLngBounds(bounds, width, height, 0));
    }
    //onPostExecuteで実行される関数
    public void resultJSON(String result){
        setStorePin(result);
    }

    //条件に一致する場所にピンを立てる
    public void setStorePin(String jsonData) {

        if (jsonData.contains("ZERO_RESULTS")) {
            Toast.makeText(MapsActivity.this, "500m範囲以内に\n日高屋がありません。", Toast.LENGTH_LONG).show();
        } else {
            try {
                JSONObject jsonObject = new JSONObject(jsonData);
                JSONArray shopList = jsonObject.getJSONArray("results");

                for (int i = 0; i < shopList.length(); i++) {
                    JSONObject jsonObject_store = shopList.getJSONObject(i);
                    JSONObject location = jsonObject_store.getJSONObject("geometry").getJSONObject("location");
                    JSONObject openingHours = jsonObject_store.getJSONObject("opening_hours");

                    //店舗情報に表示するデータ
                    final String store_name = jsonObject_store.getString("name");
                    final String store_vicinity = jsonObject_store.getString("vicinity");
                    final String store_place_id = jsonObject_store.getString("place_id");
                    final String store_openNow = openingHours.getString("open_now");
                    final String storeLat = location.getString("lat");
                    final String storeLng = location.getString("lng");

                    //マーカーインスタンス生成
                    MarkerOptions markerOptions = new MarkerOptions();
                    //店の位置(マーカーを表示する位置)
                    markerOptions.position(new LatLng(Double.parseDouble(storeLat), Double.parseDouble(storeLng)));
                    Marker marker = mMap.addMarker(markerOptions);
                    //表示する
                     marker.showInfoWindow();
                    Log.d("Search1", "店情報:" + i  + store_name)  ;

                    //マーカータップ時のイベントハンドラ取得
                    mMap.setOnMarkerClickListener(new OnMarkerClickListener() {

                        //マーカータップしたら、店舗情報をポップアップで表示する
                        @Override
                        public boolean onMarkerClick(Marker marker) {
                            Intent intent = new Intent(MapsActivity.this, StoreInformationActivity.class);
                            intent.putExtra("store_name", store_name);
                            intent.putExtra("store_vicinity", store_vicinity);
                            intent.putExtra("store_place_id", store_place_id);
                            intent.putExtra("store_openingHours", store_openNow);
                            intent.putExtra("storeLat", storeLat);
                            intent.putExtra("storeLng", storeLng);

                            startActivity(intent);
                            return false;
                        }
                    });
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}