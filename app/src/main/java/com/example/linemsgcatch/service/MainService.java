package com.example.linemsgcatch.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.os.Process;

import androidx.annotation.Nullable;

import java.util.List;

public class MainService extends Service {@Override
public void onCreate() {
    super.onCreate();
    new Thread(new Runnable() {
        @Override
        public void run() { // 每 10 秒檢查通知欄擷取是否失效
            while (true){
                try{
                    Thread.sleep((10*1000));
                    isNotificationMonitorService();
                }catch (Exception e){}
            }
        }
    }).start();
}

    private void isNotificationMonitorService() {
        // 檢查通知欄擷取是否失效，
        // 如果失效 將呼叫 restartNotificationMonitorService() 重新啟動 通知欄擷取
        // 如果未失效 將不動作
        ComponentName componentName = new ComponentName(this, NotificationMonitorService.class);
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        boolean isRunning = false;
        List<ActivityManager.RunningServiceInfo> runningServiceInfo = activityManager.getRunningServices(Integer.MAX_VALUE);    if (runningServiceInfo == null ) { return; }

        for (ActivityManager.RunningServiceInfo service : runningServiceInfo) {
            if (service.service.equals(componentName)) {
                if (service.pid == Process.myPid() ) {
                    isRunning = true;
                }
            }
        }

        if (isRunning) { return; }

        restartNotificationMonitorService();
    }
    private void restartNotificationMonitorService() { // 重新啟動 通知欄擷取
        ComponentName componentName = new ComponentName(this,  NotificationMonitorService.class);
        PackageManager packageManager = getPackageManager();
        packageManager.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        packageManager.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
