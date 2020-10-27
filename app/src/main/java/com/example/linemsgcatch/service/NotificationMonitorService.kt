package com.example.linemsgcatch.service

import android.app.Notification
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.example.linemsgcatch.tool.GetNotificationEvent
import org.greenrobot.eventbus.EventBus


class NotificationMonitorService : NotificationListenerService() {
    override fun onNotificationPosted(sbn: StatusBarNotification) {
        Log.e(">>>", "get notification")
//        super.onNotificationPosted(sbn);
        val extras = sbn.notification.extras
        val packageName = sbn.packageName
        val title = extras.getString(Notification.EXTRA_TITLE)
        val text = extras.getString(Notification.EXTRA_TEXT)
        var smallIcon: Drawable? = null
        try { //取得通知欄的小圖示
            val iconId = extras.getInt(Notification.EXTRA_SMALL_ICON)
            if (extras.containsKey(Notification.EXTRA_PICTURE)) {
                val bmp = extras[Notification.EXTRA_PICTURE] as Bitmap
            }
            val manager = packageManager
            val resources = manager.getResourcesForApplication(packageName)
            smallIcon = resources.getDrawable(iconId)
        } catch (e: Exception) {
            e.printStackTrace()
        }

//        val largeIcon = sbn.notification.getLargeIcon().resId //取得通知欄的大圖示

//        val activity = MainActivity()
        if (title == "Paul股期A群" || title == "Android_Cheryl") {
            EventBus.getDefault().post(
                GetNotificationEvent(
                    packageName,
                    title,
                    text,
                    smallIcon
                )
            )
        }
//        activity.show(packageName,title,text,smallIcon)//傳送資料

//        activity.show(packageName,title,text,smallIcon,largeIcon)//傳送資料
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) { //通知被刪除將觸發
    }
}