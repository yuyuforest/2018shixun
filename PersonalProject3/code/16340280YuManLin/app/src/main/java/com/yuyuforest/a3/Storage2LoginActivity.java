package com.yuyuforest.a3;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

public class Storage2LoginActivity extends AppCompatActivity {
    private RadioGroup switchEntry;
    private EditText username;
    private ImageButton newAvatar;
    private EditText newPassword;
    private EditText confirmPassword;
    private Bitmap avatar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storage2_login);

        switchEntry = findViewById(R.id.switchEntry);
        username = findViewById(R.id.username);
        newAvatar = findViewById(R.id.newAvatar);
        newPassword = findViewById(R.id.newPassword);
        confirmPassword = findViewById(R.id.confirmPassword);
        avatar = ((BitmapDrawable)getDrawable(R.mipmap.me)).getBitmap();	// 将avatar初始化为默认头像

        switchEntry.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int checked = switchEntry.getCheckedRadioButtonId();
                if(checked == R.id.login) {
                    newAvatar.setVisibility(View.GONE);
                    newPassword.setHint("Password");
                    confirmPassword.setVisibility(View.GONE);
                }
                else {
                    newAvatar.setVisibility(View.VISIBLE);
                    newPassword.setHint("New Password");
                    confirmPassword.setVisibility(View.VISIBLE);
                }
                clearUI();
            }
        });
    }

    // 处理从图库选择的图片，确定新头像
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try{
            if (data != null) {
                Uri uri = data.getData();
                avatar = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                avatar = ThumbnailUtils.extractThumbnail(avatar, 150, 150, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
                newAvatar.setBackground(new BitmapDrawable(getResources(), avatar));
            }
            super.onActivityResult(requestCode, resultCode, data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 从手机图库读取图片
    public void loadPicture(View view) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 0);
    }

    // OK按钮的点击事件：登录/注册
    public void onOkClick(View view) {
        DB db = DB.getInstance(Storage2LoginActivity.this);
        String name = username.getText().toString();
        String pw = newPassword.getText().toString();

        if(TextUtils.isEmpty(name)) {
            Toast.makeText(Storage2LoginActivity.this, "Username can't be empty.", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(pw)) {
            Toast.makeText(Storage2LoginActivity.this, "Password can't be empty.", Toast.LENGTH_SHORT).show();
            return;
        }

        User user = db.getUser(name);
        if(switchEntry.getCheckedRadioButtonId() == R.id.register) {    // 注册
            if(!confirmPassword.getText().toString().equals(pw)) {
                // 注册失败，密码不一致
                Toast.makeText(Storage2LoginActivity.this, "Password Mismatch.", Toast.LENGTH_SHORT).show();
                return;
            }
            if(user == null){
                // 注册成功
                user = new User(name, pw, avatar, new ArrayList<Long>());
                db.insertUser(user);
                Storage2Status.currentUser = user;
                Toast.makeText(Storage2LoginActivity.this, "Register successfully.", Toast.LENGTH_SHORT).show();
            }
            else{
                // 注册失败，用户名已存在
                Toast.makeText(Storage2LoginActivity.this, "Username exists.", Toast.LENGTH_SHORT).show();
            }
        }
        else {  // 登录
            if(user == null){
                // 登录失败，用户名不存在
                Toast.makeText(Storage2LoginActivity.this, "Username doesn't exist.", Toast.LENGTH_SHORT).show();
            }
            else if(!pw.equals(user.getPassword())){
                // 登录失败，密码不正确
                Toast.makeText(Storage2LoginActivity.this, "Invalid password.", Toast.LENGTH_SHORT).show();
            }
            else{
                // 登录成功
                Storage2Status.currentUser = user;
                clearUI();

                // 跳转到评论页面
                Intent intent = new Intent(Storage2LoginActivity.this, Storage2CommentsActivity.class);
                startActivity(intent);
            }
        }
    }

    // CLEAR按钮的点击事件：清空输入框
    public void onClearClick(View view) {
        username.setText("");
        newPassword.setText("");
        confirmPassword.setText("");
    }

    // 清空登陆/注册界面
    private void clearUI() {
        username.setText("");
        newPassword.setText("");
        confirmPassword.setText("");
        newAvatar.setBackground(getDrawable(R.mipmap.add));
    }
}
