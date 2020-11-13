package com.example.linemsgcatch.data.manager

import androidx.appcompat.app.AppCompatActivity
import java.util.*

/**
 * Activity 管理類
 * 幫助實現全域 dialog 顯示
 * https://codertw.com/android-%E9%96%8B%E7%99%BC/332275/
 */

private val activityStack = Stack<AppCompatActivity>()

//新增Activity到堆疊
fun addActivity(activity: AppCompatActivity) {
    activityStack.add(activity)
}

//獲取當前Activity（堆疊中最後一個壓入的）
fun currentActivity(): AppCompatActivity {
    return activityStack.lastElement()
}

//移除Activity到堆疊
fun removeActivity(activity: AppCompatActivity) {
    activityStack.remove(activity)
}

