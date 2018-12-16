package com.websarva.wings.android.honmono_go;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class FavoriteListFragment extends Fragment {

    private Activity _parentActivity;
    //コンストラクタ
    public FavoriteListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        //所属するアクティビティオブジェクト取得
        _parentActivity = getActivity();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //フラグ円とで表示する画面部品をインフレートする
        View view = inflater.inflate(R.layout.fragment_favorite_list,container,false);
        //ListView lvFavorite = view.findViewById(R.id.favoriteList);
        //★後で消す

        List<Map<String,String>>favoriteList = new ArrayList<>();
        Map<String,String>favorite = new HashMap<>();
        favorite.put("name","からあげ");
        favorite.put("price","200円");
        favoriteList.add(favorite);
        favorite = new HashMap<>();
        favorite.put("name","ハンバーグ");
        favorite.put("price","300円");
        favoriteList.add(favorite);
        String[] from = {"name","price"};
        int[]to ={android.R.id.text1,android.R.id.text2};
        SimpleAdapter adapter = new SimpleAdapter(_parentActivity,favoriteList,android.R.layout.simple_list_item_2,from,to);
        //lvFavorite.setAdapter(adapter);
        //★ここまで消す

        return view;
    }

}
