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
import android.view.View;
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
import org.json.JSONException;
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

            mMap = googleMap;
            // 設定した緯度・経度を表示する
            mMap.setLocationSource(this);

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
        }
    }

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
        Log.d("Search1", "マーカー立てた:" + result);
    }

    //条件に一致する場所にピンを立てる
    public void setStorePin(String jsonData) {

        String store_place_id;
        String storeLat;
        String storeLng;
        final String[] storePlaceIdList = new String[4];
        final String[] storeLatList = new String[4];
        final String[] storeLngList = new String[4];

        if (jsonData.contains("ZERO_RESULTS")) {
            Toast.makeText(MapsActivity.this, "500m範囲以内に\n日高屋がありません。", Toast.LENGTH_LONG).show();
        } else {
            try {
                JSONObject jsonObject = new JSONObject(jsonData);
                JSONArray shopList = jsonObject.getJSONArray("results");

                for (int i = 0; i < shopList.length(); i++) {
                    JSONObject jsonObject_store = shopList.getJSONObject(i);
                    JSONObject location = jsonObject_store.getJSONObject("geometry").getJSONObject("location");

                    //店舗情報に表示するデータ
                    store_place_id = jsonObject_store.getString("place_id");
                    storeLat = location.getString("lat");
                    storeLng = location.getString("lng");
                    //取得したデータをリストに格納
                    storePlaceIdList[i] = store_place_id;
                    storeLatList[i] = storeLat;
                    storeLngList[i] = storeLng;

                    //マーカーインスタンス生成
                    MarkerOptions markerOptions = new MarkerOptions();
                    //店の位置(マーカーを表示する位置)
                    markerOptions.position(new LatLng(Double.parseDouble(storeLat), Double.parseDouble(storeLng)));
                    mMap.addMarker(markerOptions);

                    //マーカータップ時のイベントハンドラ取得
                    mMap.setOnMarkerClickListener(new OnMarkerClickListener() {

                        //マーカータップしたら、店舗情報をポップアップで表示する
                        @Override
                        public boolean onMarkerClick(Marker marker) {

                            //マーカーID
                            String marker_id = marker.getId();
                            Log.d("Search1", "マーカーID"  + marker_id)  ;
                            //マーカーIDごとにデータを分岐
                            switch (marker_id) {
                                case "m0":
                                    Intent intent0 = new Intent(MapsActivity.this, StoreInformationActivity.class);
                                    intent0.putExtra("store_place_id", storePlaceIdList[0]);
                                    intent0.putExtra("storeLat", storeLatList[0]);
                                    intent0.putExtra("storeLng", storeLngList[0]);
                                    startActivity(intent0);
                                    break;
                                case "m1":
                                    Intent intent1 = new Intent(MapsActivity.this, StoreInformationActivity.class);
                                    intent1.putExtra("store_place_id", storePlaceIdList[1]);
                                    intent1.putExtra("storeLat", storeLatList[1]);
                                    intent1.putExtra("storeLng", storeLngList[1]);
                                    startActivity(intent1);
                                    break;
                                case "m2":
                                    Intent intent2 = new Intent(MapsActivity.this, StoreInformationActivity.class);
                                    intent2.putExtra("store_place_id", storePlaceIdList[2]);
                                    intent2.putExtra("storeLat", storeLatList[2]);
                                    intent2.putExtra("storeLng", storeLngList[2]);
                                    startActivity(intent2);
                                    break;
                                case "m3":
                                    Intent intent3 = new Intent(MapsActivity.this, StoreInformationActivity.class);
                                    intent3.putExtra("store_place_id", storePlaceIdList[3]);
                                    intent3.putExtra("storeLat", storeLatList[3]);
                                    intent3.putExtra("storeLng", storeLngList[3]);
                                    startActivity(intent3);
                                    break;
                                case "m4":
                                    Intent intent4 = new Intent(MapsActivity.this, StoreInformationActivity.class);
                                    intent4.putExtra("store_place_id", storePlaceIdList[4]);
                                    intent4.putExtra("storeLat", storeLatList[4]);
                                    intent4.putExtra("storeLng", storeLngList[4]);
                                    startActivity(intent4);
                                    break;
                            }
                            return false;
                        }
                    });
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    //ヘッダーボタン押下
    public void onMenuButtonClick(View view){
        //メインメニュー画面に戻る
        Intent intent = new Intent(getApplication(),MenuActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}