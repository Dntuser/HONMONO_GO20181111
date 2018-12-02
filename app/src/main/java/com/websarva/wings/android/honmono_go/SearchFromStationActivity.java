package com.websarva.wings.android.honmono_go;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class SearchFromStationActivity extends AppCompatActivity implements View.OnClickListener{

    String stationLatitude = "";
    String stationLongitude = "";

    //都道府県格納用配列
    private ArrayList<Prefectures> PrefecturesList = new ArrayList<>();
    //路線の格納用配列
    private ArrayList<Line> LineList = new ArrayList<>();
    //駅の格納用配列
    private ArrayList<Station> StationList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_from_station);

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        //都道府県のSpinnerを設定
        Spinner spinnerPref = findViewById(R.id.spinner1);

        //都道府県を設定する
        setDataPref();
        ArrayAdapter<Prefectures> adapter = new ArrayAdapter<Prefectures>(this, android.R.layout.simple_spinner_dropdown_item, PrefecturesList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // 都道府県のspinner に adapter をセット
        spinnerPref.setAdapter(adapter);

        // リスナーを登録
        spinnerPref.setOnItemSelectedListener(new OnItemSelectedListener() {
            //　アイテムが選択された時
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Prefectures pref = (Prefectures) parent.getSelectedItem();
                String prefId = pref.getId();
                String prefName = pref.getName();
                //ここで路線一覧を取得する
                stationDate_receiver("都道府県" ,prefId);
            }

            //　アイテムが選択されなかった
            public void onNothingSelected(AdapterView<?> parent) {
                //
            }
        });

    //    findViewById(R.id.bt_search).setOnClickListener(this);
    }
    // 非同期処理を開始する
    private void stationDate_receiver(String fromSpinner,String Id){
        //タスクの生成は都度おこなうこと
        //AsyncTaskは１度しか実行できないので、使いまわさずに、必要な時に生成・実行させる
        //でないと、2度目に実行した際に、例外エラーが発生する
        //非同期タスクの生成
        final StationDateReceiver stationDateReceiver = new StationDateReceiver(this);
        //実行
        stationDateReceiver.execute(fromSpinner,Id);
    }

    //onPostExecuteで実行される関数
    public void result_job(String fromSpinner, String result){
        Log.d("result_job", fromSpinner + "：" + result);
        if(fromSpinner == "都道府県"){
            setDataLine(result);
        }else if(fromSpinner == "路線"){
            setDataStation(result);
        }
    }

    //路線スピナーを作成
    private void setDataLine(String dataLine) {
        try {
            JSONObject json = new JSONObject(dataLine);
            JSONArray lineList = json.getJSONArray("line");

            //都道府県のSpinnerを設定
            Spinner spinnerLine = findViewById(R.id.spinner2);

            ArrayAdapter<Line> lineAdapter = new ArrayAdapter<Line>(this, android.R.layout.simple_spinner_dropdown_item, LineList);
            lineAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            lineAdapter.clear();

            for (int i = 0; i < lineList.length(); i++) {
                JSONObject data = lineList.getJSONObject(i);
                LineList.add(new Line(data.getString("line_cd"), data.getString("line_name")));
            }

            // spinner に adapter をセット
            spinnerLine.setAdapter(lineAdapter);

            // リスナーを登録
            spinnerLine.setOnItemSelectedListener(new OnItemSelectedListener() {
                //　アイテムが選択された時
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    Line line = (Line) parent.getSelectedItem();
                    String lineId = line.getId();
                    String lineName = line.getName();
                    //ここで駅一覧を取得する
                    stationDate_receiver("路線", lineId);
                }
                //　アイテムが選択されなかった
                public void onNothingSelected(AdapterView<?> parent) {
                    //
                }
            });

        }catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //駅スピナーを作成
    private void setDataStation(String dataStation) {
        try {
            JSONObject json = new JSONObject(dataStation);
            JSONArray stationList = json.getJSONArray("station_l");

            //駅のSpinnerを設定
            Spinner spinnerStation = findViewById(R.id.spinner3);

            ArrayAdapter<Station> stationAdapter = new ArrayAdapter<Station>(this, android.R.layout.simple_spinner_dropdown_item, StationList);
            stationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            stationAdapter.clear();

            for (int i = 0; i < stationList.length(); i++) {
                JSONObject data = stationList.getJSONObject(i);
                StationList.add(new Station(data.getString("station_cd"), data.getString("station_name"), data.getString("lon"), data.getString("lat")));
            }
            // spinner に adapter をセット
            spinnerStation.setAdapter(stationAdapter);

            // リスナーを登録
            spinnerStation.setOnItemSelectedListener(new OnItemSelectedListener() {
                //　駅名は選択されたら変数名に格納するのみ
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    Station station = (Station) parent.getSelectedItem();
                    stationLatitude = station.getLat();
                    stationLongitude = station.getLon();
                }
                //　アイテムが選択されなかった
                public void onNothingSelected(AdapterView<?> parent) {
                    //
                }
            });
        }catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    //都道府県一覧を作成
    private void setDataPref() {
        //都道府県を追加する
        PrefecturesList.add(new Prefectures("0", "-----"));
        PrefecturesList.add(new Prefectures("1", "北海道"));
        PrefecturesList.add(new Prefectures("2", "青森県"));
        PrefecturesList.add(new Prefectures("3", "岩手県"));
        PrefecturesList.add(new Prefectures("4", "宮城県"));
        PrefecturesList.add(new Prefectures("5", "秋田県"));
        PrefecturesList.add(new Prefectures("6", "山形県"));
        PrefecturesList.add(new Prefectures("7", "福島県"));
        PrefecturesList.add(new Prefectures("8", "茨城県"));
        PrefecturesList.add(new Prefectures("9", "栃木県"));
        PrefecturesList.add(new Prefectures("10", "群馬県"));
        PrefecturesList.add(new Prefectures("11", "埼玉県"));
        PrefecturesList.add(new Prefectures("12", "千葉県"));
        PrefecturesList.add(new Prefectures("13", "東京都"));
        PrefecturesList.add(new Prefectures("14", "神奈川県"));
        PrefecturesList.add(new Prefectures("15", "新潟県"));
        PrefecturesList.add(new Prefectures("16", "富山県"));
        PrefecturesList.add(new Prefectures("17", "石川県"));
        PrefecturesList.add(new Prefectures("18", "福井県"));
        PrefecturesList.add(new Prefectures("19", "山梨県"));
        PrefecturesList.add(new Prefectures("20", "長野県"));
        PrefecturesList.add(new Prefectures("21", "岐阜県"));
        PrefecturesList.add(new Prefectures("22", "静岡県"));
        PrefecturesList.add(new Prefectures("23", "愛知県"));
        PrefecturesList.add(new Prefectures("24", "三重県"));
        PrefecturesList.add(new Prefectures("25", "滋賀県"));
        PrefecturesList.add(new Prefectures("26", "京都府"));
        PrefecturesList.add(new Prefectures("27", "大阪府"));
        PrefecturesList.add(new Prefectures("28", "兵庫県"));
        PrefecturesList.add(new Prefectures("29", "奈良県"));
        PrefecturesList.add(new Prefectures("30", "和歌山県"));
        PrefecturesList.add(new Prefectures("31", "鳥取県"));
        PrefecturesList.add(new Prefectures("32", "島根県"));
        PrefecturesList.add(new Prefectures("33", "岡山県"));
        PrefecturesList.add(new Prefectures("34", "広島県"));
        PrefecturesList.add(new Prefectures("35", "山口県"));
        PrefecturesList.add(new Prefectures("36", "徳島県"));
        PrefecturesList.add(new Prefectures("37", "香川県"));
        PrefecturesList.add(new Prefectures("38", "愛媛県"));
        PrefecturesList.add(new Prefectures("39", "高知県"));
        PrefecturesList.add(new Prefectures("40", "福岡県"));
        PrefecturesList.add(new Prefectures("41", "佐賀県"));
        PrefecturesList.add(new Prefectures("42", "長崎県"));
        PrefecturesList.add(new Prefectures("43", "熊本県"));
        PrefecturesList.add(new Prefectures("44", "大分県"));
        PrefecturesList.add(new Prefectures("45", "宮崎県"));
        PrefecturesList.add(new Prefectures("46", "鹿児島県"));
        PrefecturesList.add(new Prefectures("47", "沖縄県"));
    }

    public void onClick(View view) {

            //インテントオブジェクトを用意
            Intent intent = new Intent(SearchFromStationActivity.this, MapsActivity.class);
            //緯度経度格納
            intent.putExtra("stationLatitude",stationLatitude);
            intent.putExtra("stationLongitude",stationLongitude);
            startActivity(intent);
    }
    public void onMenuButtonClick(View view){
        //メインメニュー画面に戻る
        finish();
    }
}
