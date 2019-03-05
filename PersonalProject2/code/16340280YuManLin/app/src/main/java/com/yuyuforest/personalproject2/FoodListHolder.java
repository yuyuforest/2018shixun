package com.yuyuforest.personalproject2;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import jp.wasabeef.recyclerview.animators.holder.AnimateViewHolder;

public class FoodListHolder extends RecyclerView.ViewHolder implements AnimateViewHolder{
    private View view;  // item的父视图，即 RelativeLayout
    private Button shortKind;
    private TextView name;

    public FoodListHolder(Context _context, View _view, ViewGroup _viewGroup){
        super(_view);
        view = _view;
        shortKind = null;
        name = null;
    }


    // 由传入的视图获取要缓存的子视图
    public FoodListHolder(View _view){
        super(_view);
        view = _view;
        shortKind = view.findViewById(R.id.shortKind);
        name = view.findViewById(R.id.name);
    }

    // 获取FoodListHolder实例
    public static FoodListHolder getInstance(Context _context, ViewGroup _viewGroup, int _layoutId){
        View _view = LayoutInflater.from(_context).inflate(_layoutId, _viewGroup, false);
        FoodListHolder holder = new FoodListHolder(_context, _view, _viewGroup);
        return holder;
    }

    // 设置种类缩写
    public void setShortKind(String text){
        shortKind.setText(text);
    }

    // 设置食品名称
    public void setName(String text){
        name.setText(text);
    }

    @Override
    public void preAnimateAddImpl(RecyclerView.ViewHolder holder) {

    }

    @Override
    public void preAnimateRemoveImpl(RecyclerView.ViewHolder holder) {

    }

    // 设置删除项的动画
    @Override
    public void animateRemoveImpl(RecyclerView.ViewHolder holder, ViewPropertyAnimatorListener listener) {
        ViewCompat.animate(itemView).translationX(itemView.getWidth() * 2)
                .alpha(0)
                .scaleX(0)
                .scaleY(0)
                .setDuration(300)
                .setListener(listener)
                .start();
    }

    @Override
    public void animateAddImpl(RecyclerView.ViewHolder holder, ViewPropertyAnimatorListener listener) {

    }
}
