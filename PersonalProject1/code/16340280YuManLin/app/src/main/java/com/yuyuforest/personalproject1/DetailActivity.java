package com.yuyuforest.personalproject1;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class DetailActivity extends AppCompatActivity {
    private RelativeLayout top;
    private ListView operationList;
    private ImageButton starButton;
    private boolean collect;
    private boolean fullStar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        top = findViewById(R.id.top);
        operationList = findViewById(R.id.bottom);
        starButton = findViewById(R.id.starButton);
        collect = false;
        fullStar = false;

        setDetail(this.getIntent().getStringExtra("name"));

        String[] operations = getResources().getStringArray(R.array.operations);
        operationList.setAdapter(new ArrayAdapter<>(this, R.layout.operation, operations));
    }

    // 收藏按钮点击事件
    public void onCollectButtonClick(View view){
        collect = true;
        Toast.makeText(DetailActivity.this, "已收藏", Toast.LENGTH_SHORT).show();
    }

    // 按下顶部的返回按钮返回食品/收藏夹列表
    public void onBackButtonClick(View view){
        Intent intent = new Intent(DetailActivity.this, CollectionActivity.class);
        TextView name = findViewById(R.id.name);
        TextView kind = findViewById(R.id.kind);
        intent.putExtra("name", name.getText());
        intent.putExtra("shortKind", kind.getText().subSequence(0, 1));
        intent.putExtra("collect", collect);
        setResult(RESULT_OK, intent);
        finish();
    }

    // 星形按钮点击事件
    public void onStarButtonClick(View view){
        if(fullStar){
            starButton.setBackgroundResource(R.mipmap.empty_star);
            fullStar = false;
        }
        else{
            starButton.setBackgroundResource(R.mipmap.full_star);
            fullStar = true;
        }
    }

    // 按下底部的系统返回按钮返回
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(DetailActivity.this, CollectionActivity.class);
        TextView name = findViewById(R.id.name);
        TextView kind = findViewById(R.id.kind);
        intent.putExtra("name", name.getText());
        intent.putExtra("shortKind", kind.getText().subSequence(0, 1));
        intent.putExtra("collect", collect);
        setResult(RESULT_OK, intent);
        finish();
    }

    // 设置食品详情（包括名称、种类、营养物质、颜色）
    private void setDetail(String n){
        TextView name = findViewById(R.id.name);
        TextView kind = findViewById(R.id.kind);
        TextView nutrient = findViewById(R.id.nutrient);
        String k = FoodMap.getInstance().getKind(n);
        String nu = "富含 " + FoodMap.getInstance().getNutrient(n);
        String c = FoodMap.getInstance().getColor(n);

        int id = getResources().getIdentifier(c, "color", this.getPackageName());
        top.setBackgroundColor(getColor(id));
        name.setText(n);
        kind.setText(k);
        nutrient.setText(nu);
    }
}
