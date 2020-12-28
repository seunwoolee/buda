package com.example.buda.fragment;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.buda.R;
import com.example.buda.adapter.AdapterListBudas;
import com.example.buda.adapter.AdapterListNews;
import com.example.buda.adapter.CustomerListAdapter;
import com.example.buda.adapter.OrderDetailAdapter;
import com.example.buda.data.DataGenerator;
import com.example.buda.http.HttpService;
import com.example.buda.http.RetrofitClient;
import com.example.buda.model.Buda;
import com.example.buda.model.News;
import com.example.buda.model.OrderDetail;
import com.example.buda.model.RouteD;
import com.google.android.material.snackbar.Snackbar;
import com.skt.Tmap.TMapTapi;

import org.checkerframework.checker.linear.qual.Linear;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainFragment extends Fragment {
    private final String TAG = "MainFragment";
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private Context mContext;
    private HttpService mHttpService;
    private ArrayList<Buda> mBudas;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHttpService = RetrofitClient.getHttpService();
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
        LinearLayout lyt_progress = (LinearLayout) root_view.findViewById(R.id.lyt_progress);
        Log.d(TAG, "onCreateView");

        //        lyt_progress.setVisibility(View.GONE);

        RecyclerView recyclerView = (RecyclerView) root_view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.setHasFixedSize(true);

        List<News> items = DataGenerator.getNewsData(mContext, 10);
        AdapterListNews adapterListNews = new AdapterListNews(mContext, items, R.layout.item_news_light);
        recyclerView.setAdapter(adapterListNews);

        mSwipeRefreshLayout = (SwipeRefreshLayout) root_view.findViewById(R.id.swipe_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getBudas(lyt_progress, recyclerView);
//                List<News> items = DataGenerator.getNewsData(mContext, 10);
//
//                //set data and list adapter
//                AdapterListNews adapterListNews = new AdapterListNews(mContext, items, R.layout.item_news_light);
//
//                recyclerView.setAdapter(adapterListNews);
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        getBudas(lyt_progress, recyclerView);
        // on item list clicked
//        adapterListNews.setOnItemClickListener(new AdapterListNews.OnItemClickListener() {
//            @Override
//            public void onItemClick(View view, News obj, int position) {
//                Snackbar.make(parent_view, "Item " + obj.title + " clicked", Snackbar.LENGTH_SHORT).show();
//            }
//        });

        return root_view;
    }

    private void getBudas(LinearLayout linearLayout, RecyclerView recyclerView) {
        linearLayout.setVisibility(View.VISIBLE);
        Call<List<Buda>> call = mHttpService.getBudas();
        call.enqueue(new Callback<List<Buda>>() {
            @Override
            public void onResponse(Call<List<Buda>> call, Response<List<Buda>> response) {
                if (response.isSuccessful()) {
                    mBudas = (ArrayList<Buda>) response.body();

                    linearLayout.setVisibility(View.GONE);
                    List<News> items = DataGenerator.getNewsData(mContext, 10);

                    AdapterListBudas adapterListBudas = new AdapterListBudas(mContext, mBudas, R.layout.item_buda);
                    recyclerView.setAdapter(adapterListBudas);
                }
            }

            @Override
            public void onFailure(Call<List<Buda>> call, Throwable t) {
                linearLayout.setVisibility(View.GONE);

            }
        });

    }
}