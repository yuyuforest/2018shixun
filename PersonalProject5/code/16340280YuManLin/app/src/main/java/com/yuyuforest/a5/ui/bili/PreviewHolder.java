package com.yuyuforest.a5.ui.bili;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.yuyuforest.a5.R;
import com.yuyuforest.a5.model.bili.PreviewInfo;


public class PreviewHolder extends RecyclerView.ViewHolder{
    private ImageView frame;
    private ProgressBar progressBar;
    private SeekBar seekbar;
    private TextView play;
    private TextView review;
    private TextView duration;
    private TextView create;
    private TextView title;
    private TextView content;

    public PreviewHolder(View view) {
        super(view);
        frame = view.findViewById(R.id.frame);
        progressBar = view.findViewById(R.id.progress);
        seekbar = view.findViewById(R.id.seekbar);
        play = view.findViewById(R.id.play);
        review = view.findViewById(R.id.review);
        duration = view.findViewById(R.id.duration);
        create = view.findViewById(R.id.create);
        title = view.findViewById(R.id.title);
        content = view.findViewById(R.id.content);
    }

    public void setInfo(Context context, final PreviewInfo info) {
        play.setText(context.getString(R.string.bili_play, info.getPlay()));
        review.setText(context.getString(R.string.bili_review, info.getReview()));
        duration.setText(context.getString(R.string.bili_duration, info.getDuration()));
        create.setText(context.getString(R.string.bili_create, info.getCreate()));
        title.setText(info.getTitle());
        content.setText(info.getContent());

        if(info.getCover() != null) {
            progressBar.setVisibility(View.GONE);
            frame.setVisibility(View.VISIBLE);
            frame.setImageBitmap(info.getCover());
        }

        if(info.getFramesCount() != 0) {
            seekbar.setMax(info.getFramesCount() - 1);
            seekbar.setVisibility(View.VISIBLE);
            seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    frame.setImageBitmap(info.getFrame(progress));
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    seekBar.setProgress(0);
                    frame.setImageBitmap(info.getCover());
                }
            });
        }
        else {
            seekbar.setVisibility(View.GONE);
        }
    }
}
