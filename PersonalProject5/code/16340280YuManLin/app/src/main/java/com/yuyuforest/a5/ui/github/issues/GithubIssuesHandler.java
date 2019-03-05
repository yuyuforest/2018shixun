package com.yuyuforest.a5.ui.github.issues;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.yuyuforest.a5.R;
import com.yuyuforest.a5.model.github.IssueModel;
import com.yuyuforest.a5.model.github.IssuePost;
import com.yuyuforest.a5.model.github.RepoModel;
import com.yuyuforest.a5.service.github.GithubService;
import com.yuyuforest.a5.utils.Utils;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.HttpException;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class GithubIssuesHandler {
    private GithubIssuesActivity mActivity;

    private EditText inputTitle;
    private EditText inputBody;
    private RecyclerView result;
    private IssueAdapter mIssueAdapter;

    private String username;
    private String reponame;

    public GithubIssuesHandler(GithubIssuesActivity activity, String username, String reponame) {
        this.mActivity = activity;
        this.username = username;
        this.reponame = reponame;

        inputTitle = mActivity.findViewById(R.id.input_title);
        inputBody = mActivity.findViewById(R.id.input_body);
        result = mActivity.findViewById(R.id.result);

        mIssueAdapter = new IssueAdapter(activity);
        result.setAdapter(mIssueAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(
                mActivity, LinearLayoutManager.VERTICAL, false);
        result.setLayoutManager(layoutManager);

        requestIssues();
    }

    public void requestIssues() {
        OkHttpClient build = new OkHttpClient.Builder()
                .connectTimeout(2, TimeUnit.SECONDS)
                .readTimeout(2, TimeUnit.SECONDS)
                .writeTimeout(2, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GithubService.baseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(build)
                .build();

        GithubService githubService = retrofit.create(GithubService.class);

        Observable<List<IssueModel>> observable = githubService.getIssues(username, reponame);

        Observer<List<IssueModel>> observer = new Observer<List<IssueModel>>() {
            private Disposable disposable;
            @Override
            public void onSubscribe(Disposable d) {
                disposable = d;
            }

            @Override
            public void onNext(List<IssueModel> issueModels) {
                loadIssues(issueModels);
                onComplete();
            }

            @Override
            public void onError(Throwable e) {
                if(e instanceof HttpException) {
                    Utils.toast(mActivity, "该仓库不存在");
                } else if(e instanceof UnknownHostException) {
                    Utils.toast(mActivity, "网络连接异常");
                } else {
                    Utils.toast(mActivity, e.getMessage());
                }
            }

            @Override
            public void onComplete() {
                disposable.dispose();
            }
        };

        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void loadIssues(List<IssueModel> issueModels) {
        if(issueModels == null || issueModels.isEmpty()) {
            Utils.toast(mActivity, "该仓库无开启的问题");
            return;
        }
        mIssueAdapter.newList((ArrayList<IssueModel>) issueModels);
    }

    public void submit() {
        OkHttpClient build = new OkHttpClient.Builder()
                .connectTimeout(2, TimeUnit.SECONDS)
                .readTimeout(2, TimeUnit.SECONDS)
                .writeTimeout(2, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GithubService.baseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(build)
                .build();

        GithubService githubService = retrofit.create(GithubService.class);

        String title = inputTitle.getText().toString();
        String body = inputBody.getText().toString();
        inputTitle.setText("");
        inputBody.setText("");

        Observable<IssueModel> observable = githubService.newIssue(username, reponame, new IssuePost(title, body));

        Observer<IssueModel> observer = new Observer<IssueModel>() {
            private Disposable disposable;
            @Override
            public void onSubscribe(Disposable d) {
                disposable = d;
            }

            @Override
            public void onNext(IssueModel issueModel) {
                mIssueAdapter.add(issueModel);
                onComplete();
            }

            @Override
            public void onError(Throwable e) {
                if(e instanceof UnknownHostException) {
                    Utils.toast(mActivity, "网络连接异常");
                } else {
                    Utils.toast(mActivity, e.getMessage());
                }
            }

            @Override
            public void onComplete() {
                disposable.dispose();
            }
        };

        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }
}
