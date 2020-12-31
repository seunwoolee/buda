package com.example.buda.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.buda.R;
import com.example.buda.adapter.AdapterListBudas;
import com.example.buda.adapter.AdapterListComment;
import com.example.buda.http.RetrofitClient;
import com.example.buda.model.Buda;
import com.example.buda.utils.Tools;

import org.w3c.dom.Text;

import java.util.Objects;

public class BudaDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buda_detail);
        initToolbar();
        initContent();
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }

    private void initContent() {
        Buda buda = getIntent().getParcelableExtra("buda");
        TextView title =  findViewById(R.id.title);
        ImageView photo =  findViewById(R.id.photo);
        TextView body =  findViewById(R.id.body);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        title.setText(buda.title);
        Tools.displayImageOriginal(this, photo, RetrofitClient.MEDIA_BASE_URL + buda.photo);
        body.setText(buda.body);

        AdapterListComment adapter = new AdapterListComment(this, buda.comments, R.layout.item_comment);
        recyclerView.setAdapter(adapter);

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
}