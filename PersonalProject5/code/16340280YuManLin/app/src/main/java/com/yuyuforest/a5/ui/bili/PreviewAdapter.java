package com.yuyuforest.a5.ui.bili;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.yuyuforest.a5.R;
import com.yuyuforest.a5.model.bili.PreviewInfo;

import java.util.ArrayList;

public class PreviewAdapter extends RecyclerView.Adapter<PreviewHolder> {
    private Context context;
    private ArrayList<PreviewInfo> data;

    public PreviewAdapter(Context context) {
        this.context = context;
        this.data = new ArrayList<>();
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @NonNull
    @Override
    public PreviewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = View.inflate(context, R.layout.item_preview, null);
        return new PreviewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PreviewHolder previewHolder, int pos) {
        previewHolder.setInfo(context, data.get(pos));
    }

    public int add(PreviewInfo newobj) {
        data.add(newobj);
        notifyDataSetChanged();
        return data.size() - 1;
    }

    public void update(int pos, Bitmap cover, ArrayList<Bitmap> frames) {
        if(0 <= pos && pos < data.size()) {
            PreviewInfo d = data.get(pos);
            d.setCover(cover);
            d.setFrames(frames);
            notifyItemChanged(pos);
        }
    }
}
