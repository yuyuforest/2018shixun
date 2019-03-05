package com.yuyuforest.a5.ui.github.issues;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.yuyuforest.a5.R;

public class GithubIssuesActivity extends AppCompatActivity {

    private GithubIssuesHandler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_github_issues);

        Intent intent = getIntent();
        String username = intent.getStringExtra("username");
        String reponame = intent.getStringExtra("reponame");

        mHandler = new GithubIssuesHandler(GithubIssuesActivity.this, username, reponame);
    }

    public void onSubmitClick(View view) {
        mHandler.submit();
    }
}
