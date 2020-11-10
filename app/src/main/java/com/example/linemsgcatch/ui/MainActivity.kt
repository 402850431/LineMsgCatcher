package com.example.linemsgcatch.ui

import android.app.AlertDialog
import android.app.SearchManager
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
import android.view.Menu
import android.widget.SearchView
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationManagerCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.linemsgcatch.R
import com.example.linemsgcatch.data.MessageOutput
import com.example.linemsgcatch.data.UserOutput
import com.example.linemsgcatch.service.MainService
import com.example.linemsgcatch.tool.GetNotificationEvent
import com.example.linemsgcatch.tool.nowDateFormatter
import com.example.linemsgcatch.tool.nowTimeFormatter
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_msg_list_rv.view.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : BaseEventBusActivity() {

    companion object {
        const val TAG = "MainActivity"
        const val USER_NAME = ""
    }

//    private val mRVAdapter = RVAdapter()

    private val mRVAdapter = GroupAdapter<GroupieViewHolder>()
    private var dataList: MutableList<MessageOutput> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getPermission()
        setUpToolbar()
        initRv()
        listenForMessage()
        initCbListener()
        startService(Intent(this, MainService::class.java))
//        handleIntent(intent)
    }

//    override fun onNewIntent(intent: Intent) {
//        handleIntent(intent)
//    }

    private fun handleIntent(intent: Intent) {
        if (Intent.ACTION_SEARCH == intent.action) {
            val query = intent.getStringExtra(SearchManager.QUERY)
            //use the query to search your data somehow
            Log.e(">>>", "query = $query")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.options_menu, menu)

        // Associate searchable configuration with the SearchView
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = menu?.findItem(R.id.search)?.actionView as SearchView
        searchView.apply {
            setSearchableInfo(searchManager.getSearchableInfo(componentName))
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                Log.e(">>>", "query = ${query.toString()}")
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }

        })
/*
        if (Intent.ACTION_SEARCH == intent.action) {
            val query = intent.getStringExtra(SearchManager.QUERY)
            //use the query to search your data somehow
//            Log.e(">>>", "query = $query")
            Log.e(">>>", "query = ${searchView.query.toString()}")
        }
*/

        return true
    }

    private fun setUpToolbar() {
    }

    private fun initCbListener() {
        cb_important.setOnCheckedChangeListener { buttonView, isChecked ->
            /*
            if (isChecked) {
                val filterDataList =
                    dataList.filter { it.name?.contains("(重要)") == true } as MutableList<MessageOutput>
                mRVAdapter.setData(filterDataList)
            } else {
                mRVAdapter.setData(dataList)
            }
            */
        }
        test_img.setOnClickListener {
            Log.e(">>>", "test_img.isSelected = ${test_img.isSelected}")
        }

    }

    private fun initRv() {
        rv.apply {
            this.adapter = mRVAdapter
            val linearLayoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            linearLayoutManager.reverseLayout
            this.layoutManager = linearLayoutManager
          }

        mRVAdapter.setOnItemClickListener { item, view ->
            val intent = Intent(this, FilterMessageActivity::class.java)
            val item = item as MsgItem
            intent.putExtra(USER_NAME, item.msgItem.name)
            startActivity(intent)
        }
    }

    private fun listenForMessage() {
        val date = nowDateFormatter(System.currentTimeMillis())
        val ref = FirebaseDatabase.getInstance().getReference("/message/$date")
        ref.addChildEventListener(object : ChildEventListener {

            /*
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach {
                    val message = it.getValue(MessageOutput::class.java)
                    if (message != null) {
                        mRVAdapter.add(MsgItem(message))
                    }
                }
            }
*/
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val message = snapshot.getValue(MessageOutput::class.java)
                if (message != null) {
                    mRVAdapter.add(MsgItem(message))
                    scrollToBottom()
                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
            }

        })
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
    private fun storeToFirebase(name: String?, pic: Icon?, content: String?, nowTime: Long?) {
        Log.e(TAG, "storeToFirebase")

        storeMessage(name, content, nowTime)
        if (name == null || pic == null) return
        if (!isUserExist(name)) {//search if exist in db
            val picRef = FirebaseStorage.getInstance().getReference("/images/$name")
            //TODO Cheryl : better way to get uri ?
            val drawable = pic.loadDrawable(this)
            val bitmap = drawableToBitmap(drawable) ?: return
            val picUri = getImageUri(this, name, bitmap) ?: return

            picRef.putFile(picUri)
                .addOnSuccessListener {
                    picRef.downloadUrl.addOnSuccessListener { downloadUri ->
                        Log.d(TAG, "upload user img succeed. File Location: $downloadUri")
                        val profileImgUrl = downloadUri.toString()
                        storeUser(name, profileImgUrl)
                    }
                }
                .addOnFailureListener {
                    Log.e(TAG, "upload user img failed: ${it.message}")
                }
        }

    }

    private fun storeUser(name: String, profileImgUrl: String) {
        val userRef = FirebaseDatabase.getInstance().getReference("/users/$name")
        val user = UserOutput(name, profileImgUrl, null)
        userRef.setValue(user)
            .addOnSuccessListener {
                Log.d(TAG, "upload user succeed")
            }
            .addOnFailureListener {
                Log.e(TAG, "upload user failed")
            }
    }

    private fun storeMessage(name: String?, content: String?, nowTime: Long?) {
        val date = nowDateFormatter(System.currentTimeMillis())
        val msgRef = FirebaseDatabase.getInstance().getReference("/message/$date").push() //format = 2020/11/09
        val msg = MessageOutput(name, content, nowTime)
        msgRef.setValue(msg)
            .addOnSuccessListener {
                Log.d(TAG, "upload message succeed")
            }
            .addOnFailureListener {
                Log.e(TAG, "upload message failed")
            }
    }

    private fun scrollToBottom() {
        rv.scrollToPosition(mRVAdapter.itemCount - 1)
    }

    private fun isUserExist(name: String): Boolean {
        var isExist = false
        val userRef = FirebaseDatabase.getInstance().getReference("/users").child(name)
        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                isExist = snapshot.exists()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "search user if exist error: ${error.message}")
            }

        })
        return isExist
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

        event.apply {
            storeToFirebase(name, pic, content, System.currentTimeMillis())
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
                tv_time.text = nowTimeFormatter(msgItem.time)
                val imgRef = FirebaseStorage.getInstance().getReference("/images/${msgItem.name}")
                imgRef.downloadUrl.addOnSuccessListener {
                    Glide.with(this).load(it.toString()).error(android.R.drawable.stat_notify_error)
                        .into(img_icon)
                }
            }

        }

    }

}
