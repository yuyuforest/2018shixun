package com.yuyuforest.a5.ui.bili;

import android.graphics.Bitmap;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.EditText;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.yuyuforest.a5.R;
import com.yuyuforest.a5.utils.Utils;
import com.yuyuforest.a5.model.bili.FramesJson;
import com.yuyuforest.a5.model.bili.PreviewInfo;
import com.yuyuforest.a5.model.bili.VideoJson;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class BiliHandler {
    private BiliActivity mActivity;
    private EditText uid;
    private RecyclerView result;
    private PreviewAdapter mPreviewAdapter;

    private enum RequestType {
        NONE, INFO, IMAGE, ERROR
    }

    public BiliHandler(BiliActivity biliActivity) {
        mActivity = biliActivity;
        result = mActivity.findViewById(R.id.result);
        uid = mActivity.findViewById(R.id.uid);

        mPreviewAdapter = new PreviewAdapter(biliActivity);
        result.setAdapter(mPreviewAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(
                mActivity, LinearLayoutManager.VERTICAL, true);
        result.setLayoutManager(layoutManager);
    }

    public void beginRequest(final int id) {
        // 观察者负责更新UI
        Observer<Object[]> observer = new Observer<Object[]>() {
            private Disposable disposable;
            private int pos = -1;

            @Override
            public void onSubscribe(Disposable d) {
                disposable = d;
            }

            @Override
            public void onNext(Object[] objs) {
                RequestType requestType = (RequestType) objs[0];
                switch (requestType) {
                    case INFO:
                        pos = onSearchResult((VideoJson)objs[1]);
                        break;
                    case IMAGE:
                        onCoverResult(pos, (Bitmap) objs[1], (ArrayList<Bitmap>)objs[2]);
                        break;
                    case ERROR:
                        Utils.toast(mActivity, (String)objs[1]);
                        break;
                }
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {
                disposable.dispose();
            }
        };

        // 被观察者负责请求网络获取信息
        Observable<Object[]> observable = Observable.create(new ObservableOnSubscribe<Object[]>() {
            @Override
            public void subscribe(ObservableEmitter<Object[]> emitter) {
                try {
                    // 请求视频信息
                    InputStream is = Utils.requestURL("https://space.bilibili.com/ajax/top/showTop?mid=" + id);

                    // 解析视频信息
                    String jsonstr = Utils.readStringFromInputStream(is);
                    VideoJson videoJson = new Gson().fromJson(jsonstr, VideoJson.class);
                    if(videoJson.getStatus()) {
                        // 通知观察者更新视频信息
                        Object[] info = new Object[]{RequestType.INFO, videoJson};
                        emitter.onNext(info);

                        // 请求封面
                        is = Utils.requestURL(videoJson.getData().getCover());

                        // 封面和帧图
                        Bitmap cover = Utils.readBitmapFromInputStream(is);
                        ArrayList<Bitmap> frames = null;

                        // 请求视频帧信息
                        is = Utils.requestURL("https://api.bilibili.com/pvideo?aid=" + videoJson.getData().getAid());
                        String jsonstr2 = Utils.readStringFromInputStream(is);
                        FramesJson framesJson = new Gson().fromJson(jsonstr2, FramesJson.class);
                        FramesJson.Data framesData = framesJson.getData();
                        int count = framesData.getCount();

                        if(framesJson.getData().getImage() != null && framesJson.getData().getImage().length > 0) {
                            // 请求帧图
                            is = Utils.requestURL(framesJson.getData().getImage()[0]);
                            frames = Utils.readFramesFromInputStream(is, count, framesData.getImg_x_len(), framesData.getImg_y_len(),
                                    framesData.getImg_x_size(), framesData.getImg_y_size());

                        }
                        // 通知观察者更新封面和帧图
                        Object[] image = new Object[]{RequestType.IMAGE, cover, frames};
                        emitter.onNext(image);
                    }
                    else {
                        emitter.onNext(new Object[]{RequestType.ERROR, "该记录不存在"});
                    }
                } catch (JsonSyntaxException e) {
                    emitter.onNext(new Object[]{RequestType.ERROR, "该记录不存在"});
                } catch (SocketTimeoutException e) {
                    emitter.onNext(new Object[]{RequestType.ERROR, "网络连接异常"});
                } catch (IOException e) {
                    emitter.onNext(new Object[]{RequestType.ERROR, "网络连接异常"});
                } finally {
                    emitter.onComplete();
                }
            }
        });

        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void search() {
        String query = uid.getText().toString();
        try {
            int id = Integer.valueOf(query).intValue();
            if(id <= 0) {
                Utils.toast(mActivity, "用户id应当大于0");
                return;
            }
            search(id);
        } catch (NumberFormatException e) {
            Utils.toast(mActivity, "请输入整数");
        }

    }

    public void search(int id) {
        if(id > 40) return;

        beginRequest(id);
    }

    public int onSearchResult(VideoJson videoJson) {
        if(videoJson.getStatus()) {
            PreviewInfo previewInfo = new PreviewInfo(videoJson);
            return mPreviewAdapter.add(previewInfo);
        } else {
            Utils.toast(mActivity, "该记录不存在");
            return -1;
        }
    }

    public void onCoverResult(int pos, Bitmap cover, ArrayList<Bitmap> frames) {
        mPreviewAdapter.update(pos, cover, frames);
    }

}
