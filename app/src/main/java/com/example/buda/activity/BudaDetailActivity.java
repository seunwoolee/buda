package com.example.buda.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.buda.BuildConfig;
import com.example.buda.R;
import com.example.buda.adapter.AdapterListBudas;
import com.example.buda.adapter.AdapterListComment;
import com.example.buda.http.HttpService;
import com.example.buda.http.RetrofitClient;
import com.example.buda.model.Buda;
import com.example.buda.model.Comment;
import com.example.buda.model.User;
import com.example.buda.utils.Tools;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.Objects;

import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BudaDetailActivity extends AppCompatActivity {
    private final String TAG = "BudaDetailActivity";
    public static final int LOGIN = 1001;
    private Realm mRealm;
    private User mUser;
    private Buda mBuda;
    private final HttpService mHttpService = RetrofitClient.getHttpService();
    private AdapterListComment mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buda_detail);
        mRealm = Tools.initRealm(BudaDetailActivity.this);
        initToolbar();
        getBuda();
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
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
                    mBuda = (Buda) response.body();
                    initContent();
                }
            }

            @Override
            public void onFailure(Call<Buda> call, Throwable t) {

            }

        });
    }

    private void initContent() {
        TextView title = findViewById(R.id.title);
        ImageView photo = findViewById(R.id.photo);
        TextView body = findViewById(R.id.body);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        ImageView saveBtn = findViewById(R.id.save_btn);
        title.setText(mBuda.title);
        Tools.displayImageOriginal(this, photo, RetrofitClient.MEDIA_BASE_URL + mBuda.photo);
        body.setText(mBuda.body);

        mAdapter = new AdapterListComment(this, mBuda.comments, R.layout.item_comment);
        mAdapter.setOnDeleteBtnClickListener(new AdapterListComment.OnDeleteBtnClickListener() {
            @Override
            public void onItemClick(Comment obj, int position) {
                AlertDialog.Builder builder = new AlertDialog.Builder(BudaDetailActivity.this);
                builder.setMessage("댓글을 삭제 하시겠습니까?")
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
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
                                mAdapter.notifyDataSetChanged();                            }
                        })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                // User cancelled the dialog
                            }
                        });
                builder.create().show();
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mAdapter);

        saveBtn.setOnClickListener(v -> {
            mUser = mRealm.where(User.class).findAll().first();
            if (mUser == null) {
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
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                break;
//            case R.id.action_save:
//                mRealm.beginTransaction();
//                mRealm.where(Blank.class)
//                        .equalTo("article.id", mArticle.getId())
//                        .findAll()
//                        .deleteAllFromRealm();
//                StringBuilder stringBuilder = new StringBuilder();
//
//                for (TextView view : mTextViews) {
//                    String word = mTextViewHelper.createBlank(view, mRealm, mArticle);
//                    stringBuilder.append(word);
//                }
//                mArticle.setContent(stringBuilder.toString());
//                mRealm.commitTransaction();
//                onBackPressed();
//                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void goToLogin() {
        Intent intent = new Intent(BudaDetailActivity.this, LoginActivity.class);
        startActivityForResult(intent, LOGIN);
    }

    private void saveComment() {
        EditText editText = findViewById(R.id.text_content);
        Call<Comment> call = mHttpService.createComment(mUser.username, mBuda.id, editText.getText().toString());
        call.enqueue(new Callback<Comment>() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onResponse(Call<Comment> call, Response<Comment> response) {
                if (response.isSuccessful()) {
                    Comment comment = (Comment) response.body();
                    mBuda.comments.add(comment);
                    mAdapter.setItems(mBuda.comments);
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                    editText.getText().clear();
                }
            }

            @Override
            public void onFailure(Call<Comment> call, Throwable t) {
                Log.d(TAG, "dddd");
            }

        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LOGIN) {
            if (resultCode == RESULT_OK) {
                mUser = mRealm.where(User.class).findAll().first();
                Toast.makeText(BudaDetailActivity.this, "로그인 완료", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(BudaDetailActivity.this, "로그인 실패", Toast.LENGTH_SHORT).show();
            }
        }
    }
}