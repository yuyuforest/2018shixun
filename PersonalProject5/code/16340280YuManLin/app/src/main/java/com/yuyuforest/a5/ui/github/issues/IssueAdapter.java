package com.yuyuforest.a5.ui.github.issues;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.yuyuforest.a5.R;
import com.yuyuforest.a5.model.github.IssueModel;

import java.util.ArrayList;

public class IssueAdapter extends RecyclerView.Adapter<IssueHolder> {
    private Context context;
    private ArrayList<IssueModel> data;

    public IssueAdapter(Context context) {
        this.context = context;
        this.data = new ArrayList<>();
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @NonNull
    @Override
    public IssueHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = View.inflate(context, R.layout.item_issue, null);
        return new IssueHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IssueHolder issueHolder, int pos) {
        issueHolder.setModel(context, data.get(pos));
    }

    public void newList(ArrayList<IssueModel> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    public void add(IssueModel issueModel) {
        data.add(0, issueModel);
        notifyItemInserted(0);
    }
}
