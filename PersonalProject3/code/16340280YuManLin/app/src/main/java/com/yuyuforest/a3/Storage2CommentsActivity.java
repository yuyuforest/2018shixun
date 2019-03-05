package com.yuyuforest.a3;

import android.content.DialogInterface;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;

public class Storage2CommentsActivity extends AppCompatActivity {

    private ListView comments;
    private Storage2CommentsAdapter commentsAdapter;
    private EditText edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storage2_comments);

        comments = findViewById(R.id.comments);
        edit = findViewById(R.id.edit);

        // 获取所有评论
        DB db = DB.getInstance(Storage2CommentsActivity.this);
        final ArrayList<Comment> commentsList = db.getComments();

        // 设置评论列表视图的适配器
        commentsAdapter = new Storage2CommentsAdapter(Storage2CommentsActivity.this, commentsList);
        comments.setAdapter(commentsAdapter);

        // 设置评论列表项的点击事件：显示评论用户和电话号码
        comments.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String message = "";
                Comment comment = (Comment) commentsAdapter.getItem(position);
                message += "Username:\t" + comment.getUsername() + "\n";

                try{
                    // 查询电话号码
                    Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " = \"" + comment.getUsername() + "\"",
                            null, null);
                    if(cursor.moveToFirst()){
                        message += "Phone: ";
                        if(cursor.getCount() > 1) message += "\n";
                        do {
                            message += "\t\t" + cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)) + "\n";
                        } while (cursor.moveToNext());
                    }
                    else {
                        message += "Phone number does not exist.";
                    }
                } catch (SecurityException se) {
                    // 无法查询电话号码，提醒检查读取通讯录的权限
                    message += "Phone number can't be queried.\nPlease check if permission for reading contacts is granted.\n";
                }

                // 弹出消息框
                AlertDialog.Builder builder = new AlertDialog.Builder(Storage2CommentsActivity.this);
                builder.setTitle("Info");
                builder.setMessage(message);
                builder.setPositiveButton("OK", null);
                builder.show();
            }
        });

        // 设置评论的长按事件：删除或举报评论
        comments.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Storage2CommentsActivity.this);

                final Comment comment = (Comment) commentsAdapter.getItem(position);
                if(Storage2Status.currentUser.getUsername().equals(comment.getUsername())){
                    // 删除自己的评论
                    builder.setTitle("Delete or not?");
                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            DB db = DB.getInstance(Storage2CommentsActivity.this);
                            db.deleteComment(commentsAdapter.getItemId(position));
                            commentsAdapter.remove(position);
                        }
                    });
                    builder.setNegativeButton("NO", null);
                }
                else{
                    // 举报他人的评论
                    builder.setTitle("Report or not?");
                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(Storage2CommentsActivity.this, "Already reported.", Toast.LENGTH_SHORT).show();
                        }
                    });
                    builder.setNegativeButton("NO", null);
                }

                builder.show();
                return true;
            }
        });
    }

    // 发送评论
    public void onSendClick(View view) {
        if(TextUtils.isEmpty(edit.getText())) {
            // 评论为空，发送失败
            Toast.makeText(Storage2CommentsActivity.this, "Comment can't be empty.", Toast.LENGTH_SHORT).show();
            return;
        }
        User currentUser = Storage2Status.currentUser;
        Comment comment = new Comment(currentUser.getUsername(), edit.getText().toString(), new Date(), 0, currentUser.getAvatar());
        DB db = DB.getInstance(Storage2CommentsActivity.this);
        long id = db.insertComment(comment);
        comment.setId(id);
        commentsAdapter.add(comment);
        edit.setText("");
    }

}
