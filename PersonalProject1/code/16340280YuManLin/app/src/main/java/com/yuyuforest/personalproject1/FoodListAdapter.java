package com.yuyuforest.personalproject1;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class FoodListAdapter extends RecyclerView.Adapter<FoodListHolder>{
    private Context context;
    private ArrayList<Food> data;

    public interface OnItemClickListener{
        void onClick(int pos);
        void onLongClick(int pos);
    }

    private OnItemClickListener onItemClickListener;

    public FoodListAdapter(Context context, ArrayList<Food> data){
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public FoodListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = View.inflate(context, R.layout.item, null);
        return new FoodListHolder(view);
    }

    // 给item的视图绑定ViewHolder，加载每个item要显示的数据，并设置点击和长按事件
    @Override
    public void onBindViewHolder(@NonNull final FoodListHolder foodListHolder,final int pos) {
        foodListHolder.setShortKind(data.get(pos).getKind());
        foodListHolder.setName(data.get(pos).getName());

        if(onItemClickListener != null){
            foodListHolder.itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    onItemClickListener.onClick(pos);
                }
            });
            foodListHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    onItemClickListener.onLongClick(pos);
                    return true;
                }
            });
        }
    }

    public void setOnItemClickListener(OnItemClickListener _onItemClickListener){
        this.onItemClickListener = _onItemClickListener;
    }

    public int getItemCount(){
        return data == null ? 0 : data.size();
    }
}
