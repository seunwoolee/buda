package com.example.buda.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.buda.BuildConfig;
import com.example.buda.R;
import com.example.buda.fragment.MainFragment;
import com.example.buda.fragment.MapsFragment;
import com.example.buda.fragment.MapsFragment.IsAmButtonClicked;
import com.example.buda.http.HttpService;
import com.example.buda.http.RetrofitClient;
import com.example.buda.model.RouteD;
import com.example.buda.model.User;
import com.example.buda.utils.Tools;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.messaging.FirebaseMessaging;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "MainActivity";
    private static final String[] needPermissions = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
    };
    private static final int REQUEST_LOCATION_PERMISSION = 200;

    private TabLayout tab_layout;
    private DrawerLayout drawer;
    private Toolbar toolbar;
    private boolean isAm = true;

    private Realm mRealm;
    private long mPressedTime;
    private ArrayList<RouteD> mRouteDS;
    private HttpService mHttpService;
    private final Callback<List<RouteD>> callback = new Callback<List<RouteD>>() {
        @Override
        public void onResponse(Call<List<RouteD>> call, Response<List<RouteD>> response) {
            if (response.isSuccessful()) {
                mRouteDS = (ArrayList<RouteD>) response.body();
                initMapFragment();
            }
        }

        @Override
        public void onFailure(Call<List<RouteD>> call, Throwable t) {

        }
    };

    public void requestPermission() {
        for (String permission : needPermissions) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        this,
                        needPermissions,
                        REQUEST_LOCATION_PERMISSION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean permissionToRecordAccepted = true;

        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    permissionToRecordAccepted = false;
                    break;
                }
            }
        }

        if (!permissionToRecordAccepted) {
            Toast.makeText(MainActivity.this, "권한이 거부되었습니다. 권한을 승인해주세요.", Toast.LENGTH_LONG).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent();
                    intent.setAction(
                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package",
                            BuildConfig.APPLICATION_ID, null);
                    intent.setData(uri);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
            }, 1500);
        }
    }

    @Override
    public void onBackPressed() {
        if (mPressedTime == 0) {
            Toast.makeText(MainActivity.this, " 한 번 더 누르면 종료됩니다.", Toast.LENGTH_LONG).show();
            mPressedTime = System.currentTimeMillis();
        } else {
            int seconds = (int) (System.currentTimeMillis() - mPressedTime);

            if (seconds > 2000) {
                Toast.makeText(MainActivity.this, " 한 번 더 누르면 종료됩니다.", Toast.LENGTH_LONG).show();
                mPressedTime = 0;
            } else {
                super.onBackPressed();
                finish();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mHttpService = RetrofitClient.getHttpService();
        requestPermission();
        initToolbar();
        initComponent();
        initMainFragment();
        mRealm = Tools.initRealm(this);
    }

    private void goToLogin() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivityForResult(intent, 1000);
    }

    private void getHashKey() {
        PackageInfo packageInfo = null;
        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageInfo == null)
            Log.e("KeyHash", "KeyHash:null");

        for (Signature signature : packageInfo.signatures) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            } catch (NoSuchAlgorithmException e) {
                Log.e("KeyHash", "Unable to get MessageDigest. signature=" + signature, e);
            }
        }
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        toolbar.setNavigationIcon(R.drawable.ic_apps);
//        toolbar.getNavigationIcon().setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Tools.setSystemBarColor(this, android.R.color.white);
        Tools.setSystemBarLight(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        Tools.changeMenuIconColor(menu, Color.BLACK);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        } else if(id == R.id.action_me) {
            goToLogin();
        } else{
            Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void initComponent() {
        tab_layout = findViewById(R.id.tab_layout);
        LinearLayout tab0 = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.item_tab0, null);
        LinearLayout tab1 = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.item_tab1, null);

        tab_layout.addTab(tab_layout.newTab().setCustomView(tab0), 0);
        tab_layout.addTab(tab_layout.newTab().setCustomView(tab1), 1);

        ((ImageView) Objects.requireNonNull(tab_layout.getTabAt(0)).view.findViewById(R.id.nav_icon))
                .setColorFilter(getResources().getColor(R.color.quantum_black_100), PorterDuff.Mode.SRC_IN);
        ((ImageView) Objects.requireNonNull(tab_layout.getTabAt(1)).view.findViewById(R.id.nav_icon))
                .setColorFilter(getResources().getColor(R.color.grey_20), PorterDuff.Mode.SRC_IN);

        tab_layout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                switchFragment(position);
                ImageView imageView = (ImageView) tab.view.findViewById(R.id.nav_icon);
                imageView.setColorFilter(getResources().getColor(R.color.quantum_black_100), PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                ImageView imageView = (ImageView) tab.view.findViewById(R.id.nav_icon);
                imageView.setColorFilter(getResources().getColor(R.color.grey_20), PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    private void initMapFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        MapsFragment mapsFragment = getMapsFragment();
        Fragment fragment = fragmentManager.findFragmentByTag(MapsFragment.class.getName());
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        if (fragment != null) {
            transaction.replace(R.id.mainFragment, fragment, MapsFragment.class.getName()).commit();
            return;
        }

        transaction.add(R.id.mainFragment, mapsFragment, MapsFragment.class.getName()).addToBackStack(MapsFragment.class.getName()).commit();
    }


    private MapsFragment getMapsFragment() {
        MapsFragment mapsFragment = new MapsFragment(mHttpService);
        Bundle bundle = new Bundle();
        bundle.putBoolean("isAm", isAm);
        bundle.putParcelableArrayList("routeDs", mRouteDS);
        mapsFragment.setArguments(bundle);
        return mapsFragment;
    }


    private void initMainFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        MainFragment mainFragment = new MainFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("routeDs", mRouteDS);
        mainFragment.setArguments(bundle);
        Fragment fragment = fragmentManager.findFragmentByTag(MainFragment.class.getName());
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (fragment != null) {
            transaction.replace(R.id.mainFragment, fragment, MainFragment.class.getName()).commit();
            return;
        }

        transaction.add(R.id.mainFragment, mainFragment, MainFragment.class.getName()).addToBackStack(MainFragment.class.getName()).commit();
    }

    private void switchFragment(int position) {
        switch (position) {
            case 0:
                initMainFragment();
                break;
            case 1:
                initMapFragment();
                break;
        }
    }

}