package com.example.linemsgcatch.tool

import android.content.Context
import android.view.View
import com.example.linemsgcatch.ui.common.CustomAlertDialog

private var mErrorDialog: CustomAlertDialog? = null

fun showDialog(context: Context, title: String? = null, errorMessage: String?= null, positiveBtnText: String? = "確定", negativeBtnText: String? = "取消", positiveClickListener: View.OnClickListener?) {
    try {
        //防止跳出多個 error dialog
        if (mErrorDialog?.isShowing == true)
            mErrorDialog?.dismiss()

        mErrorDialog = CustomAlertDialog(context)
        mErrorDialog?.setTitle(title)
        mErrorDialog?.setMessage(errorMessage)
        mErrorDialog?.setPositiveButtonText(positiveBtnText)
        mErrorDialog?.setNegativeButtonText(negativeBtnText)

        if (positiveClickListener != null)
            mErrorDialog?.setPositiveClickListener(View.OnClickListener {
                positiveClickListener.onClick(null)
                mErrorDialog?.dismiss()
            })
        mErrorDialog?.setCanceledOnTouchOutside(false)
        mErrorDialog?.setCancelable(false)
        mErrorDialog?.show()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun showDialog(context: Context, positiveClickListener: View.OnClickListener?) {
    showDialog(context, null, "要離開app嗎?", "確定", "取消", positiveClickListener)
}
