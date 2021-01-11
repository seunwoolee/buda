package com.example.buda.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.example.buda.R;
import com.example.buda.activity.BudaDetailActivity;
import com.example.buda.activity.LoginActivity;
import com.example.buda.adapter.AdapterListBudas;
import com.example.buda.http.HttpService;
import com.example.buda.http.RetrofitClient;
import com.example.buda.model.Buda;
import com.example.buda.model.User;
import com.kakao.auth.ISessionCallback;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.usermgmt.response.model.Profile;
import com.kakao.usermgmt.response.model.UserAccount;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.OptionalBoolean;
import com.kakao.util.exception.KakaoException;
import com.kakao.usermgmt.UserManagement;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.buda.utils.Tools.requestMe;

public class SessionCallback implements ISessionCallback {
    Activity mActivity;
    public SessionCallback(LoginActivity activity) {
        mActivity = activity;
    }

    @Override
    public void onSessionOpened() {
        Log.d("main", "onSessionOpened");
        requestMe(mActivity);
    }

    // 로그인에 실패한 상태
    @Override
    public void onSessionOpenFailed(KakaoException exception) {
        Log.e("SessionCallback :: ", "onSessionOpenFailed : " + exception.getMessage());
    }

    // 사용자 정보 요청
}
