package com.example.linemsgcatch.ui.common

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.text.Spanned
import android.view.Gravity
import android.view.View
import com.example.linemsgcatch.R
import kotlinx.android.synthetic.main.dialog_confirm.*

/**
 * 常用提示對話框
 * 預設按鈕: 取消 / 確定
 *
 * memo:
 * this.setCanceledOnTouchOutside(false) //disable 點擊外部關閉 dialog
 * this.setCancelable(false) //disable 按實體鍵 BACK 關閉 dialog
 */
class CustomAlertDialog(context: Context) : AlertDialog(context) {

    private var mTitle: String? = "提示"
    private var mMessage: String? = null
    private var mSpannedMessage: Spanned? = null
    private var mPositiveText: String? = "確定"
    private var mNegativeText: String? = "取消"
    private var mPositiveClickListener: View.OnClickListener = View.OnClickListener { dismiss() }
    private var mNegativeClickListener: View.OnClickListener = View.OnClickListener { dismiss() }
    private var mGravity = Gravity.CENTER

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_confirm)
        this.window?.setBackgroundDrawableResource(android.R.color.transparent)
        initView()
    }

    private fun initView() {
        if (mTitle == null) {
            tv_title.visibility = View.GONE
        } else {
            tv_title.text = mTitle
        }

        when {
            mSpannedMessage != null -> tv_message.text = mSpannedMessage
            mMessage == null -> {
                sv_block_content.visibility = View.GONE
            }
            else -> tv_message.text = mMessage
        }

        tv_message.gravity = mGravity

        if (mPositiveText == null) {
            btn_positive.visibility = View.GONE
            line3.visibility = View.GONE //隱藏 button 之間的分隔線
        } else
            btn_positive.text = mPositiveText

        if (mNegativeText == null) {
            btn_negative.visibility = View.GONE
            line3.visibility = View.GONE //隱藏 button 之間的分隔線
        } else
            btn_negative.text = mNegativeText

        if (mPositiveText == null && mNegativeText == null)
            line2.visibility = View.GONE

        btn_positive.setOnClickListener(mPositiveClickListener)
        btn_negative.setOnClickListener(mNegativeClickListener)
    }


    ////
    //以下設定要在 dialog.show() 之前才有效果
    ////
    fun setTitle(title: String?) {
        mTitle = title
    }

    fun setMessage(message: String?) {
        mMessage = message
    }

    //set .html 語法文字
    fun setMessage(spanned: Spanned) {
        mSpannedMessage = spanned
    }

    fun setGravity(gravity: Int) {
        mGravity = gravity
    }

    /**
     * @param positiveText: Positive 按鈕文字，若給 null 則隱藏按鈕
     */
    fun setPositiveButtonText(positiveText: String?) {
        mPositiveText = positiveText
    }

    /**
     * @param negativeText: Negative 按鈕文字，若給 null 則隱藏按鈕
     */
    fun setNegativeButtonText(negativeText: String?) {
        mNegativeText = negativeText
    }

    fun setPositiveClickListener(positiveClickListener: View.OnClickListener) {
        mPositiveClickListener = positiveClickListener
    }

    fun setNegativeClickListener(negativeClickListener: View.OnClickListener) {
        mNegativeClickListener = negativeClickListener
    }
}