package com.example.linemsgcatch.ui.normal

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.greenrobot.eventbus.EventBus

open class BaseEventBusActivity : AppCompatActivity() {

    private lateinit var eventBus: EventBus

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //訂閱監聽
        eventBus = EventBus.getDefault()
        eventBus.register(this)

    }

    override fun onDestroy() {
        super.onDestroy()
        //取消註冊釋放資源
        eventBus.unregister(this)
    }
}
