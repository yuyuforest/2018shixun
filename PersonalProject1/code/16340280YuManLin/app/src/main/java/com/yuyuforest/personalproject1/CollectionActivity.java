package com.yuyuforest.personalproject1;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.FadeInAnimator;

public class CollectionActivity extends Activity {
    private FloatingActionButton switchButton;
    private RecyclerView foodList;
    private FoodListAdapter foodListAdapter;
    private ListView collectionList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);

        switchButton = findViewById(R.id.switchButton);

        // 设置食品列表
        foodList = findViewById(R.id.foodList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(
                CollectionActivity.this, LinearLayoutManager.VERTICAL, false);
        foodList.setLayoutManager(layoutManager);
        final ArrayList<Food> data = FoodMap.getInstance().getSimpleFoodList();
        foodListAdapter = new FoodListAdapter(CollectionActivity.this, data);
        foodListAdapter.setOnItemClickListener(new FoodListAdapter.OnItemClickListener() {
            // 设置食品列表项的点击事件：跳转详情界面
            @Override
            public void onClick(int pos) {
                Intent intent = new Intent(CollectionActivity.this, DetailActivity.class);
                String name = data.get(pos).getName();
                intent.putExtra("name", name);
                startActivityForResult(intent, 0);
            }

            // 设置食品列表项的长按事件：删除
            @Override
            public void onLongClick(int pos) {
                Toast.makeText(CollectionActivity.this, "删除" + data.get(pos).getName(), Toast.LENGTH_SHORT).show();
                foodListAdapter.notifyItemRemoved(pos);
                data.remove(pos);
                //notifyItemRemoved(pos);
                foodListAdapter.notifyItemRangeChanged(pos, foodListAdapter.getItemCount());
            }
        });
        foodList.setAdapter(foodListAdapter);
        foodList.setItemAnimator(new FadeInAnimator()); // 添加ItemAnimator，引入项动画
        // 然后可在FoodListHolder里实现AnimateViewHolder接口，以覆盖这个ItemAnimator里的动画效果


        // 设置收藏夹列表
        collectionList = findViewById(R.id.collectionList);
        final ArrayList<Food> collections = new ArrayList<>();
        collections.add(new Food("收藏夹", "*"));
        final CollectionListAdapter collectionListAdapter =
                new CollectionListAdapter(CollectionActivity.this, collections);
        collectionList.setAdapter(collectionListAdapter);
        // 设置收藏夹列表项的点击事件：跳转食品详情界面
        collectionList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0) return;   // 如果是第一项（即 * 收藏夹），则无反应

                Intent intent = new Intent(CollectionActivity.this, DetailActivity.class);
                intent.putExtra("name", collections.get(position).getName());
                startActivityForResult(intent, 0);
            }
        });
        // 设置收藏夹列表项的长按事件：删除项
        collectionList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                if(position == 0) return true;   // 如果是第一项（即 * 收藏夹），则无反应

                AlertDialog.Builder builder=new AlertDialog.Builder(CollectionActivity.this);
                builder.setTitle("删除");
                builder.setMessage("确定删除");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        collectionListAdapter.remove(position);
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.show();
                return true;
            }
        });
    }

    // 设置悬浮按钮的点击事件：切换视图
    public void onSwitchButtonClick(View view){
        if(foodList.getVisibility() == View.VISIBLE){
            foodList.setVisibility(View.INVISIBLE);
            collectionList.setVisibility(View.VISIBLE);
            switchButton.setImageResource(R.mipmap.mainpage);
        }
        else{
            collectionList.setVisibility(View.INVISIBLE);
            foodList.setVisibility(View.VISIBLE);
            switchButton.setImageResource(R.mipmap.collect);
        }
    }

    // 处理先前跳转的活动的返回结果
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK){
            Bundle bundle = data.getExtras();
            String name = bundle.getString("name");
            String shortKind = bundle.getString("shortKind");
            boolean collect = bundle.getBoolean("collect"); // 在食品详情界面是否点击了“收藏”按钮，是则加入收藏夹列表
            if(collect){
                CollectionListAdapter adapter = (CollectionListAdapter)collectionList.getAdapter();
                Food food = new Food(name, shortKind);
                adapter.add(food);
            }
        }
    }
}
