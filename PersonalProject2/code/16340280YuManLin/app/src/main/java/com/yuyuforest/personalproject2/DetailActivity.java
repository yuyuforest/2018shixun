package com.yuyuforest.personalproject2;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

public class DetailActivity extends AppCompatActivity {
    private RelativeLayout top;
    private ListView operationList;
    private ImageButton starButton;
    // private boolean collect;
    private boolean fullStar;

    private DynamicReceiver dynamicReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        top = findViewById(R.id.top);
        operationList = findViewById(R.id.bottom);
        starButton = findViewById(R.id.starButton);
        // collect = false;
        fullStar = false;

        Bundle bundle = this.getIntent().getExtras();
        setDetail(bundle.getString("foodName"));

        String[] operations = getResources().getStringArray(R.array.operations);
        operationList.setAdapter(new ArrayAdapter<>(this, R.layout.operation, operations));

        // 注册用以发送通知、刷新widget的动态广播
        dynamicReceiver = new DynamicReceiver();
        IntentFilter dynamicFilter = new IntentFilter();
        dynamicFilter.addAction("com.yuyuforest.personalproject2.MyDynamicFilter");
        dynamicFilter.addAction("com.yuyuforest.personalproject2.MyWidgetDynamicFilter");
        registerReceiver(dynamicReceiver, dynamicFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // 注销动态广播
        unregisterReceiver(dynamicReceiver);
    }

    // 收藏按钮点击事件
    public void onCollectButtonClick(View view){

        Toast.makeText(DetailActivity.this, "已收藏", Toast.LENGTH_SHORT).show();

        // EventBus
        TextView name = findViewById(R.id.name);
        TextView kind = findViewById(R.id.kind);
        String foodName = (String) name.getText();
        String shortKind = (String) kind.getText().subSequence(0, 1);
        EventBus.getDefault().post(new MainActivity.MessageEvent(foodName, shortKind));

        // 发送动态广播，以显示通知
        Bundle bundle = new Bundle();
        bundle.putString("foodName", foodName);
        Intent intentBroadcast = new Intent();
        intentBroadcast.setAction("com.yuyuforest.personalproject2.MyDynamicFilter");
        intentBroadcast.putExtras(bundle);
        sendBroadcast(intentBroadcast);

        // 发送动态广播，以更新widget
        Intent widgetIntentBroadcast = new Intent();
        widgetIntentBroadcast.setAction("com.yuyuforest.personalproject2.MyWidgetDynamicFilter");
        widgetIntentBroadcast.putExtras(bundle);
        sendBroadcast(widgetIntentBroadcast);
    }


    // 按下顶部的返回按钮返回食品/收藏夹列表
    public void onBackButtonClick(View view){
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

    public class DynamicReceiver extends BroadcastReceiver {
        private static final String DYNAMICATION = "com.yuyuforest.personalproject2.MyDynamicFilter";
        private static final String WIDGETDYNAMICACTION = "com.yuyuforest.personalproject2.MyWidgetDynamicFilter";

        @Override
        public void onReceive(Context context, Intent intent){
            // 收到需要发送通知的广播
            if(intent.getAction().equals(DYNAMICATION)) {

                // 获取数据
                Bundle bundle = intent.getExtras();

                // 创建要附加到通知上的PendingIntent
                Intent intent2 = new Intent(DetailActivity.this, MainActivity.class);
                Bundle bundle2 = new Bundle();
                bundle2.putBoolean("collect", true);    // 指示添加到收藏夹
                intent2.putExtras(bundle2);
                PendingIntent pendingIntent;
                pendingIntent = PendingIntent.getActivity(DetailActivity.this, 0,
                        intent2, PendingIntent.FLAG_CANCEL_CURRENT);

                String channelID = "collect";
                String channelName = "CollectChannel";
                NotificationChannel channel = new NotificationChannel(channelID,
                        channelName, NotificationManager.IMPORTANCE_HIGH);
                NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                manager.createNotificationChannel(channel);
                Notification.Builder builder = new Notification.Builder(context, channelID);

                // 发送通知
                builder.setContentTitle("已收藏")
                        .setContentText(bundle.getString("foodName"))
                        .setTicker("您有一条新消息")
                        .setSmallIcon(R.mipmap.full_star)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true);
                Notification notification = builder.build();
                manager.notify(0, notification);
            }
            else if(intent.getAction().equals(WIDGETDYNAMICACTION)) {
                // 收到需要刷新widget的广播
                Bundle bundle = intent.getExtras();
                CharSequence newText = (CharSequence) bundle.get("foodName");
                RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.my_widget);
                views.setTextViewText(R.id.widgetText, "已收藏 " + newText);

                Intent i = new Intent(context, MainActivity.class);
                Bundle bundle2 = new Bundle();
                bundle2.putBoolean("collect", true);
                i.putExtras(bundle2);
                PendingIntent pending = PendingIntent.getActivity(context, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);

                views.setOnClickPendingIntent(R.id.widgetImage, pending);
                ComponentName cn = new ComponentName(context, MyWidget.class);
                AppWidgetManager.getInstance(context).updateAppWidget(cn, views);
            }
        }
    }
}
