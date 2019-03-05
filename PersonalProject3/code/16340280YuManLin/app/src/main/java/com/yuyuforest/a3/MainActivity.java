package com.yuyuforest.a3;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onStorage1Click(View view) {
        Intent intent = new Intent(MainActivity.this, Storage1LoginActivity.class);
        startActivity(intent);
    }

    public void onStorage2Click(View view) {
        Intent intent = new Intent(MainActivity.this, Storage2LoginActivity.class);
        startActivity(intent);
    }
}
