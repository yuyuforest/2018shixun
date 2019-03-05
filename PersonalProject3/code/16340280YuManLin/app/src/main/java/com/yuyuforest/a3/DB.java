package com.yuyuforest.a3;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.ByteArrayOutputStream;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.Date;

public class DB extends SQLiteOpenHelper {
    private static final String DB_NAME = "data";
    private static final String USERS_TABLE = "user";
    private static final String COMMENTS_TABLE = "comment";
    private static final int DB_VERSION = 1;
    private static DB instance;

    public static DB getInstance(Context context) {
        instance = new DB(context);
        return instance;
    }

    private DB(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USERS_TABLE = "CREATE TABLE if not exists "
                + USERS_TABLE
                + " (username TEXT PRIMARY KEY, password TEXT, likes TEXT, avatar BLOB)";
        db.execSQL(CREATE_USERS_TABLE);

        String CREATE_COMMENTS_TABLE = "CREATE TABLE if not exists "
                + COMMENTS_TABLE
                + " (id INTEGER PRIMARY KEY, username TEXT, content TEXT, date INTEGER, likenum INTEGER)";
        db.execSQL(CREATE_COMMENTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    // 由用户名获取用户
    public User getUser(String username) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(USERS_TABLE, new String[]{"username","password", "avatar", "likes"},
                "username=?", new String[]{username}, null, null, null);

        if(!cursor.moveToFirst()) return null;

        String password = cursor.getString(cursor.getColumnIndex("password"));
        String likes = cursor.getString(cursor.getColumnIndex("likes"));
        byte[] blob = cursor.getBlob(cursor.getColumnIndex("avatar"));
        Bitmap avatar = BitmapFactory.decodeByteArray(blob, 0, blob.length);

        db.close();

        return new User(username, password, avatar, likes);
    }

    // 插入新用户
    public void insertUser(User user) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("username", user.getUsername());
        cv.put("password", user.getPassword());

        // 数据库存储头像
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        user.getAvatar().compress(Bitmap.CompressFormat.PNG, 100, os);
        cv.put("avatar", os.toByteArray());

        cv.put("likes", user.getLikes());
        db.insert(USERS_TABLE, null, cv);
        db.close();
    }

    // 修改当前用户所点赞的评论
    public void updateUser(User user) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("likes", user.getLikes());

        String whereClause = "username=?";
        String[] whereArgs = {user.getUsername()};
        db.update(USERS_TABLE, cv, whereClause, whereArgs);
        db.close();
    }

    // 获取所有评论
    public ArrayList<Comment> getComments() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(COMMENTS_TABLE, new String[]{"id","username", "content", "date", "likenum"},
                null, null, null, null, null);

        ArrayList<Comment> comments = new ArrayList<>();

        while(cursor.moveToNext()){
            long id = cursor.getLong(cursor.getColumnIndex("id"));
            String username = cursor.getString(cursor.getColumnIndex("username"));
            String content = cursor.getString(cursor.getColumnIndex("content"));
            long timestamp = cursor.getLong(cursor.getColumnIndex("date"));
            int likenum = cursor.getInt(cursor.getColumnIndex("likenum"));

            // 从用户表中根据用户名获取头像
            Cursor cursorAvatar = db.query(USERS_TABLE, new String[]{"username", "avatar"},
                    "username=?", new String[]{username}, null, null, null);
            cursorAvatar.moveToFirst();
            byte[] blob = cursorAvatar.getBlob(cursorAvatar.getColumnIndex("avatar"));
            Bitmap avatar = BitmapFactory.decodeByteArray(blob, 0, blob.length);

            comments.add(0, new Comment(id, username, content, new Date(timestamp), likenum, avatar));
        }

        db.close();

        return comments;
    }

    // 插入新评论，返回该评论的id
    public long insertComment(Comment comment) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        Integer nullid = null;
        cv.put("id", nullid);
        cv.put("username", comment.getUsername());
        cv.put("content", comment.getContent());
        cv.put("date", comment.getTimestamp());
        cv.put("likenum", comment.getLikenum());

        long id = db.insert(COMMENTS_TABLE, null, cv);
        db.close();

        return id;
    }

    // 修改该评论的点赞数
    public void updateComment(Comment comment) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("likenum", comment.getLikenum());

        String whereClause = "id=?";
        String[] whereArgs = new String[]{Long.toString(comment.getId())};
        db.update(COMMENTS_TABLE, cv, whereClause, whereArgs);
        db.close();
    }

    // 删除评论
    public void deleteComment(long commentID) {
        SQLiteDatabase db = getWritableDatabase();

        String whereClause = "id=?";
        String[] whereArgs = new String[]{Long.toString(commentID)};
        db.delete(COMMENTS_TABLE, whereClause, whereArgs);
        db.close();
    }
}
