package com.yuyuforest.a5.ui.github.repos;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.yuyuforest.a5.R;
import com.yuyuforest.a5.model.github.RepoModel;

import java.util.ArrayList;

public class RepoAdapter extends RecyclerView.Adapter<RepoHolder> {
    private Context context;
    private ArrayList<RepoModel> data;
    private String username;

    public RepoAdapter(Context context) {
        this.context = context;
        this.data = new ArrayList<>();
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @NonNull
    @Override
    public RepoHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = View.inflate(context, R.layout.item_repo, null);
        return new RepoHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RepoHolder repoHolder, int pos) {
        repoHolder.setModel(context, username, data.get(pos));
    }

    public void newList(String username, ArrayList<RepoModel> data) {
        this.username = username;
        this.data = data;
        notifyDataSetChanged();
    }

    public void clear() {
        data.clear();
        notifyDataSetChanged();
    }
}
