package com.yuyuforest.a5.ui.github.repos;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.yuyuforest.a5.R;

public class GithubReposActivity extends AppCompatActivity {
    private GithubReposHandler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_github_repos);

        mHandler = new GithubReposHandler(GithubReposActivity.this);
    }

    public void onSearchClick(View view) {
        mHandler.search();
    }
}
