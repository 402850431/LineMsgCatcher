package com.example.linemsgcatch.service

import android.app.Notification
import android.graphics.drawable.Icon
import android.os.Build
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.example.linemsgcatch.tool.GetNotificationEvent
import org.greenrobot.eventbus.EventBus


class NotificationMonitorService : NotificationListenerService() {
    override fun onNotificationPosted(sbn: StatusBarNotification) {
//        super.onNotificationPosted(sbn);
        val extras = sbn.notification.extras
        val title = extras.getString(Notification.EXTRA_TITLE)
        val text = extras.getString(Notification.EXTRA_TEXT)
        var pic: Icon? = null
//        var testUri: Uri? = null
        try { //取得通知欄的小圖示
//            val iconId = extras.getInt(Notification.EXTRA_LARGE_ICON)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                pic = sbn.notification.getLargeIcon() as Icon
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

//        val largeIcon = sbn.notification.getLargeIcon().resId //取得通知欄的大圖示
        if (title == "Paul股期A群" || title == "Android_Cheryl" || title == "Android_Cheryl(重要)" || title == "me") {

            val splitStr = text?.split(" : ")
            val name = if (splitStr?.size ?: 0 > 1) splitStr?.firstOrNull() else title
            val content = if (splitStr?.size ?: 0 > 1) splitStr?.get(1) else text

            EventBus.getDefault().post(
                GetNotificationEvent(
                    name,
                    content,
                    pic
                )
            )
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) { //通知被刪除將觸發
    }

}