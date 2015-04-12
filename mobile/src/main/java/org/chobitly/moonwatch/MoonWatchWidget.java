package org.chobitly.moonwatch;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.RemoteViews;

import org.chobitly.utils.MoonRotateUtil;

import java.util.Calendar;
import java.util.Date;

/**
 * Implementation of App Widget functionality.
 */
public class MoonWatchWidget extends AppWidgetProvider {

    private static int[] monthRes = {R.drawable.month_1, R.drawable.month_2, R.drawable.month_3,
            R.drawable.month_4, R.drawable.month_5, R.drawable.month_6,
            R.drawable.month_7, R.drawable.month_8, R.drawable.month_9,
            R.drawable.month_10, R.drawable.month_11, R.drawable.month_12};
    private static int[] dayRes = {R.drawable.date_1, R.drawable.date_2, R.drawable.date_3,
            R.drawable.date_4, R.drawable.date_5, R.drawable.date_6, R.drawable.date_7,
            R.drawable.date_8, R.drawable.date_9, R.drawable.date_10, R.drawable.date_11,
            R.drawable.date_12, R.drawable.date_13, R.drawable.date_14, R.drawable.date_15,
            R.drawable.date_16, R.drawable.date_17, R.drawable.date_18, R.drawable.date_19,
            R.drawable.date_20, R.drawable.date_21, R.drawable.date_22, R.drawable.date_23,
            R.drawable.date_24, R.drawable.date_25, R.drawable.date_26, R.drawable.date_27,
            R.drawable.date_28, R.drawable.date_29, R.drawable.date_30, R.drawable.date_31};

    /**
     * @see {moon_watch_widget_info.xml android:updatePeriodMillis="1800000" 半小时触发一次这里的刷新}
     */
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        updateAllWidget(context, appWidgetManager, appWidgetIds);
        if (TimeService.getInstance() != null) {//如果服务已经启动
            TimeService.getInstance().checkUpdate();//检查程序更新
        } else {//如果服务未启动（或被杀死）
            context.startService(new Intent(context, TimeService_.class));//启动服务
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
        for (int appWidgetId : appWidgetIds) {
            MoonWatchWidgetConfigureActivity.deleteShowDatePref(context, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        if (TimeService.getInstance() != null) {//如果服务已经启动
            TimeService.getInstance().updateAllWidget();//更新界面
        } else {//如果服务未启动（或被杀死）
            context.startService(new Intent(context, TimeService_.class));//启动服务
        }
    }

    @Override
    public void onDisabled(Context context) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int widgetCount = appWidgetManager.getAppWidgetIds(new ComponentName(context, MoonWatchWidget.class)).length;
        widgetCount += appWidgetManager.getAppWidgetIds(new ComponentName(context, MoonWatchMiniWidget.class)).length;
        widgetCount += appWidgetManager.getAppWidgetIds(new ComponentName(context, MoonWatchLargeWidget.class)).length;
        widgetCount += appWidgetManager.getAppWidgetIds(new ComponentName(context, MoonWatchExtraWidget.class)).length;
        if (widgetCount == 0) {// 没有任何活跃的月相表小部件的话
            context.stopService(new Intent(context, TimeService_.class));// 停止服务
        }
    }

    static void updateAllWidget(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);//更新小部件的月相部分和时间部分
        }
    }

    /**
     * @see {moon_watch_widget_info.xml android:updatePeriodMillis="1800000" 半小时刷新一次月相}
     */
    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.moon_watch_widget);
        // 设置Moon图片的旋转角度
        views.setImageViewBitmap(R.id.imageView_Moon,
                MoonRotateUtil.getRotateImage(context, R.drawable.watch_inner_moon, MoonRotateUtil.getDegree()));
        // 根据用户设置确定是否显示时间控件
        boolean showDate = MoonWatchWidgetConfigureActivity.loadShowDatePref(context, appWidgetId);
        views.setViewVisibility(R.id.imageView_Month, showDate ? View.VISIBLE : View.GONE);
        views.setViewVisibility(R.id.imageView_Day, showDate ? View.VISIBLE : View.GONE);
        // 根据当前日期和时间设置小部件
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(System.currentTimeMillis()));
        // 获取时间并控制指针
        views.setImageViewBitmap(R.id.imageView_Minute,
                MoonRotateUtil.getRotateImage(context, R.drawable.watch_minute,
                        cal.get(Calendar.MINUTE) * 6f));
        views.setImageViewBitmap(R.id.imageView_Hour,
                MoonRotateUtil.getRotateImage(context, R.drawable.watch_hour,
                        cal.get(Calendar.HOUR) * 30f + cal.get(Calendar.MINUTE) * 0.5f));
        // 根据当前日期设置小部件
        views.setImageViewResource(R.id.imageView_Month, monthRes[cal.get(Calendar.MONTH)]);
        views.setImageViewResource(R.id.imageView_Day, dayRes[cal.get(Calendar.DAY_OF_MONTH) - 1]);
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

}