package com.example.linemsgcatch.ui.normal

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.linemsgcatch.R
import com.example.linemsgcatch.data.MessageOutput
import com.example.linemsgcatch.tool.dateMinus
import com.example.linemsgcatch.tool.todayDate
import com.example.linemsgcatch.tool.nowTimeFormatter
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_filter_message.*
import kotlinx.android.synthetic.main.content_filter_date_msg_list_rv.view.*
import kotlinx.android.synthetic.main.content_filter_msg_list_rv.view.*


class FilterMessageActivity : AppCompatActivity() {

    val dataList = mutableListOf<FilterMsgItem>()
    private val mRVAdapter = GroupAdapter<GroupieViewHolder>()
    private var userName = ""
    private var mIsLoadMore = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filter_message)

        userName = intent.getStringExtra(MainActivity.USER_NAME)
        tv_title_name.text = userName

        initRv()
        searchMessage()
    }

    private fun searchMessage(date: String? = todayDate) {
//        val path = FirebaseDatabase.getInstance().reference.child("message/${date}")
//        val db = path.child("name").equalTo(userName)
//        db.orderByChild("time")
        val db = FirebaseDatabase.getInstance().reference
            .child("message/${date}")
//            .orderByChild("name")
//            .equalTo(userName)
            .orderByChild("time")

        db.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val dataCount = snapshot.children.count()
                if (dataCount > 0 && date != todayDate) mRVAdapter.add(
                    DateMsgItem(
                        date
                    )
                )
                mIsLoadMore = dataCount > 0

                if (mIsLoadMore) {
                    snapshot.children.forEach {
                        val msg = it.getValue(MessageOutput::class.java)
                        if (msg?.name == userName) {
                            Log.e(">>>", "msg = ${msg?.content}, ${nowTimeFormatter(msg?.time)}")
//                    newList.add(FilterMsgItem(msg))
                            mRVAdapter.add(
                                FilterMsgItem(
                                    msg
                                )
                            )
                        }
                    }

                }
/*

                mRVAdapter.addAll(newList.asReversed())

                if (mIsLoadMore) {
                    val linearLayoutManager = LinearLayoutManager(
                        this@FilterMessageActivity.baseContext,
                        LinearLayoutManager.VERTICAL,
                        false
                    )
//                    linearLayoutManager.reverseLayout
//                    linearLayoutManager.stackFromEnd
                    rv.layoutManager = linearLayoutManager
                }
*/

//                if (!mIsLoadMore) scrollToBottom()

//                Log.e(">>>", "mIsLoadMore = $mIsLoadMore")
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })

    }

    private var nextPage = 0
    private fun initRv() {
        /*
        rv.apply {
            this.adapter = mRVAdapter
            val linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
//            linearLayoutManager.reverseLayout = true
//            linearLayoutManager.stackFromEnd = true
            this.layoutManager = linearLayoutManager
        }
        */

        rv.adapter = mRVAdapter
        val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        linearLayoutManager.reverseLayout = true
//        linearLayoutManager.stackFromEnd = true
        rv.layoutManager = linearLayoutManager

        rv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                //滑至頂埔
                if (!rv.canScrollVertically(-1)) {
//                    Log.e(">>>", "onScrollStateChanged")
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                //滑至頂埔
                if (!rv.canScrollVertically(-1)) {
                    nextPage += 1
                    Log.e(">>>", "onScrolled, nextPage = $nextPage")
                    val previousDate = dateMinus(nextPage)
                    searchMessage(previousDate)
                }
            }
        })

    }



    private fun scrollToBottom() {
        rv.scrollToPosition(mRVAdapter.itemCount - 1)
    }

    private fun scrollToTop() {
        rv.scrollToPosition(0)
    }

    private fun listenForMessage() {
        val ref = FirebaseDatabase.getInstance().reference.child("/message").push().child("name")
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
                Log.e(">>>", "message = ${message?.content}")
                /*
                val message = snapshot.getValue(MessageOutput::class.java)
                if (message != null) {
                    mRVAdapter.add(MsgItem(message))
                    scrollToBottom()
                }
                */
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
            }

        })
    }

}

class FilterMsgItem(private val msg: MessageOutput?) : Item<GroupieViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.content_filter_msg_list_rv
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.apply {
            tv_msg.text = msg?.content
            tv_time.text = nowTimeFormatter(msg?.time)
        }
    }
}


class DateMsgItem(private val date: String?) : Item<GroupieViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.content_filter_date_msg_list_rv
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.apply {
            tv_date.text = date
        }
    }

}


class RVAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val mDataList: ArrayList<MessageOutput> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return  MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.content_filter_msg_list_rv, parent, false))
    }

    override fun getItemCount(): Int = mDataList.size

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = mDataList[position]
        when (holder) {
            is MyViewHolder -> {
                holder.itemView.apply {
                    tv_msg.text = data.content
                    tv_time.text = nowTimeFormatter(data.time)
//                        Glide.with(imgIcon.context).load(data.pic).error(android.R.drawable.stat_notify_error).into(imgIcon)
                }
            }
        }
    }
    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)

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





