package com.example.buda.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.buda.R;
import com.example.buda.model.User;
import com.example.buda.utils.Tools;

import io.realm.Realm;

public class MeActivity extends AppCompatActivity {
    private Realm mRealm = Tools.initRealm(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_me);

        LinearLayout logout_lyt = findViewById(R.id.logout_lyt);
        LinearLayout account_lyt = findViewById(R.id.account);
        LinearLayout like_lyt = findViewById(R.id.like_lyt);
        logout_lyt.setOnClickListener(v -> {
            Toast.makeText(MeActivity.this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
            mRealm.beginTransaction();
            mRealm.where(User.class).findAll().deleteAllFromRealm();
            mRealm.commitTransaction();
            finish();
        });

        account_lyt.setOnClickListener(v -> {
            Intent intent = new Intent(MeActivity.this, AccountActivity.class);
            startActivity(intent);
        });

        like_lyt.setOnClickListener(v -> {
            Intent intent = new Intent(MeActivity.this, LikeActivity.class);
            startActivity(intent);
        });



    }



}