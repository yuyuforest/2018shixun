package com.yuyuforest.a5.model.github;

public class RepoModel {
    private int id;
    private String name;
    private String description;
    private boolean has_issues;
    private int open_issues;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public boolean isFork() {
        return !has_issues;
    }

    public int getOpenIssuesNum() {
        return open_issues;
    }
}
