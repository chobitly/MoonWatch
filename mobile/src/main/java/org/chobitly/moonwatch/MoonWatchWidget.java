package org.chobitly.moonwatch;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import org.chobitly.utils.MoonRotateUtil;

/**
 * Implementation of App Widget functionality.
 */
public class MoonWatchWidget extends AppWidgetProvider {

    /**
     * @see {moon_watch_widget_info.xml android:updatePeriodMillis="1800000" 半小时触发一次这里的刷新}
     * @param context
     * @param appWidgetManager
     * @param appWidgetIds
     */
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        if(TimeService.getInstance()!=null){
            TimeService.getInstance().checkUpdate();
        }
        //启动服务
        context.startService(new Intent(context, TimeService_.class));
        // There may be multiple widgets active, so update all of them
        final int N = appWidgetIds.length;
        for (int i = 0; i < N; i++) {
            updateAppWidget(context, appWidgetManager, appWidgetIds[i]);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
//        final int N = appWidgetIds.length;
//        for (int i = 0; i < N; i++) {
//        }
    }

    @Override
    public void onEnabled(Context context) {
        //启动服务
        context.startService(new Intent(context, TimeService_.class));
    }

    @Override
    public void onDisabled(Context context) {
        //停止服务
        context.stopService(new Intent(context, TimeService_.class));
    }

    /**
     * @see {moon_watch_widget_info.xml android:updatePeriodMillis="1800000" 半小时刷新一次月相}
     * @param context
     * @param appWidgetManager
     * @param appWidgetId
     */
    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.moon_watch_widget);
        //获取Moon图片当前的旋转角度并在此基础上再转一点
        views.setImageViewBitmap(R.id.imageView_Moon,
                MoonRotateUtil.getRotateImage(context, R.drawable.watch_inner_moon, MoonRotateUtil.getDegree()));
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

}