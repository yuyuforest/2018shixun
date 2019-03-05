package com.yuyuforest.a3;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class Storage1LoginActivity extends AppCompatActivity {
    private EditText newPassword;
    private EditText confirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storage1_login);

        newPassword = findViewById(R.id.newPassword);
        confirmPassword = findViewById(R.id.confirmPassword);

        String password = getSharedPreferences("MyPreference", Context.MODE_PRIVATE).getString("password", "");
        if(password != "") {
            // 不是首次登陆，所以改变界面显示
            newPassword.setVisibility(View.INVISIBLE);
            confirmPassword.setHint("Password");
        }
    }

    public void onLoginClick(View view) {
        String password = getSharedPreferences("MyPreference", Context.MODE_PRIVATE).getString("password", "");
        if(password == "") {
            // 首次登陆
            if(TextUtils.isEmpty(newPassword.getText().toString())) {
                Toast.makeText(Storage1LoginActivity.this, "Password cannot be empty.", Toast.LENGTH_SHORT).show();
            }
            else if(!confirmPassword.getText().toString().equals(newPassword.getText().toString())) {
                Toast.makeText(Storage1LoginActivity.this, "Password Mismatch.", Toast.LENGTH_SHORT).show();
            }
            else {
                // 保存密码并登陆
                Context context = Storage1LoginActivity.this;
                SharedPreferences sharedPreferences = context.getSharedPreferences("MyPreference", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("password", newPassword.getText().toString());
                editor.commit();
                Intent intent = new Intent(Storage1LoginActivity.this, Storage1NoteActivity.class);
                startActivity(intent);
            }
        }
        else if(!password.equals(confirmPassword.getText().toString())){
            Toast.makeText(Storage1LoginActivity.this, "Invalid Password.", Toast.LENGTH_SHORT).show();
        }
        else {
            Intent intent = new Intent(Storage1LoginActivity.this, Storage1NoteActivity.class);
            startActivity(intent);
        }
    }

    public void onClearClick(View view) {
        newPassword.setText("");
        confirmPassword.setText("");
    }
}
