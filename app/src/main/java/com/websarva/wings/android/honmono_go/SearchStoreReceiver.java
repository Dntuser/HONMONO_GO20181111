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

public class SearchStoreReceiver extends AsyncTask<String,String,String> {

    private MapsActivity _mapsActivity;
    //コンストラクタ、引数をフィールドに格納
    public SearchStoreReceiver(MapsActivity mapsActivity) {
        super();
        _mapsActivity = mapsActivity;
    }

    @Override
    public String doInBackground(String...params){

        //URL作成
        //String urlStr = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=\" + strings[0] + \"&radius=2000&key=AIzaSyDe1BHBQPPsAqa7Gjfx7DNji_QN4ft28gM";
        String urlStr = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=\" + strings[0] + \"&radius=2000&key=AIzaSyDe1BHBQPPsAqa7Gjfx7DNji_QN4ft28gM&keyword=日高屋";

        //GooglePlacesAPIから取得したJSON文字列。店情報が格納されている
        String result = "";

        //上記URL接続。JSON文字列を取得
        //Http接続を行う
        HttpURLConnection con = null;
        //Http接続のレスポンスデータを格納するInputStreamオブジェクト
        InputStream is = null;
        try {
            URL url = new URL(urlStr);
            //URLオブジェクトからHttpURLConnectionobjectを取得
            con = (HttpURLConnection)url.openConnection();
            //Http接続メソッドを設定
            con.setRequestMethod("POST");
            //接続
            con.connect();
            //HttpURLConnectionnオブジェクトからレスポンスデータを取得
            is = con.getInputStream();
            //レスポンスデータであるInputStreamオブジェクトを文字列に変換
            result = is2String(is);
        }
        catch (MalformedURLException ex){
        }
        catch (IOException ex){
        }
        finally {
            //HttpURLConnectionオブジェクトがnullでなければ解放
            if(con != null){
                con.disconnect();
            }
            //InputStreaamオブジェクトがnullでないなら開放
            if(con != null){
                try {
                    is.close();
                }
                catch (IOException ex){
                }
            }
        }
        //JSON文字列を返す
        return result;
    }

    //取得したJSON文字列を解析して画面に表示
    @Override
    public void onPostExecute(String result){
        //JSONを解析
        _mapsActivity.resultJSON(result);
        Log.d("Search1","渡せた？:"+ result);

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

