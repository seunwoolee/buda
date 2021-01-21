package com.example.buda.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.buda.R;
import com.example.buda.http.HttpService;
import com.example.buda.http.RetrofitClient;
import com.example.buda.model.User;
import com.example.buda.utils.Tools;

import java.util.Objects;

import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.buda.activity.BudaDetailActivity.LOGIN;

public class WriteActivity extends AppCompatActivity {
    private Realm mRealm;
    private AppCompatEditText mTitleEditText;
    private AppCompatEditText mBodyEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);

        mTitleEditText = findViewById(R.id.title);
        mBodyEditText = findViewById(R.id.body);

        initToolbar();
        mRealm = Tools.initRealm(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LOGIN) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(WriteActivity.this, "로그인 완료", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Tools.setSystemBarColor(this, R.color.colorPrimary);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_done, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else {

            if(!Tools.isLogin(mRealm)) {
                Toast.makeText(WriteActivity.this, "로그인이 필요한 서비스입니다.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(WriteActivity.this, LoginActivity.class);
                startActivityForResult(intent, LOGIN);
                return false;
            }

            if(Objects.requireNonNull(mTitleEditText.getText()).toString().length() == 0) {
                Toast.makeText(WriteActivity.this, "제목을 작성해주세요.", Toast.LENGTH_SHORT).show();
                return false;
            }

            if(Objects.requireNonNull(mBodyEditText.getText()).toString().length() == 0) {
                Toast.makeText(WriteActivity.this, "내용을 작성해주세요.", Toast.LENGTH_SHORT).show();
                return false;
            }

            HttpService httpService = RetrofitClient.getHttpService(Tools.getLoginAuthKey(mRealm));
            Call<Void> call = httpService.createBoard(Objects.requireNonNull(mTitleEditText.getText()).toString(), Objects.requireNonNull(mBodyEditText.getText()).toString());
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                    Toast.makeText(getApplicationContext(), "저장 완료", Toast.LENGTH_SHORT).show();
                    finish();
                }

                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                    Toast.makeText(getApplicationContext(), "에러발생 다시시도 해주세요.", Toast.LENGTH_SHORT).show();
                }
            });
        }
        return super.onOptionsItemSelected(item);
    }
}