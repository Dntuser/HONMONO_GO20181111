package com.websarva.wings.android.honmono_go;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class StoreInfoReceiver extends AsyncTask<String,String,String> {

    private StoreInformationActivity _storeInformaionActivity;
    //コンストラクタ、引数をフィールドに格納
    public StoreInfoReceiver(StoreInformationActivity storeInformationActivity) {
        super();
        _storeInformaionActivity = storeInformationActivity;
    }

    @Override
    public String doInBackground(String...value){
        //GooglePlacesAPIから取得したJSON文字列。店情報が格納されている
        String readDate = "";
        //URL作成
        String urlStr = "https://maps.googleapis.com/maps/api/place/details/json?placeid=" + value[0] + "&=name,rating,formatted_phone_number&language=ja&key=";
        Log.d("Search3","URL:"+ urlStr);
        //Http接続を行う
        HttpURLConnection con = null;
        try {
            URL url = new URL(urlStr);
            //URLオブジェクトからHttpURLConnectionobjectを取得
            con = (HttpURLConnection)url.openConnection();
            //Http接続メソッドを設定
            con.setRequestMethod("POST");
            // URL接続からデータを読み取る場合はtrue
            con.setDoInput(true);
            //接続
            con.connect();
            //HttpURLConnectionnオブジェクトからレスポンスデータを取得
            InputStream is = con.getInputStream();
            //レスポンスデータであるInputStreamオブジェクトを文字列に変換
            readDate = is2String(is);
        }
        catch (MalformedURLException ex){
            ex.printStackTrace();
        }
        catch (IOException ex){
            ex.printStackTrace();
        }
        //JSON文字列を返す
        return readDate;
    }

    //取得したJSON文字列を解析して画面に表示
    @Override
    public void onPostExecute(String result){
        //JSONを解析
        _storeInformaionActivity.resultJSON(result);
        Log.d("Search1","けっか:"+ result);
    }

    //InputStreamオブジェクトを文字列に変換するメソッド
    private String is2String(InputStream is)throws IOException{
        BufferedReader reader =new BufferedReader(new InputStreamReader(is, "UTF-8"));
        StringBuffer sb = new StringBuffer();
        char[]b = new char[1024];
        int line;
        while (0 <=(line = reader.read(b))){
            sb.append(b, 0, line);
        }
        return  sb.toString();
    }
}

