package com.example.buda.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.buda.BuildConfig;
import com.example.buda.R;
import com.example.buda.adapter.AdapterListComment;
import com.example.buda.data.CommentEnum;
import com.example.buda.http.HttpService;
import com.example.buda.http.RetrofitClient;
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

public class BudaDetailActivity extends AppCompatActivity {
    private final String TAG = "BudaDetailActivity";
    public static final int LOGIN = 1001;
    private Realm mRealm;
    private Buda mBuda;
    private HttpService mHttpService;
    private AdapterListComment mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buda_detail);
        mRealm = Tools.initRealm(BudaDetailActivity.this);
        mHttpService = RetrofitClient.getHttpService(Tools.getLoginAuthKey(mRealm));
        initToolbar();
        getBuda();
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
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }

    private void getBuda() {
        int budaId = getIntent().getIntExtra("budaId", 0);
        if (BuildConfig.DEBUG && budaId <= 0) {
            throw new AssertionError("buda Id는 0보다 커야함");
        }
        Call<Buda> call = mHttpService.getBuda(budaId);
        call.enqueue(new Callback<Buda>() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onResponse(@NonNull Call<Buda> call, @NonNull Response<Buda> response) {
                if (response.isSuccessful()) {
                    mBuda = response.body();
                    initContent();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Buda> call, @NonNull Throwable t) {

            }

        });
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void setLikeBtn(Button likeBtn, int isLike) {
        Drawable img;
        if (isLike == 1) {
            img = this.getResources().getDrawable(R.drawable.ic_favorites);
        } else {
            img = this.getResources().getDrawable(R.drawable.ic_favorite_border);
        }
        img.setBounds(0, 0, 60, 60);
        likeBtn.setCompoundDrawables(img, null, null, null);

    }

    @SuppressLint("DefaultLocale")
    private void initContent() {
        TextView title = findViewById(R.id.title);
        ImageView photo = findViewById(R.id.photo);
        TextView body = findViewById(R.id.body);
        TextView replyCount = findViewById(R.id.reply_count);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        ImageView saveBtn = findViewById(R.id.save_btn);
        Button likeBtn = findViewById(R.id.like_btn);
        setLikeBtn(likeBtn, mBuda.isLike);
        title.setText(mBuda.title);
        Tools.displayImageOriginal(this, photo, RetrofitClient.MEDIA_BASE_URL + mBuda.photo);
        body.setText(mBuda.body);
        if (mBuda.comments.size() > 0) {
            replyCount.setText(String.format("(%d)", mBuda.comments.size()));
        }
        likeBtn.setOnClickListener(v -> {
            if (!Tools.isLogin(mRealm)) {
                Toast.makeText(BudaDetailActivity.this, "로그인이 필요한 서비스입니다.", Toast.LENGTH_SHORT).show();
                goToLogin();
                return;
            }

            User user = mRealm.where(User.class).findAll().first();
            Call<Void> call = mHttpService.createOrDeleteLike(user.username, mBuda.id);
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {

                }

                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {

                }
            });
            mBuda.isLike = mBuda.isLike == 1 ? 0 : 1;
            setLikeBtn(likeBtn, mBuda.isLike);
        });

        mAdapter = new AdapterListComment(this, mBuda.comments, R.layout.item_comment);
        mAdapter.setOnDeleteBtnClickListener((obj, position) -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(BudaDetailActivity.this);
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
                        mBuda.comments.remove(obj);
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

        saveBtn.setOnClickListener(v -> {
            if (!Tools.isLogin(mRealm)) {
                Toast.makeText(BudaDetailActivity.this, "로그인이 필요한 서비스입니다.", Toast.LENGTH_SHORT).show();
                goToLogin();
                return;
            }
            saveComment();
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_basic, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void goToLogin() {
        Intent intent = new Intent(BudaDetailActivity.this, LoginActivity.class);
        startActivityForResult(intent, LOGIN);
    }

    private void saveComment() {
        EditText editText = findViewById(R.id.text_content);
        String s = editText.getText().toString();
        if (s.length() == 0) {
            Toast.makeText(BudaDetailActivity.this, "댓글을 작성해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        mHttpService = RetrofitClient.getHttpService(Tools.getLoginAuthKey(mRealm));
        Call<Comment> call = mHttpService.createComment(mBuda.id, editText.getText().toString(), CommentEnum.BUDA);
        call.enqueue(new Callback<Comment>() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onResponse(@NonNull Call<Comment> call, @NonNull Response<Comment> response) {
                if (response.isSuccessful()) {
                    Comment comment = response.body();
                    mBuda.comments.add(comment);
                    mAdapter.setItems(mBuda.comments);
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LOGIN) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(BudaDetailActivity.this, "로그인 완료", Toast.LENGTH_SHORT).show();
            }
        }
    }
}