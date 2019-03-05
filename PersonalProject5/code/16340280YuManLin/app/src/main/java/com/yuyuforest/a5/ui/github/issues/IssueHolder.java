package com.yuyuforest.a5.ui.github.issues;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.yuyuforest.a5.R;
import com.yuyuforest.a5.model.github.IssueModel;

public class IssueHolder extends RecyclerView.ViewHolder {
    private TextView title;
    private TextView created;
    private TextView state;
    private TextView body;

    public IssueHolder(View view) {
        super(view);
        title = view.findViewById(R.id.title);
        created = view.findViewById(R.id.created);
        state = view.findViewById(R.id.state);
        body = view.findViewById(R.id.body);
    }

    public void setModel(Context context, IssueModel model) {
        title.setText(context.getString(R.string.issue_title, model.getTitle()));
        created.setText(context.getString(R.string.issue_create, model.getCreatedTime()));
        state.setText(context.getString(R.string.issue_state, model.getState()));
        body.setText(context.getString(R.string.issue_body, model.getBody()));
    }
}
