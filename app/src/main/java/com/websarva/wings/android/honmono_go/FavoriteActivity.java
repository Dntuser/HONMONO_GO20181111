package com.websarva.wings.android.honmono_go;

import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FavoriteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        RecyclerView lvFavorite = findViewById(R.id.lvFavorite);
        LinearLayoutManager layout = new LinearLayoutManager(FavoriteActivity.this);
        lvFavorite.setLayoutManager(layout);
        RecyclerListAdapter adapter = new RecyclerListAdapter(this, createStoreList());
        lvFavorite.setAdapter(adapter);
        //区切り線用
        DividerItemDecoration decorator = new DividerItemDecoration(FavoriteActivity.this, layout.getOrientation());
        lvFavorite.addItemDecoration(decorator);
    }

    //データの中身たち(リスト)
    private List<StoreInfo>createStoreList(){
        DatabaseHelper helper = new DatabaseHelper(getApplicationContext());
        SQLiteDatabase db = helper.getWritableDatabase();

        // レコードの件数を確認
        long recordCount = DatabaseUtils.queryNumEntries(db,"storeTable");
        // レコードが存在しない場合
        if (recordCount == 0){
            Log.d("Search3","レコードカウント!:"+ recordCount);
            Toast.makeText(FavoriteActivity.this,"お気に入り登録されている\n店舗情報がありません。",Toast.LENGTH_SHORT).show();
            return null;
        }
        //1件以上
        //SQLの実行
        Cursor cursor = db.query(
                "storeTable",
                new String[]{"store_name","place_id","lat","lng"},
                null,
                null,
                null,
                null,
                "_id"+" DESC"
        );
        cursor.moveToFirst();

        List<Map<String,String>> favoriteList = new ArrayList<>();
        Map<String,String> favorite;
        for (int i = 0; i < cursor.getCount(); i++){
            favorite = new HashMap<>();
            favorite.put("store_name",(cursor.getString(0)));
            favoriteList.add(favorite);

            cursor.moveToNext();
        }
        cursor.close();

        List<StoreInfo>data = new ArrayList<>();
        int count = favoriteList.size();

        for (int i = 0; i < count; i++){
            StoreInfo info = new StoreInfo();
            info.setStoreName(favoriteList.get(i).get("store_name"));
            data.add(info);
        }
        return data;
    }

    //各アイテム(今回ならrow.xms)のレイアウトに合わせて作成する(今回ならTextView部分)
    private class RecyclerListViewHolder extends RecyclerView.ViewHolder {

        public TextView _tvStoreName;
        //コンストラクタ
        public RecyclerListViewHolder(View itemView) {
            super(itemView);
            _tvStoreName = itemView.findViewById(R.id.tvStoreName);
        }
    }

    //各アイテムにデータを割り当てる
    private class RecyclerListAdapter extends RecyclerView.Adapter<RecyclerListViewHolder>{
        //リストデータ保持用フィールド。リストデータをコンストラクタで受け取り、フィールドに保持する
        private List<StoreInfo> _listData;
        //コンストラクタ
        public RecyclerListAdapter(FavoriteActivity favoriteActivity, List<StoreInfo>listData){
            _listData = listData;
        }
        @Override
        public RecyclerListViewHolder onCreateViewHolder(ViewGroup parent,int viewType){
            View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.row, parent,false);
            RecyclerListViewHolder holder = new RecyclerListViewHolder(inflate);
            return holder;
        }

        //onCreateViewHolderで生成したビューホルダオブジェクトとアイテムのポジション番号が渡ってくる
        //必要なデータを取得し、ビューホルダ内の各フィールドの表示文字列として格納
        @Override
        public void onBindViewHolder(RecyclerListViewHolder holder,int position){
            //リストデータから該当1行分のデータを取得
            holder._tvStoreName.setText(_listData.get(position).getStoreName());
        }

        @Override
        public int getItemCount(){
            //リストデータ中の件数をリターン
            return  _listData.size();
        }
    }

    //データの内容
    private class StoreInfo{
        private String storeName;

        public String getStoreName(){
            return storeName;
        }
        public void setStoreName(String storeName){
            this.storeName = storeName;
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
