package com.yuyuforest.a5.model.bili;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.ArrayList;

public class PreviewInfo  implements Serializable {
    private String title;
    private String content;
    private String duration;
    private String create;
    private int play;
    private int review;

    private Bitmap cover;
    private ArrayList<Bitmap> frames;

    public PreviewInfo(VideoJson videoJson) {
        VideoJson.Data data = videoJson.getData();
        title = data.getTitle();
        content = data.getContent();
        duration = data.getDuration();
        create = data.getCreate();
        play = data.getPlay();
        review = data.getReview();
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getDuration() {
        return duration;
    }

    public String getCreate() {
        return create;
    }

    public int getPlay() {
        return play;
    }

    public int getReview() {
        return review;
    }

    public Bitmap getCover() {
        return cover;
    }

    public Bitmap getFrame(int pos) {
        return frames.get(pos);
    }

    public int getFramesCount() {
        return frames == null? 0 : frames.size();
    }

    public void setCover(Bitmap bitmap) {
        this.cover = bitmap;
    }

    public void setFrames(ArrayList<Bitmap> frames) {
        this.frames = frames;
    }
}
