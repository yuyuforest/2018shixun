package com.yuyuforest.a5.ui.github.repos;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.yuyuforest.a5.R;
import com.yuyuforest.a5.model.github.RepoModel;
import com.yuyuforest.a5.ui.github.issues.GithubIssuesActivity;
import com.yuyuforest.a5.utils.Utils;

public class RepoHolder extends RecyclerView.ViewHolder {
    private CardView card;
    private TextView name;
    private TextView id;
    private TextView issuesNum;
    private TextView description;
    private String reponame;

    public RepoHolder(View view) {
        super(view);
        card = view.findViewById(R.id.card);
        name = view.findViewById(R.id.name);
        id = view.findViewById(R.id.id);
        issuesNum = view.findViewById(R.id.issues_num);
        description = view.findViewById(R.id.description);
    }

    public void setModel(final Context context, final String username, final RepoModel model) {
        reponame = model.getName();
        name.setText(context.getString(R.string.repo_name, reponame));
        id.setText(context.getString(R.string.repo_id, model.getId()));
        issuesNum.setText(context.getString(R.string.issues_num, model.getOpenIssuesNum()));
        description.setText(context.getString(R.string.repo_description, model.getDescription() == null ? "" : model.getDescription()));
        card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(model.isFork()) {
                    Utils.toast(context, "该仓库fork自其它仓库，无法查看或创建issues");
                }
                else {
                    Intent intent = new Intent(context, GithubIssuesActivity.class);
                    intent.putExtra("username", username);
                    intent.putExtra("reponame", reponame);
                    context.startActivity(intent);
                }
            }
        });
    }
}