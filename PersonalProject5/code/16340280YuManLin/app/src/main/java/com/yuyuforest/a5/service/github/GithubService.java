package com.yuyuforest.a5.service.github;

import com.yuyuforest.a5.model.github.IssueModel;
import com.yuyuforest.a5.model.github.IssuePost;
import com.yuyuforest.a5.model.github.RepoModel;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface GithubService {
    String baseURL = "https://api.github.com";

    @GET("/users/{user_name}/repos")
    Observable<List<RepoModel>> getRepos(@Path("user_name") String user_name);


    @GET("/repos/{user_name}/{repo_name}/issues")
    Observable<List<IssueModel>> getIssues(@Path("user_name") String user_name, @Path("repo_name") String repo_name);

    @Headers("Authorization: token bd6d7c38932fc404f415b4588c31bf83178c0bd2")
    @POST("/repos/{user_name}/{repo_name}/issues")
    Observable<IssueModel> newIssue(@Path("user_name") String user_name, @Path("repo_name") String repo_name, @Body IssuePost issuePost);
}
