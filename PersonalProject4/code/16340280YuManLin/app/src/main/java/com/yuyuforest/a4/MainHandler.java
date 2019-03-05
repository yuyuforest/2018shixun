package com.yuyuforest.a4;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class MainHandler {

    private CircleImageView mCover;
    private SeekBar mProgress;
    private TextView mCurrent;
    private TextView mEnd;
    private TextView mSong;
    private TextView mSinger;
    private ImageButton mPlayOrPause;
    private ObjectAnimator mCoverAnimator;
    private SimpleDateFormat mTimeFormatter = new SimpleDateFormat("mm:ss");

    private boolean pausing = true;
    private boolean stopping = true;

    private MainActivity mActivity;
    private IBinder mMusicServiceBinder;
    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    private static final String initSong = "/山高水长.mp3";

    public MainHandler(MainActivity activity) {
        mActivity = activity;

        mCover = activity.findViewById(R.id.cover);
        mCurrent = activity.findViewById(R.id.current);
        mEnd = activity.findViewById(R.id.end);
        mPlayOrPause = activity.findViewById(R.id.playpause);
        mProgress = activity.findViewById(R.id.progress);
        mSinger = activity.findViewById(R.id.singer);
        mSong = activity.findViewById(R.id.song);

        mCoverAnimator = ObjectAnimator.ofFloat(mCover, "rotation", 0.0f, 360.0f);
        mCoverAnimator.setDuration(10000);
        mCoverAnimator.setInterpolator(new LinearInterpolator());
        mCoverAnimator.setRepeatCount(-1);
        mCoverAnimator.setRepeatMode(ObjectAnimator.RESTART);
    }

    // 绑定服务
    public void setMusicService(IBinder mMusicServiceBinder) {
        this.mMusicServiceBinder = mMusicServiceBinder;
        final IBinder binder = mMusicServiceBinder;

        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            mMusicServiceBinder.transact(MusicService.GET_SONG, data, reply, 0);
            if(reply.readString() == null) {
                // MusicService当前没有歌曲，则使用默认歌曲来进行初始化
                String path = Environment.getExternalStorageDirectory() + "/data" + initSong;
                data.writeString(path);
                mMusicServiceBinder.transact(MusicService.INIT, data, reply, 1);
            }
            else {  // MusicService当前有歌曲，需要将界面恢复到该播放器的相应状态
                restore();  // 恢复状态
            }
            updateViews();  // 更新界面

            // 观察者负责更新UI
            DisposableObserver<Integer> observer = new DisposableObserver<Integer>() {
                @Override
                public void onNext(Integer integer) {
                    int pos = integer.intValue();
                    mProgress.setProgress(pos);
                    mCurrent.setText(mTimeFormatter.format(pos));
                }

                @Override
                public void onError(Throwable e) {
                    e.printStackTrace();
                }

                @Override
                public void onComplete() {

                }
            };
            // 被观察者负责与MusicService通信，获取当前播放位置
            Observable<Integer> observable = Observable.create(new ObservableOnSubscribe<Integer>() {
                @Override
                public void subscribe(ObservableEmitter<Integer> emitter) {
                    Parcel data = Parcel.obtain();
                    Parcel reply = Parcel.obtain();
                    try {
                        while(true) {
                            if(!pausing) {
                                data = Parcel.obtain();
                                reply = Parcel.obtain();
                                binder.transact(MusicService.GET_CURRENT_POSITION, data, reply, 0);
                                int pos = reply.readInt();
                                emitter.onNext(pos);
                            }
                            Thread.sleep(1000);
                        }
                        //emitter.onComplete();
                    } catch (InterruptedException e) {
                        if (!emitter.isDisposed()) {
                            emitter.onError(e);
                        }
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        data.recycle();
                        reply.recycle();
                    }
                }
            });
            observable.subscribeOn(Schedulers.io())             // 被观察者在Schedulers.io()线程上工作
                    .observeOn(AndroidSchedulers.mainThread())  // 观察者在主线程上工作
                    .subscribe(observer);
            mCompositeDisposable.add(observer);

            // 为进度条设置监听器
            mProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if(fromUser) {
                        Parcel data = Parcel.obtain();
                        Parcel reply = Parcel.obtain();
                        try {
                            data.writeInt(mProgress.getProgress());
                            binder.transact(MusicService.SEEK_TO_POSITION, data, reply, 1);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        } finally {
                            data.recycle();
                            reply.recycle();
                        }
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) { }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) { }
            });

        } catch (RemoteException e) {
            e.printStackTrace();
        } finally {
            data.recycle();
            reply.recycle();
        }
    }

    // 按下返回键后再次打开应用后，需要判断是否正在播放音乐，恢复到与音乐服务一致的状态
    public void restore() {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            mMusicServiceBinder.transact(MusicService.GET_PLAYING, data, reply, 0);

            boolean[] par = {false};
            reply.readBooleanArray(par);
            if(par[0]) {    // 是否正在播放音乐
                mPlayOrPause.setImageResource(R.mipmap.pause);
                mCoverAnimator.start();
                pausing = false;
                stopping = false;
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        } finally {
            data.recycle();
            reply.recycle();
        }
    }

    // 更新界面
    public void updateViews() {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            mMusicServiceBinder.transact(MusicService.GET_ALL, data, reply, 0);

            Bundle bundle = reply.readBundle();
            int current = bundle.getInt("current");
            int end = bundle.getInt("end");
            String song = bundle.getString("song");
            String singer = bundle.getString("singer");
            byte[] bytes = bundle.getByteArray("cover");
            Bitmap cover;
            if(bytes != null && bytes.length != 0) cover = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            else cover = BitmapFactory.decodeResource(mActivity.getResources(), R.mipmap.defaultcover);

            mCurrent.setText(mTimeFormatter.format(current));
            mEnd.setText(mTimeFormatter.format(end));
            mProgress.setMax(end);
            mProgress.setProgress(current);
            mSong.setText(song);
            mSinger.setText(singer);
            mCover.setImageBitmap(cover);
        } catch (RemoteException e) {
            e.printStackTrace();
        } finally {
            data.recycle();
            reply.recycle();
        }
    }

    public void onDestroy() {
        mCompositeDisposable.clear();   // 清除观察者
    }

    // 播放或暂停
    public void playOrPause() {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            mMusicServiceBinder.transact(MusicService.PLAY_OR_PAUSE, data, reply, 1);
            if(pausing) {
                mPlayOrPause.setImageResource(R.mipmap.pause);
                if(stopping)
                    mCoverAnimator.start();
                else
                    mCoverAnimator.resume();
                stopping = false;
            }
            else {
                mPlayOrPause.setImageResource(R.mipmap.play);
                mCoverAnimator.pause();
            }
            pausing = !pausing;
        } catch (RemoteException e) {
            e.printStackTrace();
        } finally {
            data.recycle();
            reply.recycle();
        }
    }

    // 停止
    public void stop() {
        if(stopping) return;
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            mMusicServiceBinder.transact(MusicService.STOP, data, reply, 1);
            mPlayOrPause.setImageResource(R.mipmap.play);
            pausing = true;
            stopping = true;
            mCoverAnimator.end();
            mCurrent.setText(mTimeFormatter.format(0));
            mProgress.setProgress(0);
        } catch (RemoteException e) {
            e.printStackTrace();
        } finally {
            data.recycle();
            reply.recycle();
        }
    }

    // 退出
    public void exit() {
        mCompositeDisposable.clear();
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            mMusicServiceBinder.transact(MusicService.EXIT, data, reply, 1);
        } catch (RemoteException e) {
            e.printStackTrace();
        } finally {
            data.recycle();
            reply.recycle();
        }
    }

    // 选歌
    public void choose() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/*");
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        mActivity.startActivityForResult(intent, 0);
    }

    // 选歌后，更新播放器的播放源
    public void switchSong(String path) {
        stop();
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeString(path);
            mMusicServiceBinder.transact(MusicService.RESET, data, reply, 1);
            updateViews();
        } catch (RemoteException e) {
            e.printStackTrace();
        } finally {
            data.recycle();
            reply.recycle();
        }
    }
}
