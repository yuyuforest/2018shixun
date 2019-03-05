package com.yuyuforest.a5.ui.github.repos;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.EditText;

import com.yuyuforest.a5.R;
import com.yuyuforest.a5.model.github.RepoModel;
import com.yuyuforest.a5.service.github.GithubService;
import com.yuyuforest.a5.utils.Utils;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.HttpException;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class GithubReposHandler {
    private GithubReposActivity mActivity;
    private EditText inputUser;
    private RecyclerView result;
    private RepoAdapter mRepoAdapter;

    public GithubReposHandler(GithubReposActivity activity) {
        mActivity = activity;
        result = mActivity.findViewById(R.id.result);
        inputUser = mActivity.findViewById(R.id.input_user);

        mRepoAdapter = new RepoAdapter(activity);
        result.setAdapter(mRepoAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(
                mActivity, LinearLayoutManager.VERTICAL, false);
        result.setLayoutManager(layoutManager);
    }

    public void requestRepos() {
        String username = inputUser.getText().toString();

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

        Observable<List<RepoModel>> observable = githubService.getRepos(username);

        Observer<List<RepoModel>> observer = new Observer<List<RepoModel>>() {
            private Disposable disposable;
            @Override
            public void onSubscribe(Disposable d) {
                disposable = d;
            }

            @Override
            public void onNext(List<RepoModel> repoModels) {
                onSearchResult(repoModels);
                onComplete();
            }

            @Override
            public void onError(Throwable e) {
                if(e instanceof HttpException && ((HttpException) e).code() == 404) {
                    Utils.toast(mActivity, "该用户不存在");
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


    public void search() {
        mRepoAdapter.clear();
        requestRepos();
    }

    public void onSearchResult(List<RepoModel> repoModels) {
        if(repoModels == null || repoModels.isEmpty()) {
            Utils.toast(mActivity, "该用户无仓库");
            return;
        }
        mRepoAdapter.newList(inputUser.getText().toString(), (ArrayList<RepoModel>) repoModels);
    }
}
