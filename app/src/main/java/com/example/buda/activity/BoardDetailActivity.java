package com.example.buda.activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.buda.BuildConfig;
import com.example.buda.R;
import com.example.buda.adapter.AdapterListComment;
import com.example.buda.http.HttpService;
import com.example.buda.http.RetrofitClient;
import com.example.buda.model.Board;
import com.example.buda.model.Buda;
import com.example.buda.model.Comment;
import com.example.buda.model.User;
import com.example.buda.utils.Tools;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.util.Objects;

import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BoardDetailActivity extends AppCompatActivity {
    private final String TAG = "BudaDetailActivity";
    public static final int LOGIN = 1001;
    private Realm mRealm;
    private Board mBoard;
    private HttpService mHttpService;
    private AdapterListComment mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_detail);
        mRealm = Tools.initRealm(BoardDetailActivity.this);
        mHttpService = RetrofitClient.getHttpService(Tools.getLoginAuthKey(mRealm));
        initToolbar();
        getBoard();
        MobileAds.initialize(this, initializationStatus -> {
        });

        AdView adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        Tools.setSystemBarColor(this, R.color.colorPrimary);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }

    private void getBoard() {
        int boardId = getIntent().getIntExtra("boardId", 0);
        if (BuildConfig.DEBUG && boardId <= 0) {
            throw new AssertionError("buda Id는 0보다 커야함");
        }
        Call<Board> call = mHttpService.getBoard(boardId);
        call.enqueue(new Callback<Board>() {
            @Override
            public void onResponse(@NonNull Call<Board> call, @NonNull Response<Board> response) {
                if (response.isSuccessful()) {
                    mBoard = response.body();
                    initContent();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Board> call, @NonNull Throwable t) {

            }
        });
    }

    @SuppressLint("DefaultLocale")
    private void initContent() {
        TextView title = findViewById(R.id.title);
        TextView body = findViewById(R.id.body);
        TextView replyCount = findViewById(R.id.reply_count);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        ImageView saveBtn = findViewById(R.id.save_btn);
        title.setText(mBoard.title);
        body.setText(mBoard.body);
        if (mBoard.comments.size() > 0) {
            replyCount.setText(String.format("(%d)", mBoard.comments.size()));
        }
        mAdapter = new AdapterListComment(this, mBoard.comments, R.layout.item_comment);
        mAdapter.setOnDeleteBtnClickListener((obj, position) -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(BoardDetailActivity.this);
            builder.setMessage("댓글을 삭제 하시겠습니까?")
                    .setPositiveButton("확인", (dialog, id) -> {
                        Call<Void> call = mHttpService.deleteComment(obj.id);
                        call.enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {

                            }

                            @Override
                            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {

                            }
                        });
                        mBoard.comments.remove(obj);
                        mAdapter.notifyDataSetChanged();
                    })
                    .setNegativeButton("취소", (dialog, id) -> {
                        dialog.cancel();
                        // User cancelled the dialog
                    });
            builder.create().show();
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mAdapter);

//        saveBtn.setOnClickListener(v -> {
//            if (!Tools.isLogin(mRealm)) {
//                Toast.makeText(BoardDetailActivity.this, "로그인이 필요한 서비스입니다.", Toast.LENGTH_SHORT).show();
//                goToLogin();
//                return;
//            }
//            saveComment();
//        });
    }

    private void goToLogin() {
        Intent intent = new Intent(BoardDetailActivity.this, LoginActivity.class);
        startActivityForResult(intent, LOGIN);
    }

    private void saveComment() {
        EditText editText = findViewById(R.id.text_content);
        String s = editText.getText().toString();
        if(s.length() == 0) {
            Toast.makeText(BoardDetailActivity.this, "댓글을 작성해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        User user = mRealm.where(User.class).findAll().first();
        Call<Comment> call = mHttpService.createComment(user.username, mBoard.id, editText.getText().toString());
        call.enqueue(new Callback<Comment>() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onResponse(@NonNull Call<Comment> call, @NonNull Response<Comment> response) {
                if (response.isSuccessful()) {
                    Comment comment = response.body();
                    mBoard.comments.add(comment);
                    mAdapter.setItems(mBoard.comments);
                    mAdapter.notifyDataSetChanged();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                    editText.getText().clear();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Comment> call, @NonNull Throwable t) {
                Log.d(TAG, "dddd");
            }

        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}