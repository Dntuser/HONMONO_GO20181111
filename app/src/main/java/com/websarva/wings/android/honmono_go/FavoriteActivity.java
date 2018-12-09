package com.websarva.wings.android.honmono_go;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

public class FavoriteActivity extends AppCompatActivity {

    private ListView listView;
    private SQLiteDatabase db;
    private DatabaseHelper helper;
    //lvStore.setOnItemClickListener(new ListItemClickListener());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        Intent intent = getIntent();
        //画面品Listview
        listView = findViewById(R.id.list_view);
  //      readDate();
    }

//    private void readDate(){
//
//        if(helper == null){
//            helper == new DatabaseHelper(getApplicationContext());
//        }
//        if (db == null){
//            db == helper.getReadableDatebase();
//        }
//
//        //レコード件数の確認
//        long recordCount = DatebaseUtils.queryNumEntries(db, "storeTable");
//        //レコードが存在しない場合
//        if(recordCount == 0){
//            Toast.makeText(FavoriteActivity.this,"お気に入りが登録されていません",Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        //レコードが存在する場合
//        Cursor cursor = db.query(
//                "StoreTable",
//                new String[]{"store_name","place_id","latitude","longitude"},
//                null,
//                null,
//                null,
//                null,
//                null
//        );
//        cursor.moveToFirst();
//    }
//    //ListAdapterで使用するListオブジェクトを用意
//    List<Map<String,String>>favoriteList = new ArrayList<>();
//    //「●●」のデータを格納するMapオブジェクトの用意とfavoriteListへのデータ登録
//    Map<String,String> favorite;
//    favorite = new HashMap<>();
//
//    //カーソルの数分データ登録を繰り返す
//    for(int i = 0; i< cursor.getCount(); i ++){
//        favorite = new HashMap<>();
//        favorite.put("store_name",(cursor.getString(0)));
//        favoriteList.add(favorite);
//        cursor.moveToNext();
//        }
//        cursor.close();
//
//
//    String[]items;
//    int count = favoriteList.size();
//    items[i] = favoriteList.get(i).get(i).get("store_name");
//
//    for (int i = 0; i < count; i++){
//        items[i] = favoriteList.get(i).get("store_name");
//    }
//    //ListAdapterを生成
//    ListAdapter adapter = new ListAdapter(FavoriteActivity.this,R.layout.row,items);
//    //アダプタの登録
//    ListView.setAdapter(adapter);
//    listView.setOnItemClickListener(new ListItemClickListener());
//    }

    //リストタップされたときのメンバクラス
//    private class ListItemClickListener implements AdapterView.OnItemClickListener{
//
//        @Override
//        public void onItemClick(AdapterView<?>parent, View view, int position, long id){
//            //タップされた行のデータ取得
//            Map<String,String>item = (Map<String, String>)parent.getItemAtPosition(position);
//            //を取得
//            String storeNsme = item.get("storeName");
//            String place_id = item.get("place_id");
//            //インテントオブジェクト生成
//            Intent intent = new Intent(FavoriteActivity.this, MapsActivity.class);
//            //MapsActivityに送るデータを格納
//            intent.putExtra("storeName", storeNsme);
//            intent.putExtra("place_id", place_id);
//            //MapsActivityへ
//            startActivity(intent);
//        }
//    }
    //フッターボタン押下
    public void onMenuButtonClick(View view){
        //メインメニュー画面に戻る
        finish();
    }
}
