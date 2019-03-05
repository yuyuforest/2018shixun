package com.yuyuforest.a4;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class MusicService extends Service{

    private IBinder mBinder;
    private MediaPlayer mMediaPlayer;
    private String singer;
    private String song;
    private Bitmap cover;

    @Override
    public void onCreate() {
        super.onCreate();
        mMediaPlayer = new MediaPlayer();
        mBinder = new MusicServiceBinder();
        mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Toast.makeText(MusicService.this, "mediaplayer error:" + what + " " + extra, Toast.LENGTH_LONG).show();
                return false;
            }
        });
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    // 初始化音乐播放器
    public void initMediaPlayer(String path) {
        setPath(path);
    }

    // 播放或暂停
    public void playOrPause() {
        if (mMediaPlayer == null) return;
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
        } else {
            mMediaPlayer.start();
        }
    }

    // 停止音乐播放器
    public void stop() {
        if (mMediaPlayer == null) return;
        try {
            mMediaPlayer.stop();
            mMediaPlayer.prepareAsync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 终止服务，回收音乐播放器
    public void exit() {
        if(mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    // 重置音乐播放器的音乐
    public void reset(String path) {
        mMediaPlayer.reset();
        setPath(path);
    }

    // 设置播放源
    private void setPath(String path) {
        try{
            mMediaPlayer.setDataSource(path);
            mMediaPlayer.prepareAsync();
            mMediaPlayer.setLooping(true);

            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(path);
            singer = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
            song = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
            byte[] data = mmr.getEmbeddedPicture();
            if(data != null && data.length != 0) {
                cover = BitmapFactory.decodeByteArray(data, 0, data.length);
            }
            else {
                cover = null;
            }

            mmr.release();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // MusicServiceBinder的onTransact方法的状态码
    public static final int INIT = 1;
    public static final int RESET = 2;
    public static final int SEEK_TO_POSITION = 3;
    public static final int PLAY_OR_PAUSE = 4;
    public static final int STOP = 5;
    public static final int EXIT = 6;
    public static final int GET_CURRENT_POSITION = 7;
    public static final int GET_SONG = 8;
    public static final int GET_PLAYING = 9;
    public static final int GET_ALL = 10;

    private class MusicServiceBinder extends Binder {
        @Override
        protected boolean onTransact(int code, @NonNull Parcel data, @Nullable Parcel reply, int flags) throws RemoteException {
            switch (code) {
                case INIT: {
                    String path = data.readString();
                    initMediaPlayer(path);
                    break;
                }
                case RESET: {
                    String path = data.readString();
                    reset(path);
                    break;
                }
                case SEEK_TO_POSITION: {
                    int pos = data.readInt();
                    mMediaPlayer.seekTo(pos);
                    break;
                }
                case PLAY_OR_PAUSE:
                    playOrPause();
                    break;
                case STOP:
                    stop();
                    break;
                case EXIT:
                    exit();
                    break;
                case GET_CURRENT_POSITION:
                    reply.writeInt(mMediaPlayer.getCurrentPosition());
                    break;
                case GET_SONG:
                    reply.writeString(song);
                    break;
                case GET_PLAYING:
                    boolean[] playing = {mMediaPlayer.isPlaying()};
                    reply.writeBooleanArray(playing);
                    break;
                case GET_ALL:
                    Bundle bundle = new Bundle();
                    bundle.putString("song", song);
                    bundle.putString("singer", singer);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    if(cover != null) cover.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    bundle.putByteArray("cover", baos.toByteArray());
                    bundle.putInt("current", mMediaPlayer.getCurrentPosition());
                    bundle.putInt("end", mMediaPlayer.getDuration());
                    reply.writeBundle(bundle);
                    break;
                default:
                    break;
            }

            return super.onTransact(code, data, reply, flags);
        }
    }
}
