package com.example.buda.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.buda.R;
import com.example.buda.activity.AccountActivity;
import com.example.buda.activity.BudaDetailActivity;
import com.example.buda.activity.MeActivity;
import com.example.buda.activity.WriteActivity;
import com.example.buda.adapter.AdapterListBoard;
import com.example.buda.adapter.AdapterListBudas;
import com.example.buda.http.HttpService;
import com.example.buda.http.RetrofitClient;
import com.example.buda.model.Board;
import com.example.buda.model.Buda;
import com.example.buda.utils.Tools;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BoardFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private Context mContext;
    private HttpService mHttpService;
    private LinearLayout mProgressBar;
    private ArrayList<Board> mBoards = new ArrayList<>();
    private AdapterListBoard mAdapterListBoard;
    private boolean mIsLoading = false;
    private int mPage = 10;
    private final RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

            if (!mIsLoading) {
                if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == mBoards.size() - 1) {
                    loadMore();
                    mIsLoading = true;
                }
            }
        }
    };

    private void drawRecyclerView() {
        mProgressBar.setVisibility(View.GONE);
        mAdapterListBoard = new AdapterListBoard(mContext, mBoards);
        mRecyclerView.setAdapter(mAdapterListBoard);
    }

    private void getBoards() {
        mProgressBar.setVisibility(View.VISIBLE);
        Call<List<Board>> call = mHttpService.getBoards(mPage);
        call.enqueue(new Callback<List<Board>>() {
            @Override
            public void onResponse(@NonNull Call<List<Board>> call, @NonNull Response<List<Board>> response) {
                mBoards = (ArrayList<Board>) response.body();
                drawRecyclerView();
                mPage += 10;
            }

            @Override
            public void onFailure(@NonNull Call<List<Board>> call, @NonNull Throwable t) {
                mProgressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        mPage = 10;
        mRecyclerView.removeOnScrollListener(onScrollListener);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Realm realm = Tools.initRealm(getContext());
        mHttpService = RetrofitClient.getHttpService(Tools.getLoginAuthKey(realm));
//        initScrollListener();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup root_view = (ViewGroup) inflater.inflate(R.layout.fragment_board, container, false);
        mProgressBar = root_view.findViewById(R.id.lyt_progress);
        FloatingActionButton floatingActionButton = root_view.findViewById(R.id.fab_add);
        floatingActionButton.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, WriteActivity.class);
            startActivity(intent);
        });
        mRecyclerView = root_view.findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setHasFixedSize(true);

        if (mBoards.size() == 0) {
            getBoards();
        } else {
            drawRecyclerView();
        }

        return root_view;
    }


    private void initScrollListener() {
        mRecyclerView.addOnScrollListener(onScrollListener);
    }

    private void loadMore() {
        mProgressBar.setVisibility(View.VISIBLE);
        Call<List<Board>> call = mHttpService.getBoards(mPage);
        call.enqueue(new Callback<List<Board>>() {
            @Override
            public void onResponse(@NonNull Call<List<Board>> call, @NonNull Response<List<Board>> response) {
                ArrayList<Board> boards = (ArrayList<Board>) response.body();
                if (boards != null) {
                    mBoards.remove(mBoards.size() - 1);
                    int scrollPosition = mBoards.size();
                    mAdapterListBoard.notifyItemRemoved(scrollPosition);
                    mBoards.addAll(boards);
                    mAdapterListBoard.notifyDataSetChanged();
                    mIsLoading = false;
                    mPage += 10;
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Board>> call, @NonNull Throwable t) {

            }
        });
    }
}