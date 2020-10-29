package com.example.linemsgcatch.ui

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.Icon
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationManagerCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.linemsgcatch.R
import com.example.linemsgcatch.data.MessageOutput
import com.example.linemsgcatch.data.UserOutput
import com.example.linemsgcatch.data.db.MemberDatabaseHelper
import com.example.linemsgcatch.service.MainService
import com.example.linemsgcatch.tool.GetNotificationEvent
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_msg_list_rv.view.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : BaseEventBusActivity() {

    companion object {
        const val TAG = "MainActivity"
    }

    private val mRVAdapter = RVAdapter()

    //    private val mRVAdapter = GroupAdapter<GroupieViewHolder>()
//    private var mSql: MemberDatabaseHelper? = null
    private var dataList: MutableList<MessageOutput> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getPermission()
//        initSql()
        initRv()
//        initCbListener()
        startService(Intent(this, MainService::class.java))
    }

    private fun initCbListener() {
        cb_important.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                val filterDataList =
                    dataList.filter { it.name?.contains("(重要)") == true } as MutableList<MessageOutput>
                mRVAdapter.setData(filterDataList)
            } else {
                mRVAdapter.setData(dataList)
            }
        }


    }

    private fun initSql() {
//        mSql = MemberDatabaseHelper(this)
    }

    private fun initRv() {
        rv.apply {
            this.adapter = mRVAdapter
            val linearLayoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            linearLayoutManager.reverseLayout
            this.layoutManager = linearLayoutManager
        }

//        dataList = mSql?.getData()?: mutableListOf()
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


    //    private var profileImgUrl: String? = null
    @RequiresApi(Build.VERSION_CODES.M)
    private fun storeToFirebase(name: String?, pic: Icon?, content: String?, nowTime: String?) {
        Log.e(TAG, "storeToFirebase")
        val picRef = FirebaseStorage.getInstance().getReference("/images/$name")
        picRef.child("/images/$name").downloadUrl //search if exist in db
            .addOnSuccessListener {
                Log.e(">>>", "file exist. uri = $it")
            }
            .addOnFailureListener {
//                Log.e(">>>", "file not exist : ${it.message}")

                val errorCode = (it as StorageException).errorCode
                if (errorCode == StorageException.ERROR_OBJECT_NOT_FOUND) {
                    Log.e(">>>", "ERROR_OBJECT_NOT_FOUND")
                    //TODO Cheryl : better way to get uri ?
                    if (pic == null) return@addOnFailureListener
                    val drawable = pic.loadDrawable(this)
                    val bitmap = drawableToBitmap(drawable) ?: return@addOnFailureListener
                    val picUri =
                        getImageUri(this, name ?: "", bitmap) ?: return@addOnFailureListener

                    picRef.putFile(picUri)
                        .addOnSuccessListener {
                            picRef.downloadUrl.addOnSuccessListener { downloadUri ->
                                Log.e(TAG, "File Location: $downloadUri")
                                val profileImgUrl = downloadUri.toString()
                                storeUserAndMessage(name, profileImgUrl, content, nowTime)
                            }
                        }
                        .addOnFailureListener {
                            Log.e(TAG, "upload failed: ${it.message}")
                        }
                } else {
                    Log.e(">>>", "search img file failed : ${it.message}")
                }
            }
    }

    private fun storeUserAndMessage(
        name: String?,
        picUrl: String?,
        content: String?,
        nowTime: String?
    ) {
        val msgRef =
            FirebaseDatabase.getInstance().getReference("/message/${System.currentTimeMillis()}")
        val msg = MessageOutput(name, content, nowTime)
        msgRef.setValue(msg)
            .addOnSuccessListener {
                Log.e(">>>", "upload message succeed")
            }
            .addOnFailureListener {
                Log.e(">>>", "upload message failed")
            }


        val userRef = FirebaseDatabase.getInstance().getReference("/users/$name")
        val user = UserOutput(name, picUrl, null)
        userRef.setValue(user)
            .addOnSuccessListener {
                Log.e(">>>", "upload user succeed")
            }
            .addOnFailureListener {
                Log.e(">>>", "upload user failed")
            }

    }

    private fun drawableToBitmap(drawable: Drawable): Bitmap? {
        if (drawable is BitmapDrawable) {
            if (drawable.bitmap != null) {
                return drawable.bitmap
            }
        }
        val bitmap: Bitmap? = if (drawable.intrinsicWidth <= 0 || drawable.intrinsicHeight <= 0) {
            Bitmap.createBitmap(
                1,
                1,
                Bitmap.Config.ARGB_8888
            ) // Single color bitmap will be created of 1x1 pixel
        } else {
            Bitmap.createBitmap(
                drawable.intrinsicWidth,
                drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
        }
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    private fun getImageUri(inContext: Context, title: String, inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 30, bytes)
        val path = MediaStore.Images.Media.insertImage(
            inContext.contentResolver,
            inImage,
            title,
            null
        )
        return Uri.parse(path)
    }

    //更新畫面
    @RequiresApi(Build.VERSION_CODES.M)
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: GetNotificationEvent) {
        Log.e(">>>", "GetNotificationEvent")

        val nowTime = SimpleDateFormat(
            "MM/dd hh:mm:ss",
            Locale.getDefault()
        ).format(System.currentTimeMillis())
        event.apply {
//            val splitStr = text?.split(" : ")
//            val name = if (splitStr?.size?:0 > 1) splitStr?.firstOrNull() else title
//            val content = if (splitStr?.size?:0 > 1) splitStr?.get(1) else text
//        val iconUri = testUri

            storeToFirebase(name, pic, content, nowTime)
            /*
            dataList.add(
                MessageOutput(
                    name,
                    content,
                    nowTime,
                    pic
                )
            )
            */
//            mSql?.addData(name, content, nowTime)
//            mRVAdapter.addData(dataList.last())
//            rv.scrollToPosition(dataList.size - 1)
        }
    }

//--------- rv adapter ---------

    class MsgItem(val msgItem: MessageOutput) : Item<GroupieViewHolder>() {
        override fun getLayout(): Int {
            return R.layout.content_msg_list_rv
        }

        @RequiresApi(Build.VERSION_CODES.M)
        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            viewHolder.itemView.apply {
                tv_name.text = msgItem.name
                tv_msg.text = msgItem.content
                tv_time.text = msgItem.time
//                img_icon.setImageIcon(msgItem.pic)
            }

        }

    }


    class RVAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        private val mDataList: ArrayList<MessageOutput> = ArrayList()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return MyViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.content_msg_list_rv, parent, false)
            )
        }

        override fun getItemCount(): Int = mDataList.size

        @RequiresApi(Build.VERSION_CODES.M)
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val data = mDataList[position]
            when (holder) {
                is MyViewHolder -> {
                    holder.apply {
                        tvName.text = data.name
                        tvMsg.text = data.content
                        tvTime.text = data.time
//                        imgIcon.setImageIcon(data.pic)
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
