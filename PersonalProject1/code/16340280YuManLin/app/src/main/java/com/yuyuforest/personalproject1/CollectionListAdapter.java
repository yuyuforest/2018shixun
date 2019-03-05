package com.yuyuforest.personalproject1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

// 收藏夹列表的ListView的自定义适配器
public class CollectionListAdapter extends BaseAdapter {
    private ArrayList<Food> collections;
    private Context context;

    public CollectionListAdapter(Context _context, ArrayList<Food> _collections){
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
        ViewHolder viewHolder;
        if(view == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.item, null);
            viewHolder = new ViewHolder();
            viewHolder.shortKind = convertView.findViewById(R.id.shortKind);
            viewHolder.name = convertView.findViewById(R.id.name);
            convertView.setTag(viewHolder);
        }
        else{
            convertView = view;
            viewHolder = (ViewHolder)convertView.getTag();
        }
        viewHolder.shortKind.setText(collections.get(i).getKind());
        viewHolder.name.setText(collections.get(i).getName());
        return convertView;
    }

    public void add(Food food){
        collections.add(food);
        notifyDataSetChanged();
    }

    public void remove(int pos){
        collections.remove(pos);
        notifyDataSetChanged();
    }

    private class ViewHolder{
        public Button shortKind;
        public TextView name;
    }
}

