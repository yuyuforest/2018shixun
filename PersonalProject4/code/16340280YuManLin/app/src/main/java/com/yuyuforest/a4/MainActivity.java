package com.yuyuforest.a4;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private MainHandler mHandler;
    private ServiceConnection mServiceConnection;
    private Intent mServiceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mHandler = new MainHandler(this);

        mServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mHandler.setMusicService(service);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };

        mServiceIntent = new Intent(this, MusicService.class);
        startService(mServiceIntent);
        bindService(mServiceIntent, mServiceConnection, 0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.onDestroy();
        unbindService(mServiceConnection);
    }

    public void onPlayPauseClick(View view) {
        mHandler.playOrPause();
    }

    public void onStopClick(View view) {
        mHandler.stop();
    }

    public void onExitClick(View view) {
        mHandler.exit();
        try{
            stopService(mServiceIntent);
            finish();
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onChooseClick(View view) {
        mHandler.choose();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            String path = getRealPathFromURI(this.getApplicationContext(), uri);
            mHandler.switchSong(path);
        }
    }

    private static String getRealPathFromURI(Context context, Uri contentUri) {
        String[] proj = {MediaStore.Audio.Media.DATA};
        try (Cursor cursor = context.getContentResolver().query(contentUri, proj,
                null, null, null)) {
            if (cursor == null) {
                return "";
            }
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
    }
}
