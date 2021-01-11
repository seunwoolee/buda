package com.example.buda.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.buda.R;
import com.example.buda.activity.BudaDetailActivity;
import com.example.buda.adapter.AdapterListBudas;
import com.example.buda.http.HttpService;
import com.example.buda.http.RetrofitClient;
import com.example.buda.model.Buda;

import java.util.ArrayList;
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
    private RecyclerView mRecyclerView;
    private AdapterListBudas mAdapterListBudas;
    private LinearLayout mProgressBar;
    private boolean mIsLoading = false;

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
        mProgressBar = (LinearLayout) root_view.findViewById(R.id.lyt_progress);
        mRecyclerView = (RecyclerView) root_view.findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setHasFixedSize(true);

        mSwipeRefreshLayout = (SwipeRefreshLayout) root_view.findViewById(R.id.swipe_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getBudas();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        getBudas();
        initScrollListener();

        return root_view;
    }

    private void getBudas() {
        mProgressBar.setVisibility(View.VISIBLE);
        Call<List<Buda>> call = mHttpService.getBudas();
        call.enqueue(new Callback<List<Buda>>() {
            @Override
            public void onResponse(@NonNull Call<List<Buda>> call, @NonNull Response<List<Buda>> response) {
                if (response.isSuccessful()) {
                    mBudas = (ArrayList<Buda>) response.body();
                    mProgressBar.setVisibility(View.GONE);
                    mAdapterListBudas = new AdapterListBudas(mContext, mBudas, R.layout.item_dark_buda);
                    mAdapterListBudas.setOnItemClickListener(new AdapterListBudas.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, Buda obj, int position) {
                            Intent intent = new Intent(mContext, BudaDetailActivity.class);
                            intent.putExtra("budaId", obj.id);
                            startActivity(intent);
                        }
                    });

                    mRecyclerView.setAdapter(mAdapterListBudas);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Buda>> call, @NonNull Throwable t) {
                mProgressBar.setVisibility(View.GONE);
            }
        });
    }

    private void initScrollListener() {
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                if (!mIsLoading) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == mBudas.size() - 1) {
                        loadMore();
                        mIsLoading = true;
                    }
                }
            }
        });
    }

    private void loadMore() {
        mProgressBar.setVisibility(View.VISIBLE);
        Call<List<Buda>> call = mHttpService.getBudas();
        call.enqueue(new Callback<List<Buda>>() {
            @Override
            public void onResponse(@NonNull Call<List<Buda>> call, @NonNull Response<List<Buda>> response) {
                mProgressBar.setVisibility(View.GONE);

                if (response.isSuccessful()) {
                    ArrayList<Buda> budas = (ArrayList<Buda>) response.body();
                    mBudas.remove(mBudas.size() - 1);
                    int scrollPosition = mBudas.size();
                    mAdapterListBudas.notifyItemRemoved(scrollPosition);
                    assert budas != null: "loadMore budas array null 될 수 없음";
                    mBudas.addAll(budas);
                    mAdapterListBudas.notifyDataSetChanged();
                    mIsLoading = false;
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Buda>> call, @NonNull Throwable t) {
                mProgressBar.setVisibility(View.GONE);
            }
        });
    }
}