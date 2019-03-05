package com.yuyuforest.a3;

import android.graphics.Bitmap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class User {
    private String username;
    private String password;
    private Bitmap avatar;
    private ArrayList<Long> likes;  // 记录用户点赞的评论的id集合

    public User(String username, String password, Bitmap avatar, ArrayList<Long> likes) {
        this.username = username;
        this.password = password;
        this.avatar = avatar;
        if(likes == null) this.likes = new ArrayList<>();
        else this.likes = likes;
    }

    public User(String username, String password, Bitmap avatar, String likes) {
        this.username = username;
        this.password = password;
        this.avatar = avatar;
        this.likes = new ArrayList<>();
        if(!likes.isEmpty()) {
            String[] strlikes = likes.split(",");
            for (String str : strlikes) {
                this.likes.add(Long.valueOf(str));
            }
        }
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Bitmap getAvatar() {
        return avatar;
    }

    public String getLikes() {  // 将点赞评论的id集合转换成字符串，供数据库存储
        String strlikes = "";
        for (Long l : likes) {
            strlikes = strlikes + ",";
            strlikes = strlikes + Long.toString(l);
        }
        if(strlikes.isEmpty()) return strlikes;
        else return strlikes.substring(1);
    }

    public boolean whetherLike(long commentID) {    // 用户是否点赞了某条评论
        return likes.contains(commentID);
    }

    public void doLike(long commentID) {    // 用户点赞某条评论
        likes.add(commentID);
    }

    public void cancelLike(long commentID) {    // 用户取消点赞评论
        likes.remove(Long.valueOf(commentID));
    }
}
