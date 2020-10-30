package com.example.linemsgcatch.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.linemsgcatch.R
import com.example.linemsgcatch.data.MessageOutput
import com.example.linemsgcatch.tool.nowTimeFormatter
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_filter_message.*
import kotlinx.android.synthetic.main.activity_filter_message.rv
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_filter_msg_list_rv.view.*

class FilterMessageActivity : AppCompatActivity() {

    private val mRVAdapter = GroupAdapter<GroupieViewHolder>()
    var userName = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filter_message)

        userName = intent.getStringExtra(MainActivity.USER_NAME)
        supportActionBar?.title = userName

        initRv()
        searchInDb()
    }

    private fun searchInDb() {
        val db = FirebaseDatabase.getInstance().reference.child("message")
        db.orderByChild("name").equalTo(userName)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.e(">>>", snapshot.childrenCount.toString())
                    Log.e(">>>", snapshot.toString())

//                    val value = snapshot.value as MessageOutput
//                    Log.e(">>>", value.content.toString())

                    val msg = snapshot.getValue(MessageOutput::class.java)
//                    Log.e(">>>", msg?.content.toString())
                    if (msg?.content != null) {
                        mRVAdapter.add(FilterMsgItem(msg))
                    }
                }
                override fun onCancelled(error: DatabaseError) {

                }


            })
    }

    private fun initRv() {
        rv.apply {
            this.adapter = mRVAdapter
            val linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            linearLayoutManager.reverseLayout
            this.layoutManager = linearLayoutManager
        }

    }



    private fun scrollToBottom() {
        rv.scrollToPosition(mRVAdapter.itemCount - 1)
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

class FilterMsgItem(private val msg: MessageOutput) : Item<GroupieViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.content_filter_msg_list_rv
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.apply {
            tv_msg.text = msg.content
            tv_time.text = nowTimeFormatter(msg.time)
        }
    }

}







