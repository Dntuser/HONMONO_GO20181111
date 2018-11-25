package com.websarva.wings.android.honmono_go;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class AsyncJob extends AsyncTask<String,String,String> {
    private SearchFromStationActivity _searchFromStation;
    private String fromSpinner = "";

    public AsyncJob(SearchFromStationActivity searchFromStation) {
        super();
        _searchFromStation = searchFromStation;
    }
    @Override
    protected String doInBackground(String...value) {
        fromSpinner = value[0];
        String readSt = "";

        try {
            HttpURLConnection con = null;
            URL url = null;
            String urlSt = "";
            if(fromSpinner == "都道府県") {
                urlSt = "http://www.ekidata.jp/api/p/" + value[1] +".json";
            }else if(fromSpinner == "路線"){
                urlSt = "http://www.ekidata.jp/api/l/" + value[1] +".json";
            }

            // URLの作成
            url = new URL(urlSt);
            // 接続用HttpURLConnectionオブジェクト作成
            con = (HttpURLConnection)url.openConnection();
            // リクエストメソッドの設定
            con.setRequestMethod("POST");
            // リダイレクトを自動で許可しない設定
            con.setInstanceFollowRedirects(false);
            // URL接続からデータを読み取る場合はtrue
            con.setDoInput(true);

            // 接続
            con.connect();
            InputStream in = con.getInputStream();

            readSt = readInputStream(in);
            Log.d("JSONのデータ置換前", readSt);

            if(fromSpinner.equals("都道府県")) {
                readSt = readSt.replace("if(typeof(xml)=='undefined') xml = {};xml.data = ", "");
                readSt = readSt.replace("if(typeof(xml.onload)=='function') xml.onload(xml.data);", "");
                readSt = readSt.replace("\"line\"", "line");
                readSt = readSt.replace("\"line_cd\"", "line_cd");
                readSt = readSt.replace("\"line_name\"", "line_name");
                readSt = readSt.replace("line_cd:", "line_cd:\"");
                readSt = readSt.replace(",line_name:", "\",line_name:");
                Log.d("都道府県のデータ置換後", readSt);
            }else if(fromSpinner.equals("路線")){
                readSt = readSt.replace("if(typeof(xml)=='undefined') xml = {};xml.data = ", "");
                readSt = readSt.replace("if(typeof(xml.onload)=='function') xml.onload(xml.data);", "");
                readSt = readSt.replace("\"station_l\"", "station_l");
                readSt = readSt.replace("\"station_cd\"", "station_cd");
                readSt = readSt.replace("\"station_name\"", "station_name");
                Log.d("路線のデータ置換後", readSt);
            }

            //readSt = "{\"line\":[{\"line_cd\":\"11101\",\"line_name\":\"JR函館本線(函館～長万部)\"},{\"line_cd\":\"11102\",\"line_name\":\"JR函館本線(長万部～小樽)\"}]}";
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }catch (Exception ex) {
            ex.printStackTrace();
        }
        return readSt;
    }

    @Override
    protected void onProgressUpdate(String... values) {}

    @Override
    protected void onPostExecute(String result) {
        Log.d("取得したデータ", result);
        _searchFromStation.result_job(fromSpinner, result);
    }

    public String readInputStream(InputStream in) throws IOException {
        StringBuffer sb = new StringBuffer();
        String st = "";

        BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        while((st = br.readLine()) != null)
        {
            sb.append(st);
        }
        try
        {
            in.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return sb.toString();
    }
}
