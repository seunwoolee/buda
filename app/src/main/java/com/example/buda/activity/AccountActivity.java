package com.example.buda.activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.buda.R;
import com.example.buda.http.HttpService;
import com.example.buda.http.RetrofitClient;
import com.example.buda.model.Buda;
import com.example.buda.model.User;
import com.example.buda.utils.Tools;

import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account2);

        Realm realm = Tools.initRealm(this);
        User user = realm.where(User.class).findAll().first();

        EditText userEditText = findViewById(R.id.username);
        userEditText.setText(user.username);

        EditText nameEditText = findViewById(R.id.name);
        nameEditText.setText(user.name);

        HttpService httpService = RetrofitClient.getHttpService(Tools.getLoginAuthKey(realm));
        Button button = findViewById(R.id.bt_submit);
        button.setOnClickListener(v -> {
            String newName = nameEditText.getText().toString();
            Call<Void> call = httpService.changeName(newName);
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                    Toast.makeText(AccountActivity.this, "닉네임 변경 완료.", Toast.LENGTH_SHORT).show();
                    realm.beginTransaction();
                    user.name = newName;
                    realm.commitTransaction();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(nameEditText.getWindowToken(), 0);
                }

                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {

                }
            });
        });


    }
}