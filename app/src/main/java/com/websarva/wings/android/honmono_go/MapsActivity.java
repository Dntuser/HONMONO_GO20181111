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

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
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
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener, GoogleMap.OnMyLocationButtonClickListener, LocationSource {

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest locationRequest;
    private int priority[] = {LocationRequest.PRIORITY_HIGH_ACCURACY, LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY,
            LocationRequest.PRIORITY_LOW_POWER, LocationRequest.PRIORITY_NO_POWER};
    private int locationPriority;
    //緯度・経度を設定するLocationインスタンス
    private OnLocationChangedListener onLocationChangedListener = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);


        // LocationRequest を生成して精度、インターバルを設定
        locationRequest = LocationRequest.create();

        //GoogleMapオブジェクト取得
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Googleが提供しているサービスに対して接続するためのクライアント
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        //インテントオブジェクトを取得
//        Intent intent = getIntent();
//        //駅名から検索画面から渡された緯度と経度を取得
//        String stationLatitude = intent.getStringExtra("stationLatitude");
//        String stationLongitude = intent.getStringExtra("stationLongitude");
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
        // check permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.d("debug", "permission granted");

            mMap = googleMap;
            // 設定した緯度・経度を表示する
            mMap.setLocationSource(this);
            mMap.setMyLocationEnabled(true);
            mMap.setOnMyLocationButtonClickListener(this);
        } else {
            Log.d("debug", "permission error");
            return;
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("debug", "onLocationChanged");
        if (onLocationChangedListener != null) {
            onLocationChangedListener.onLocationChanged(location);

            //Intent intent = getIntent();
            double lat = location.getLatitude();
            double lng = location.getLongitude();
//            String stationLatitude = intent.getStringExtra( "stationLatitude" );
//            String stationLongitude = intent.getStringExtra( "stationLongitude" );
//            Log.d("stationLatitude","駅の緯度");
//            double staLatitude = Double.parseDouble(stationLatitude);
//            double staLongitude = Double.parseDouble(stationLongitude);

            // 中心地を指定した場所にする
            LatLng newLocation = new LatLng(lat, lng);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(newLocation));
            zoomMap(lat,lng);

            // 円を描く
            mMap.addCircle(new CircleOptions()
                    .center(newLocation)
                    .radius(500)
                    .strokeColor(Color.RED));

            //GooglePlaceAPI取得用の非同期タスク
            final SearchStoreReceiver searchStoreReceiver = new SearchStoreReceiver(this);
            //String.valueOf(lat)
            //String.valueOf(lng)
            //クラスSearchStoreReceiverを実行
            searchStoreReceiver.execute(String.valueOf(lat) + "," + String.valueOf(lng));
            Log.d("Search1","現在地:"+ String.valueOf(lat)+ "&" + String.valueOf(lng));
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        // check permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.d("debug", "permission granted");

            // FusedLocationApi
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, locationRequest, this);
        } else {
            Log.d("debug", "permission error");
            return;
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d("debug", "onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d("debug", "onConnectionFailed");
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
    public void setStorePin(String jsonDate){
        try {
            JSONObject jsonObject = new JSONObject(jsonDate);
            JSONArray shopList = jsonObject.getJSONArray("results");

            for (int i = 0; i < shopList.length(); i++){
                JSONObject jsonObject_shop = shopList.getJSONObject(i);
                JSONObject location  = jsonObject_shop.getJSONObject("geomety").getJSONObject("location");

                final String shopName = jsonObject_shop.getString("name");
                final String lat = location.getString("lat");
                final String lng = location.getString("lng");

                MarkerOptions markerOptions = new MarkerOptions();
                //位置情報
                markerOptions.position(new LatLng(parseDouble(lat),parseDouble(lng)));
                //店名
                markerOptions.title(shopName);
                //住所
                markerOptions.snippet(jsonObject_shop.getString("vicinity"));
                Marker marker = mMap.addMarker(markerOptions);
                //表示する
                marker.showInfoWindow();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}