package com.yuyuforest.personalproject2;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.EventLog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Random;

import jp.wasabeef.recyclerview.animators.FadeInAnimator;

public class MainActivity extends Activity {
    private FloatingActionButton switchButton;
    private RecyclerView foodList;
    private FoodListAdapter foodListAdapter;
    private ListView collectionList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        switchButton = findViewById(R.id.switchButton);
        createFoodList();
        createCollectionList();

        // 注册EventBus
        EventBus.getDefault().register(MainActivity.this);

        // 发送静态广播，以发送通知
        Random random = new Random();
        int index = random.nextInt(foodListAdapter.getItemCount());
        Bundle bundle = new Bundle();
        bundle.putString("foodName", foodListAdapter.getItemName(index));
        String staticAction = "com.yuyuforest.personalproject2.MyStaticFilter";
        Intent intentBroadcast = new Intent(staticAction);
        intentBroadcast.putExtras(bundle);
        intentBroadcast.setComponent(new ComponentName(this.getPackageName(),
                this.getPackageName() + ".MainActivity$StaticReceiver"));
        sendBroadcast(intentBroadcast);

        // 发送静态广播，以刷新widget
        String widgetStaticAction = "com.yuyuforest.personalproject2.MyWidgetStaticFilter";
        Intent widgetIntentBroadcast = new Intent(widgetStaticAction);
        widgetIntentBroadcast.putExtras(bundle);
        widgetIntentBroadcast.setComponent(new ComponentName(this.getPackageName(),
                this.getPackageName() + ".MyWidget"));
        sendBroadcast(widgetIntentBroadcast);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Bundle bundle = intent.getExtras();
        if(bundle != null){
            boolean collect = bundle.getBoolean("collect");
            if(collect && collectionList.getVisibility() == View.INVISIBLE) {
                onSwitchButtonClick(null);
            }
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        // 发送静态广播，以刷新widget
        Random random = new Random();
        int index = random.nextInt(foodListAdapter.getItemCount());
        Bundle bundle = new Bundle();
        bundle.putString("foodName", foodListAdapter.getItemName(index));
        String widgetStaticAction = "com.yuyuforest.personalproject2.MyWidgetStaticFilter";
        Intent widgetIntentBroadcast = new Intent(widgetStaticAction);
        widgetIntentBroadcast.putExtras(bundle);
        widgetIntentBroadcast.setComponent(new ComponentName(this.getPackageName(),
                this.getPackageName() + ".MyWidget"));
        sendBroadcast(widgetIntentBroadcast);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // 注销EventBus
        EventBus.getDefault().unregister(MainActivity.this);
    }

    private void createFoodList() {
        // 设置食品列表
        foodList = findViewById(R.id.foodList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(
                MainActivity.this, LinearLayoutManager.VERTICAL, false);
        foodList.setLayoutManager(layoutManager);
        final ArrayList<FoodShort> data = FoodMap.getInstance().getSimpleFoodList();
        foodListAdapter = new FoodListAdapter(MainActivity.this, data);
        foodListAdapter.setOnItemClickListener(new FoodListAdapter.OnItemClickListener() {
            // 设置食品列表项的点击事件：跳转详情界面
            @Override
            public void onClick(int pos) {
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                String name = data.get(pos).getName();
                Bundle bundle = new Bundle();
                bundle.putString("foodName", name);
                intent.putExtras(bundle);
                startActivityForResult(intent, 0);
            }

            // 设置食品列表项的长按事件：删除
            @Override
            public void onLongClick(int pos) {
                Toast.makeText(MainActivity.this, "删除" + data.get(pos).getName(), Toast.LENGTH_SHORT).show();
                foodListAdapter.notifyItemRemoved(pos);
                data.remove(pos);
                //notifyItemRemoved(pos);
                foodListAdapter.notifyItemRangeChanged(pos, foodListAdapter.getItemCount());
            }
        });
        foodList.setAdapter(foodListAdapter);
        foodList.setItemAnimator(new FadeInAnimator()); // 添加ItemAnimator，引入项动画
        // 然后可在FoodListHolder里实现AnimateViewHolder接口，以覆盖这个ItemAnimator里的动画效果
    }

    private void createCollectionList() {
        // 设置收藏夹列表
        collectionList = findViewById(R.id.collectionList);
        final ArrayList<FoodShort> collections = new ArrayList<>();
        collections.add(new FoodShort("收藏夹", "*"));
        final CollectionListAdapter collectionListAdapter =
                new CollectionListAdapter(MainActivity.this, collections);
        collectionList.setAdapter(collectionListAdapter);
        // 设置收藏夹列表项的点击事件：跳转食品详情界面
        collectionList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0) return;   // 如果是第一项（即 * 收藏夹），则无反应

                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                intent.putExtra("foodName", collections.get(position).getName());
                startActivityForResult(intent, 0);
            }
        });
        // 设置收藏夹列表项的长按事件：删除项
        collectionList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                if(position == 0) return true;   // 如果是第一项（即 * 收藏夹），则无反应

                AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event){
        CollectionListAdapter adapter = (CollectionListAdapter)collectionList.getAdapter();
        FoodShort food = new FoodShort(event.getFoodName(), event.getShortKind());
        adapter.add(food);
    }

    public static class MessageEvent {
        private String foodName;
        private String shortKind;

        public MessageEvent() { }

        public MessageEvent(String fn, String sk) {
            foodName = fn;
            shortKind = sk;
        }

        public String getFoodName() {
            return foodName;
        }

        public String getShortKind() {
            return shortKind;
        }

        public void setFoodName(String fn){
            foodName = fn;
        }

        public void setShortKind(String sk){
            shortKind = sk;
        }
    }

    public static class StaticReceiver extends BroadcastReceiver {
        private static final String STATICACTION = "com.yuyuforest.personalproject2.MyStaticFilter";

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(STATICACTION)) {

                // 获取数据
                Bundle bundle = intent.getExtras();
                Intent intent2 = new Intent(context, DetailActivity.class);
                intent2.putExtras(bundle);
                PendingIntent pendingIntent;
                pendingIntent = PendingIntent.getActivity(context, 0,
                        intent2, PendingIntent.FLAG_CANCEL_CURRENT);

                // 创建要附加到通知上的PendingIntent
                String channelID = "recommend";
                String channelName = "RecommendChannel";
                NotificationChannel channel = new NotificationChannel(channelID,
                        channelName, NotificationManager.IMPORTANCE_HIGH);
                NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                manager.createNotificationChannel(channel);
                Notification.Builder builder = new Notification.Builder(context, channelID);

                // 发送通知
                builder.setContentTitle("今日推荐")
                        .setContentText(bundle.getString("foodName"))
                        .setTicker("您有一条新消息")
                        .setSmallIcon(R.mipmap.empty_star)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true);
                Notification notification = builder.build();
                manager.notify(0, notification);
            }
        }
    }
}
