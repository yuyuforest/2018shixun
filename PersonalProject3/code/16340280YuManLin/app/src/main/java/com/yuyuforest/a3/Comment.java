package com.yuyuforest.a3;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Comment {
    private long id;
    private String username;
    private String content;
    private Date date;
    private int likenum;
    private Bitmap avatar;

    public Comment(long id, String username, String content, Date date, int likenum, Bitmap avatar) {
        this.id = id;
        this.username = username;
        this.content = content;
        this.date = date;
        this.likenum = likenum;
        this.avatar = avatar;
    }

    public Comment(String username, String content, Date date, int likenum, Bitmap avatar) {
        this.username = username;
        this.content = content;
        this.date = date;
        this.likenum = likenum;
        this.avatar = avatar;
    }

    public long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getContent() {
        return content;
    }

    public String getFormattedDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formatter.format(date);
    }

    public Bitmap getAvatar() {
        return avatar;
    }

    public int getLikenum() {
        return likenum;
    }

    public long getTimestamp() {
        return date.getTime();
    }

    public void setId(long id){
        this.id = id;
    }

    public void doLike() {
        this.likenum++;
    }

    public void cancelLike() {
        this.likenum--;
    }
}
