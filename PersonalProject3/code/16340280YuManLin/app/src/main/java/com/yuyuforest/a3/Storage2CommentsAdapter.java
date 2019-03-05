package com.yuyuforest.a3;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class Storage2CommentsAdapter extends BaseAdapter {
    private ArrayList<Comment> comments;
    private Context context;

    public Storage2CommentsAdapter(Context context, ArrayList<Comment> comments) {
        this.context = context;
        this.comments = comments;
    }

    public void add(Comment comment) {
        comments.add(0, comment);
        notifyDataSetChanged();
    }

    public void remove(int position) {
        comments.remove(position);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return comments == null ? 0 : comments.size();
    }

    @Override
    public Object getItem(int position) {
        return comments == null ? null : comments.get(position);
    }

    @Override
    public long getItemId(int position) {
        return comments == null ? 0 : comments.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.item_storage2_comment, null);
            viewHolder = new ViewHolder();
            viewHolder.avatar = convertView.findViewById(R.id.avatar);
            viewHolder.username = convertView.findViewById(R.id.username);
            viewHolder.date = convertView.findViewById(R.id.date);
            viewHolder.content = convertView.findViewById(R.id.content);
            viewHolder.likenum = convertView.findViewById(R.id.likenum);
            viewHolder.like = convertView.findViewById(R.id.like);
            convertView.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder)convertView.getTag();
        }
        final Comment comment = comments.get(position);
        viewHolder.username.setText(comment.getUsername());
        viewHolder.avatar.setImageBitmap(comment.getAvatar());
        viewHolder.date.setText(comment.getFormattedDate());
        viewHolder.content.setText(comment.getContent());
        viewHolder.likenum.setText(Integer.toString(comment.getLikenum()));
        viewHolder.like.setImageResource(Storage2Status.currentUser.whetherLike(comment.getId()) ? R.mipmap.red : R.mipmap.white);
        viewHolder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 检查当前用户之前是否点赞了该评论
                boolean before = Storage2Status.currentUser.whetherLike(comment.getId());
                DB db = DB.getInstance(context);
                if(before){
                    // 取消对该评论的点赞
                    viewHolder.like.setImageResource(R.mipmap.white);
                    comment.cancelLike();
                    Storage2Status.currentUser.cancelLike(comment.getId());
                }
                else{
                    // 点赞该评论
                    viewHolder.like.setImageResource(R.mipmap.red);
                    comment.doLike();
                    Storage2Status.currentUser.doLike(comment.getId());
                }

                notifyDataSetChanged();
                db.updateComment(comment);
                db.updateUser(Storage2Status.currentUser);
            }
        });
        return convertView;
    }

    private class ViewHolder {
        public ImageView avatar;
        public TextView username;
        public TextView date;
        public TextView content;
        public ImageButton like;
        public TextView likenum;
    }
}
