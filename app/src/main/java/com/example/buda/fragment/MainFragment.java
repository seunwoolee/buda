package com.example.buda.fragment;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.buda.R;
import com.example.buda.adapter.AdapterListNews;
import com.example.buda.adapter.CustomerListAdapter;
import com.example.buda.adapter.OrderDetailAdapter;
import com.example.buda.data.DataGenerator;
import com.example.buda.http.HttpService;
import com.example.buda.model.News;
import com.example.buda.model.OrderDetail;
import com.example.buda.model.RouteD;
import com.google.android.material.snackbar.Snackbar;
import com.skt.Tmap.TMapTapi;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainFragment extends Fragment {
    private final String TAG = "MainFragment";
    private Context mContext;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup root_view = (ViewGroup) inflater.inflate(R.layout.fragment_main, container, false);

        RecyclerView recyclerView = (RecyclerView) root_view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.setHasFixedSize(true);

        List<News> items = DataGenerator.getNewsData(mContext, 10);

        //set data and list adapter
        AdapterListNews adapterListNews = new AdapterListNews(mContext, items, R.layout.item_news_light);
        recyclerView.setAdapter(adapterListNews);

        // on item list clicked
//        adapterListNews.setOnItemClickListener(new AdapterListNews.OnItemClickListener() {
//            @Override
//            public void onItemClick(View view, News obj, int position) {
//                Snackbar.make(parent_view, "Item " + obj.title + " clicked", Snackbar.LENGTH_SHORT).show();
//            }
//        });

        return root_view;
    }
}