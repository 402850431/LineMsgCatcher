package com.example.linemsgcatch.ui

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.NotificationManagerCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.linemsgcatch.service.MainService
import com.example.linemsgcatch.data.db.MemberDatabaseHelper
import com.example.linemsgcatch.data.MessageOutput
import com.example.linemsgcatch.R
import com.example.linemsgcatch.tool.GetNotificationEvent
import kotlinx.android.synthetic.main.activity_main.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : BaseEventBusActivity() {

    private var mString = ""
    private var drawableIcon: Drawable? = null
    private var bitmapIcon: Int? = null //resId
    private val mRVAdapter = RVAdapter()
    private var mSql: MemberDatabaseHelper? = null
    private var dataList: MutableList<MessageOutput> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getPermission()
        initSql()
        initRv()
        initCbListener()
        startService(Intent(this, MainService::class.java))
    }

    private fun initCbListener() {
        cb_important.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                val filterDataList = dataList.filter { it.name?.contains("(重要)") == true } as MutableList<MessageOutput>
                mRVAdapter.setData(filterDataList)
            } else {
                mRVAdapter.setData(dataList)
            }
        }
    }

    private fun initSql() {
        mSql = MemberDatabaseHelper(this)
    }

    private fun initRv() {
        rv.apply {
            this.adapter = mRVAdapter
            val linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            linearLayoutManager.reverseLayout
            this.layoutManager = linearLayoutManager
        }


        dataList = mSql?.getData()?: mutableListOf()
        mRVAdapter.setData(dataList)
        rv.scrollToPosition(dataList.size - 1)
    }

    private fun getPermission() {
        if (!isPurview(this)) { // 檢查權限是否開啟，未開啟則開啟對話框
            AlertDialog.Builder(this@MainActivity)
                .setTitle(R.string.app_name)
                .setMessage("請啟用通知欄擷取權限")
                .setIcon(R.mipmap.ic_launcher_round)
                .setOnCancelListener { // 對話框取消事件
                    finish()
                }
                .setPositiveButton(
                    "前往"
                ) { dialog, which ->
                    // 對話框按鈕事件
                    // 跳轉自開啟權限畫面，權限開啟後通知欄擷取服務將自動啟動。
                    startActivity(Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"))
                }.show()
        }
    }

    private fun isPurview(context: Context): Boolean { // 檢查權限是否開啟 true = 開啟 ，false = 未開啟
        val packageNames =
            NotificationManagerCompat.getEnabledListenerPackages(context)
        return packageNames.contains(context.packageName)
    }

    //更新畫面
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: GetNotificationEvent) {
        Log.e(">>>", "GetNotificationEvent")

        val nowTime = SimpleDateFormat("MM/dd hh:mm:ss", Locale.getDefault()).format(System.currentTimeMillis())
        event.apply {
            val splitStr = text?.split(" : ")
            dataList.add(
                MessageOutput(
                    splitStr?.firstOrNull(),
                    splitStr?.get(1),
                    smallIcon,
                    nowTime
                )
            )
            mSql?.addData(splitStr?.firstOrNull(), splitStr?.get(1), nowTime)

            mRVAdapter.addData(dataList.last())
            rv.scrollToPosition(dataList.size - 1)
        }
    }


//--------- rv adapter ---------


    class RVAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        private val mDataList: ArrayList<MessageOutput> = ArrayList()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return  MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.content_msg_list_rv, parent, false))
        }

        override fun getItemCount(): Int = mDataList.size

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val data = mDataList[position]
            when (holder) {
                is MyViewHolder -> {
                    holder.apply {
                        tvName.text = data.name
                        tvMsg.text = data.content
                        tvTime.text = data.time
                        imgIcon.setImageDrawable(data.icon)
                    }
                }
            }
        }
        inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val tvName: TextView = view.findViewById(R.id.tv_name)
            val tvMsg: TextView = view.findViewById(R.id.tv_msg)
            val tvTime: TextView = view.findViewById(R.id.tv_time)
            val imgIcon: ImageView = view.findViewById(R.id.img_icon)
        }

        fun setData(dataList: MutableList<MessageOutput>) {
            mDataList.clear()
            mDataList.addAll(dataList)
            notifyDataSetChanged()
        }

        fun addData(data: MessageOutput) {
            mDataList.add(data)
            notifyItemInserted(mDataList.lastIndex)
        }

    }

}
