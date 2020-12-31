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

import com.example.buda.R;
import com.example.buda.http.RetrofitClient;
import com.example.buda.model.Buda;
import com.example.buda.model.Comment;
import com.example.buda.utils.Tools;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class AdapterListComment extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Comment> items = new ArrayList<>();

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

    public AdapterListComment(Context context, List<Comment> items, @LayoutRes int layout_id) {
        this.items = items;
        ctx = context;
        this.layout_id = layout_id;
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        public ImageView photo;
        public TextView comment;
        public TextView created;
        public View lyt_parent;

        public OriginalViewHolder(View v) {
            super(v);
            photo = v.findViewById(R.id.photo);
            comment = v.findViewById(R.id.comment);
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
//            @SuppressLint("SimpleDateFormat") DateFormat format = new SimpleDateFormat("yyyy년MM월dd일");
            Comment comment = items.get(position);
            view.comment.setText(comment.comment);
//            view.created.setText(format.format(buda.created));
//            Tools.displayImageOriginal(ctx, view.photo, RetrofitClient.MEDIA_BASE_URL + buda.photo);
//            view.lyt_parent.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    if (mOnItemClickListener == null) return;
//                    mOnItemClickListener.onItemClick(view, items.get(position), position);
//                }
//            });
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

}