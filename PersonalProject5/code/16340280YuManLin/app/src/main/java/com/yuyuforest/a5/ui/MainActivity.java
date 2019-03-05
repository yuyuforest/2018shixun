package com.yuyuforest.a5.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.yuyuforest.a5.ui.bili.BiliActivity;
import com.yuyuforest.a5.ui.github.repos.GithubReposActivity;
import com.yuyuforest.a5.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onBiliClick(View view) {
        Intent intent = new Intent(MainActivity.this, BiliActivity.class);
        startActivity(intent);
    }

    public void onGithubClick(View view) {
        Intent intent = new Intent(MainActivity.this, GithubReposActivity.class);
        startActivity(intent);
    }
}
