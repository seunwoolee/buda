package com.example.buda.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.buda.R;
import com.example.buda.http.HttpService;
import com.example.buda.http.RetrofitClient;
import com.example.buda.model.User;
import com.example.buda.utils.SessionCallback;
import com.example.buda.utils.Tools;
import com.google.android.material.snackbar.Snackbar;
import com.kakao.auth.ApprovalType;
import com.kakao.auth.AuthType;
import com.kakao.auth.IApplicationConfig;
import com.kakao.auth.ISessionConfig;
import com.kakao.auth.KakaoAdapter;
import com.kakao.auth.KakaoSDK;
import com.kakao.auth.Session;
import com.kakao.usermgmt.LoginButton;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;

import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        LoginButton loginButton = findViewById(R.id.login_button);
        SessionCallback sessionCallback = new SessionCallback(this);
        Session session = Session.getCurrentSession();
        session.addCallback(sessionCallback);
        loginButton.setOnClickListener(view -> session.open(AuthType.KAKAO_TALK, LoginActivity.this));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // 카카오톡|스토리 간편로그인 실행 결과를 받아서 SDK로 전달
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            Log.d("main", "onActivityResult0");
            return;
        }
        Log.d("main", "onActivityResult1");

        super.onActivityResult(requestCode, resultCode, data);
    }
}