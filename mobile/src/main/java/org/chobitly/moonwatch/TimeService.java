package org.chobitly.moonwatch;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.tencent.android.tpush.XGPushConfig;
import com.tencent.android.tpush.XGPushManager;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EService;
import org.androidannotations.annotations.UiThread;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

@EService
public class TimeService extends Service {

    private static TimeService instance = null;

    static TimeService getInstance() {
        return instance;
    }

    // 覆盖基类的抽象方法
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // 在本服务创建时将监听系统时间的BroadcastReceiver注册
    @Override
    public void onCreate() {
        instance = this;
        super.onCreate();
        Log.i("service", "--service created--");
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_TIME_TICK); // 时间的流逝
        intentFilter.addAction(Intent.ACTION_TIME_CHANGED); // 时间被改变，人为设置时间
        registerReceiver(broadcastReceiver, intentFilter);

        XGPushConfig.enableDebug(this, false);
        XGPushManager.registerPush(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("service", "--service started--");
        // 开始服务前先刷新一次UI
        updateAllWidget();
        return START_STICKY;
    }

    // 在服务停止时解注册BroadcastReceiver
    @Override
    public void onDestroy() {
        Log.i("service", "--service destroyed--");
        XGPushManager.unregisterPush(this);

        unregisterReceiver(broadcastReceiver);
        super.onDestroy();
        instance = null;
    }

    // 用于监听系统时间变化Intent.ACTION_TIME_TICK的BroadcastReceiver，此BroadcastReceiver须为动态注册
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("time received", "--receive--");
            updateAllWidget();
        }
    };

    // 根据当前月相、日期和时间设置小部件
    void updateAllWidget() {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        MoonWatchWidget.updateAllWidget(this, appWidgetManager, appWidgetManager.getAppWidgetIds(new ComponentName(this, MoonWatchWidget.class)));
        MoonWatchWidget.updateAllWidget(this, appWidgetManager, appWidgetManager.getAppWidgetIds(new ComponentName(this, MoonWatchMiniWidget.class)));
        MoonWatchWidget.updateAllWidget(this, appWidgetManager, appWidgetManager.getAppWidgetIds(new ComponentName(this, MoonWatchLargeWidget.class)));
        MoonWatchWidget.updateAllWidget(this, appWidgetManager, appWidgetManager.getAppWidgetIds(new ComponentName(this, MoonWatchExtraWidget.class)));
    }

    /**
     * 检查更新
     */
    @Background
    public void checkUpdate() {
        Log.i("check update", "start");
        String baseUrl = "http://fir.im/api/v2/app/version/%s?token=%s";
        String checkUpdateUrl = String.format(baseUrl, "550ebc5482ffd7051a000319", "2091a0c1b02911e4ad0144e8263259fe40a9901c");
        HttpClient httpClient = new DefaultHttpClient();
        //请求超时
        httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
        //读取超时
        httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 20000);
        try {
            HttpGet httpGet = new HttpGet(checkUpdateUrl);
            HttpResponse httpResponse = httpClient.execute(httpGet);
            HttpEntity httpEntity = httpResponse.getEntity();
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                String firResponse = EntityUtils.toString(httpEntity);
                JSONObject versionJsonObj = new JSONObject(firResponse);
                //FIR上当前的versionCode
                int firVersionCode = Integer.parseInt(versionJsonObj.getString("version"));
                //FIR上当前的versionName
                String firVersionName = versionJsonObj.getString("versionShort");
                PackageManager pm = getPackageManager();
                PackageInfo pi = pm.getPackageInfo(getPackageName(),
                        PackageManager.GET_ACTIVITIES);
                if (pi != null) {
                    int currentVersionCode = pi.versionCode;
                    String currentVersionName = pi.versionName;
                    if (firVersionCode > currentVersionCode// versionCode不同，需要更新
                            || (firVersionCode == currentVersionCode && !currentVersionName
                            .equals(firVersionName))) {// 如果本地app的versionCode与Server上的app的versionCode一致，则需要判断versionName.
                        //需要更新
                        Log.i("check update", "need update");
                        notifyToOpenUpdateUrl(versionJsonObj.optString("update_url"),
                                currentVersionName + ": " + versionJsonObj.optString("changelog"));
                    } else {
                        //不需要更新,当前版本高于FIR上的app版本.
                        Log.i("check update", " no need update");
                    }
                }
            }
        } catch (IOException | JSONException | PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @UiThread
    protected void notifyToOpenUpdateUrl(String updateUrl, String description) {
        Log.i("notify update", description);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle(getString(R.string.app_name))
                        .setContentText(description)
                        .setAutoCancel(false)
                        .setDefaults(Notification.DEFAULT_ALL);
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(updateUrl));

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        // stackBuilder.addParentStack(ResultActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(1024);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(1024, mBuilder.build());
    }
}
