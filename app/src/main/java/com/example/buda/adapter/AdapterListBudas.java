package com.example.buda.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.buda.R;
import com.example.buda.http.RetrofitClient;
import com.example.buda.model.Buda;
import com.example.buda.model.News;
import com.example.buda.utils.Tools;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class AdapterListBudas extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Buda> items = new ArrayList<>();

    private Context ctx;

    @LayoutRes
    private int layout_id;

    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, Buda obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public AdapterListBudas(Context context, List<Buda> items, @LayoutRes int layout_id) {
        this.items = items;
        ctx = context;
        this.layout_id = layout_id;
    }

    public static class OriginalViewHolder extends RecyclerView.ViewHolder {
        public ImageView photo;
        public TextView title;
        public TextView created;
        public View lyt_parent;

        public OriginalViewHolder(View v) {
            super(v);
            photo = v.findViewById(R.id.photo);
            title = v.findViewById(R.id.title);
            created = v.findViewById(R.id.created);
            lyt_parent = v.findViewById(R.id.lyt_parent);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(layout_id, parent, false);
        vh = new OriginalViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof OriginalViewHolder) {

            OriginalViewHolder view = (OriginalViewHolder) holder;
            @SuppressLint("SimpleDateFormat") DateFormat format = new SimpleDateFormat("yyyy년MM월dd일");
            Buda buda = items.get(position);
            view.title.setText(buda.title);
            view.created.setText(format.format(buda.created));
            Tools.displayImageOriginal(ctx, view.photo, RetrofitClient.MEDIA_BASE_URL + buda.photo);
            view.lyt_parent.setOnClickListener(view1 -> {
                if (mOnItemClickListener == null) return;
                mOnItemClickListener.onItemClick(view1, items.get(position), position);
            });
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

}