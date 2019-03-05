package com.yuyuforest.personalproject2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

// 收藏夹列表的ListView的自定义适配器
public class CollectionListAdapter extends BaseAdapter {
    private ArrayList<FoodShort> collections;
    private Context context;

    public CollectionListAdapter(Context _context, ArrayList<FoodShort> _collections){
        collections = _collections;
        context = _context;
    }

    @Override
    public int getCount() {
        return collections == null ? 0 : collections.size();
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public Object getItem(int i) {
        return collections == null ? null : collections.get(i);
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View convertView;
        CollectionListHolder holder;
        if(view == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.item, null);
            holder = new CollectionListHolder();
            holder.shortKind = convertView.findViewById(R.id.shortKind);
            holder.name = convertView.findViewById(R.id.name);
            convertView.setTag(holder);
        }
        else{
            convertView = view;
            holder = (CollectionListHolder) convertView.getTag();
        }
        holder.shortKind.setText(collections.get(i).getKind());
        holder.name.setText(collections.get(i).getName());
        return convertView;
    }

    public void add(FoodShort food){
        collections.add(food);
        notifyDataSetChanged();
    }

    public void remove(int pos){
        collections.remove(pos);
        notifyDataSetChanged();
    }
}

