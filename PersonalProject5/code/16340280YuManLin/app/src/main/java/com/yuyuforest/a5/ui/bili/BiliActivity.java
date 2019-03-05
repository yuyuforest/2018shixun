package com.yuyuforest.a5.ui.bili;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.yuyuforest.a5.R;

public class BiliActivity extends AppCompatActivity {

    private BiliHandler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bili);

        mHandler = new BiliHandler(BiliActivity.this);
    }

    public void onSearchClick(View view) {
        mHandler.search();
    }
}
