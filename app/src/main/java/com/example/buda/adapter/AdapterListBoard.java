package com.example.buda.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.buda.model.Board;
import com.example.buda.R;
import com.example.buda.model.Buda;

import java.util.ArrayList;
import java.util.List;

public class AdapterListBoard extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Board> items;

    private Context ctx;

    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(Board obj);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public AdapterListBoard(Context context, List<Board> items) {
        this.items = items;
        ctx = context;
    }

    public static class OriginalViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView content;
        public View lyt_parent;

        public OriginalViewHolder(View v) {
            super(v);
            title = (TextView) v.findViewById(R.id.title);
            content = (TextView) v.findViewById(R.id.content);
            lyt_parent = (View) v.findViewById(R.id.lyt_parent);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_people_chat, parent, false);
        vh = new OriginalViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof OriginalViewHolder) {
            OriginalViewHolder view = (OriginalViewHolder) holder;
            Board board = items.get(position);
            view.title.setText(board.title);
            view.content.setText(board.body);
            view.lyt_parent.setOnClickListener(view1 -> {
                if (mOnItemClickListener == null) return;
                mOnItemClickListener.onItemClick(items.get(position));
            });
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

}