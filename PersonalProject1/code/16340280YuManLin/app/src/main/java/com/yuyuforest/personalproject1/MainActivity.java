package com.yuyuforest.personalproject1;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private EditText searchInput;
    private Button searchButton;
    private RadioGroup searchTypes;
    private AlertDialog.Builder alert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchInput = (EditText)findViewById(R.id.searchInput);
        searchButton = (Button)findViewById(R.id.searchButton);
        searchTypes = (RadioGroup)findViewById(R.id.searchTypes);

        // 创建对话框
        alert = new AlertDialog.Builder(MainActivity.this);
        alert.setTitle("提示");
        alert.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(MainActivity.this, "对话框“取消“按钮被点击", Toast.LENGTH_SHORT).show();
            }
        });
        alert.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(MainActivity.this, "对话框“确定“按钮被点击", Toast.LENGTH_SHORT).show();
            }
        });
        alert.create();

        // 设置单选按钮组的切换按钮事件
        searchTypes.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = (RadioButton)findViewById(searchTypes.getCheckedRadioButtonId());
                Toast.makeText(MainActivity.this, radioButton.getText().toString() + "被选中", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 搜索按钮的点击事件
     * @param view
     */
    public void onSearchButtonClick(View view) {
        // 如果搜索内容为空，弹出Toast“搜索内容不能为空”
        if(TextUtils.isEmpty(searchInput.getText().toString())){
            Toast.makeText(MainActivity.this, "搜索内容不能为空", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.equals(searchInput.getText().toString(), "Health")){
            // 如果搜索内容为"Health"，弹出对话框“XX搜索成功”，其中XX表示搜索类型
            RadioButton radioButton = (RadioButton)findViewById(searchTypes.getCheckedRadioButtonId());
            alert.setMessage(radioButton.getText().toString() + "搜索成功");
            alert.show();
        }
        else if(TextUtils.equals(searchInput.getText().toString(), "food")){
            Intent intent = new Intent(MainActivity.this, CollectionActivity.class);
            startActivity(intent);
        }
        else{
            alert.setMessage("搜索失败");
            alert.show();
        }
    }
}
