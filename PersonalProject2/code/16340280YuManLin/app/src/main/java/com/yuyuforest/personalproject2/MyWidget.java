package com.yuyuforest.personalproject2;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RemoteViews;

/**
 * Implementation of App Widget functionality.
 */
public class MyWidget extends AppWidgetProvider {
    private static final String WIDGETSTATICACTION = "com.yuyuforest.personalproject2.MyWidgetStaticFilter";

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.my_widget);
        views.setTextViewText(R.id.widgetText, widgetText);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        RemoteViews updateView = new RemoteViews(context.getPackageName(), R.layout.my_widget);
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pending = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        updateView.setOnClickPendingIntent(R.id.widgetImage, pending);
        ComponentName cn = new ComponentName(context, MyWidget.class);
        appWidgetManager.updateAppWidget(cn, updateView);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        Bundle bundle = intent.getExtras();
        if(intent.getAction().equals(WIDGETSTATICACTION)) {
            CharSequence newText = (CharSequence) bundle.get("foodName");
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.my_widget);
            views.setTextViewText(R.id.widgetText, "今日推荐 " + newText);
            Intent i = new Intent(context, DetailActivity.class);
            i.putExtras(bundle);
            PendingIntent pending = PendingIntent.getActivity(context, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setOnClickPendingIntent(R.id.widgetImage, pending);
            ComponentName cn = new ComponentName(context, MyWidget.class);
            appWidgetManager.updateAppWidget(cn, views);
        }
    }
}

