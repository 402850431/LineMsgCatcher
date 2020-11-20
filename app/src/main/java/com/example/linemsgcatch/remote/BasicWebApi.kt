package com.example.linemsgcatch.remote

import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.*
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.linemsgcatch.R
import com.example.linemsgcatch.remote.output.ErrorOutput
import com.example.linemsgcatch.ui.base.MyApplication
import com.google.gson.Gson
import java.nio.charset.StandardCharsets

open class BasicWebApi {

    companion object {
        const val TAG = "BasicWebApi"
    }

    interface ResultListener {
        fun onResult(response: String?)
        fun onError(errorOutput: ErrorOutput?)
    }

    fun createRequest(url: String, resultListener: ResultListener) {
        val queue = Volley.newRequestQueue(MyApplication.getAppContext())
//        val url = "https://wwwjsonplaceholder.typicode.com/posts"

        val stringRequest = StringRequest(
            Request.Method.GET, url,
            Response.Listener<String> { response ->
                Log.d(TAG, "url: $url, Response: $response")
                resultListener.onResult(response)
            },
            Response.ErrorListener { error ->
                Log.e(TAG, "url: $url, error: $error")
                resultListener.onError(createErrorOutput(error))
            })

        queue.add(stringRequest)
    }

    private fun createErrorOutput(error: VolleyError): ErrorOutput? {
        var errorOutput: ErrorOutput
        try {
            val response = String(error.networkResponse.data, StandardCharsets.UTF_8)
            Log.e(TAG, response)
            errorOutput = Gson().fromJson(response, ErrorOutput::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            errorOutput = ErrorOutput()
            if (error is TimeoutError) {
                errorOutput.error!!.code = -1
                errorOutput.error!!.message =
                    MyApplication.getAppContext().getString(R.string.network_timeout)
            } else if (error is ServerError) {
                errorOutput.error!!.code = -1
                errorOutput.error!!.message =
                    MyApplication.getAppContext().getString(R.string.server_error)

                //20190814 記錄問題: 500、501、403、404、401 正常情況 http 回傳的這幾個 statusCode，server 會回傳 json errorOutput，不該解析失敗執行到這
                //error.networkResponse 有機會為 null 無法獲取 statusCode
                if (error.networkResponse != null) {
                    val statusCode = error.networkResponse.statusCode
                    errorOutput.error!!.message =
                        errorOutput.error!!.message + "【" + statusCode + "】"
                }
            } else {
                errorOutput.error!!.code = -1
                errorOutput.error!!.message =
                    MyApplication.getAppContext().getString(R.string.network_io_fail)
            }
        }
        return errorOutput
    }

}