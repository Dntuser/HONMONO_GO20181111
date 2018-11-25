package com.websarva.wings.android.honmono_go;


import android.Manifest;
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
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

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

        // 測位の精度、消費電力の優先度
        locationPriority = priority[1];

        if (locationPriority == priority[0]) {
            // 位置情報の精度を優先する場合
            locationRequest.setPriority(locationPriority);
            locationRequest.setInterval(5000);
            locationRequest.setFastestInterval(16);
        } else if (locationPriority == priority[1]) {
            // 消費電力を考慮する場合
            locationRequest.setPriority(locationPriority);
            locationRequest.setInterval(60000);
            locationRequest.setFastestInterval(16);
        } else if (locationPriority == priority[2]) {
            // "city" level accuracy
            locationRequest.setPriority(locationPriority);
        } else {
            // 外部からのトリガーでの測位のみ
            locationRequest.setPriority(locationPriority);
        }

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
            double lat = location.getLatitude();
            double lng = location.getLongitude();
            Log.d("debug", "location=" + lat + "," + lng);


            // 中心地を指定した場所にする
            LatLng newLocation = new LatLng(lat, lng);
            //mMap.addMarker(new MarkerOptions().position(newLocation).title("My Location"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(newLocation));
            zoomMap(lat,lng);

            // 円を描く
            mMap.addCircle(new CircleOptions()
                    .center(newLocation)
                    .radius(500)
                    .strokeColor(Color.RED));
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
}