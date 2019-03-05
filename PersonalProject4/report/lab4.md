# 项目4安卓实验报告

开始日期  2018/11/21  完成日期  2018/11/28

## 一、实验题目

简单音乐播放器。

1. 学会使用MediaPlayer
2. 学会简单的多线程编程，使用Handler更新UI
3. 学会使用Service进行后台工作
4. 学会使用Service与Activity进行通信
5. 学习rxJava，使用rxJava更新UI

## 二、实现内容

### 要求

实现一个简单的播放器，要求功能有：  

1. 播放、暂停、停止、退出功能，按停止键会重置封面转角，进度条和播放按钮；按退出键将停止播放并退出程序
2. 后台播放功能，按手机的返回键和home键都不会停止播放，而是转入后台进行播放
3. 进度条显示播放进度、拖动进度条改变进度功能
4. 播放时图片旋转，显示当前播放时间功能，圆形图片的实现使用的是一个开源控件CircleImageView

**附加内容（加分项，加分项每项占10分）**

1. 选歌

   用户可以点击选歌按钮自己选择歌曲进行播放，要求换歌后不仅能正常实现上述的全部功能，还要求选歌成功后不自动播放，重置播放按钮，重置进度条，重置歌曲封面转动角度，最重要的一点：需要解析mp3文件，并更新封面图片。

### 验收内容

1. 布局显示是否正常
2. 播放、暂停、停止功能是否可用，界面显示是否正常
3. 是否可以后台播放
4. 播放时是否显示当前播放时间、位置，以及图片是否旋转
5. 代码+实验报告（先在实验课上检查，检查后再pr）
6. 本次作业提交一份代码和一份实验报告，代码最终提交的是rxJava版本的，关于Handler部分的代码在课堂上验收以及写入实验报告，缺少这部分的同学将会酌情扣分。
7. 因为上周我的教程写错了（现已更正），通过Binder来保持Activity和Service的通信时，直接返回service.this是不规范的，应该通过transact和ontransact的方法来进行通信，因此同学们在代码和实验报告中应使用transact进行通信，使用this的将会酌情扣分。

## 三、实验结果

### 实验截图

以下截图均为动图。（如果图片加载不出来，请查看images文件夹。）

- 播放、拖拽、暂停、播放、停止、播放

  ![](images/week12-play-pause-stop.gif)

- 按下返回键，重新打开应用；按下Home键，重新打开应用

  ![](images/week12-back-home.gif)

- 选歌；退出应用

  ![](images/week12-switch-exit.gif)

### 实验步骤及关键代码

#### 主界面布局

其中，封面使用第三方控件 `CircleImageView` ，将其 `src` 属性设为默认封面图片，即可得到圆形的封面。

```xml
<?xml version="1.0" encoding="utf-8"?>
<android.support.constraint.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity">

    <de.hdodenhof.circleimageview.CircleImageView
        xmlns:app="http://schemas.android.com/apk/res-auto"
        android:id="@+id/cover"
        android:layout_width="250dp"
        android:layout_height="250dp"
        android:src="@mipmap/defaultcover"
        android:layout_marginVertical="20dp"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintRight_toRightOf="parent"/>

    <TextView
        android:id="@+id/song"
        android:text="歌曲"
        android:textSize="25sp"
        android:textColor="#000011"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginVertical="20dp"
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintRight_toRightOf="parent"
        app:layout_constraintTop_toBottomOf="@id/cover"/>

    <TextView
        android:id="@+id/singer"
        android:text="歌手"
        android:textSize="15sp"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginVertical="10dp"
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintRight_toRightOf="parent"
        app:layout_constraintTop_toBottomOf="@id/song"/>

    <TextView
        android:id="@+id/current"
        android:text="00:00"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginStart="20dp"
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintRight_toLeftOf="@id/progress"
        app:layout_constraintTop_toTopOf="@id/progress"
        app:layout_constraintBottom_toBottomOf="@id/progress"/>

    <SeekBar
        android:id="@+id/progress"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_marginHorizontal="10dp"
        android:layout_marginVertical="20dp"
        app:layout_constraintTop_toBottomOf="@id/singer"
        app:layout_constraintLeft_toRightOf="@id/current"
        app:layout_constraintRight_toLeftOf="@id/end"/>

    <TextView
        android:id="@+id/end"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginEnd="20dp"
        app:layout_constraintLeft_toRightOf="@id/progress"
        app:layout_constraintRight_toRightOf="parent"
        app:layout_constraintTop_toTopOf="@id/progress"
        app:layout_constraintBottom_toBottomOf="@id/progress"/>

    <android.support.constraint.ConstraintLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:layout_constraintBottom_toBottomOf="parent"
        android:padding="20dp">
        <ImageButton
            android:id="@+id/choose"
            android:layout_width="50dp"
            android:layout_height="50dp"
            android:background="@mipmap/file"
            android:onClick="onChooseClick"
            app:layout_constraintHorizontal_chainStyle="spread_inside"
            app:layout_constraintStart_toStartOf="parent"
            app:layout_constraintEnd_toStartOf="@id/playpause"/>

        <ImageButton
            android:id="@+id/playpause"
            android:layout_width="50dp"
            android:layout_height="50dp"
            android:background="@mipmap/play"
            android:onClick="onPlayPauseClick"
            app:layout_constraintHorizontal_chainStyle="spread_inside"
            app:layout_constraintStart_toEndOf="@id/choose"
            app:layout_constraintEnd_toStartOf="@id/stop"/>

        <ImageButton
            android:id="@+id/stop"
            android:layout_width="50dp"
            android:layout_height="50dp"
            android:background="@mipmap/stop"
            android:onClick="onStopClick"
            app:layout_constraintHorizontal_chainStyle="spread_inside"
            app:layout_constraintStart_toEndOf="@id/playpause"
            app:layout_constraintEnd_toStartOf="@id/exit"/>

        <ImageButton
            android:id="@+id/exit"
            android:layout_width="50dp"
            android:layout_height="50dp"
            android:background="@mipmap/back"
            android:onClick="onExitClick"
            app:layout_constraintHorizontal_chainStyle="spread_inside"
            app:layout_constraintStart_toEndOf="@id/stop"
            app:layout_constraintEnd_toEndOf="parent"/>
    </android.support.constraint.ConstraintLayout>



</android.support.constraint.ConstraintLayout>
```

#### `MusicService` 音乐服务

```java
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
}

```

#### `MusicServiceBinder` 重写 `onTransact`

```java
public class MusicService extends Service{

    // ...

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

```

#### `MainActivity` 绑定服务与解绑

其中，`MainHandler` 是 `MainActivity` 的逻辑处理类。

```java
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

    // ...
}

```

#### `MainHandler` 初始化

```java
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

            // ...

        } catch (RemoteException e) {
            e.printStackTrace();
        } finally {
            data.recycle();
            reply.recycle();
        }
    }
}
```

#### `MainHandler` 使用 `Handler` 与 `Runnable` 更新进度条

```java
public class MainHandler{

    private Handler mHandler;
    private Runnable mRunnable;
    
    public void setMusicService(IBinder mMusicServiceBinder) {
        this.mMusicServiceBinder = mMusicServiceBinder;
        final IBinder binder = mMusicServiceBinder;

        try {
            // ...

            mHandler = new Handler();
            mRunnable = new Runnable() {
                @Override
                public void run() {
                    if(!pausing) {
                        try {
                            Parcel data = Parcel.obtain();
                            Parcel reply = Parcel.obtain();
                            binder.transact(MusicService.GET_CURRENT_POSITION, data, reply, 0);
                            int pos = reply.readInt();
                            mProgress.setProgress(pos);
                            mCurrent.setText(mTimeFormatter.format(pos));
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                    mHandler.postDelayed(mRunnable, 1000);
                }
            };
            mHandler.post(mRunnable);

            mProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if(fromUser) {
                        try {
                            Parcel data = Parcel.obtain();
                            Parcel reply = Parcel.obtain();
                            data.writeInt(mProgress.getProgress());
                            binder.transact(MusicService.SEEK_TO_POSITION, data, reply, 1);
                        } catch (RemoteException e) {
                            e.printStackTrace();
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
        }
    }
}
```

#### `MainHandler` 使用RxJava更新进度条

```java
public class MainHandler {
    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();

	// ...
    
    // 绑定服务
    public void setMusicService(IBinder mMusicServiceBinder) {
        this.mMusicServiceBinder = mMusicServiceBinder;
        final IBinder binder = mMusicServiceBinder;

        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            // ...

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
    
    public void onDestroy() {
        mCompositeDisposable.clear();   // 清除观察者
    }
    
    // ...
}

```



#### 播放/暂停

```java
public class MainHandler {

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
}

```

#### 停止

```java
public class MainHandler {

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
}

```

#### 选歌

```java
public class MainHandler {

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

```

`MainHandler` 创建 `Intent` 以选择文件，选择的结果回调方法写在 `MainActivity` 中，在这个方法里调用 `MainHandler` 的 `switchSong(String path)` 以重置播放源。

```java
public class MainActivity extends AppCompatActivity {

    // ...
    
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

```

#### 退出

```java
public class MainHandler {

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
}

```

#### 恢复状态

```java
public class MainHandler {
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
}
```

#### 更新界面

```java
public class MainHandler {
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
}
```

### 实验遇到的困难及解决思路

#### `CircleImageView` 不显示为圆形

这是因为我设置的是 `background` 属性。

根据Github上的文档，应当设置 `src` 属性才能显示为圆形，否则显示为常规的矩形视图。

#### android.content.ActivityNotFoundException: No Activity found to handle Intent

在选取文件时，如果这样写：

```java
Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
intent.setType("audio/*");
intent.addCategory(Intent.CATEGORY_APP_MUSIC);
mActivity.startActivityForResult(intent, 0);
```

就会出现这个异常。

改成下面后就不报错了。

```java
intent.addCategory(Intent.CATEGORY_DEFAULT);
```

#### bindService

```Java
bindService(mServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
```

以上代码虽然可以在绑定服务时自动开启服务，但是活动销毁、解绑服务后，没有绑定的活动的服务就会中止。这样，按下系统返回键、应用程序关闭后，服务可能会无法继续后台运行。

应当像下面这样写：

```java
startService(mServiceIntent);
bindService(mServiceIntent, mServiceConnection, 0);
```

## 四、实验思考及感想

1. 学习了如何解析歌曲信息：（这一部分的实现写在 `MusicService` 中的 `setPath` 方法里）

```java
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
```

2. 对 `MediaPlayer` 的生命周期和它的状态机有了一定的认识。

   - 如果在某个状态调用了不恰当的方法，会引起 `IllegalStateException` 。所以调用 `MediaPlayer` 的方法时要注意顺序。

   - 如果出现了这个异常，在断点调试时，可以通过查看 `MediaPlayer` 变量中的 `mState` 的值来获知当前的状态；也可以查看控制台里诸如这样的信息 `MediaPlayerNative: Action: xxx CurrentState: xxx` 。

   - 另外，可以为 `MediaPlayer` 设置 `OnErrorListener` ，或者通过控制台来查看 `MediaPlayer` 发生的错误。
   - 在查资料时，看到这样的说法：在 `stop()` 和 `release()` 之间加入 `reset()` ，可以避免内存泄漏，因为 `reset()` 释放了一个未在 `release()` 中释放的引用。

3. 对 `Service` 的使用有了更深入的了解。

   - `startService` 和 `bindService` 容易引起误解。使用时要视情况而定。

   - 通过 `IBinder` 和 `onTransact/transact` 方法，可以与 `Service` 进行跨进程的交互。

   - 在调用 `transact` 后，将传入的参数 `Parcel` 类型变量回收会更有益。

4. 初步了解了 `android.os.Handler` 。它可以配合 `Runnable` ，调用 `post` 或 `postDelayed` 方法以实现延时事件。

5. 实践了 `RxJava` 的使用，学习到了异步的观察者模式。

