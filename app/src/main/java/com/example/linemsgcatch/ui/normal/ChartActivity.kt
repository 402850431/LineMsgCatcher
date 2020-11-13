package com.example.linemsgcatch.ui.normal

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.linemsgcatch.R
import com.example.linemsgcatch.remote.BaseWebApi
import com.example.linemsgcatch.remote.api.StockApi
import com.example.linemsgcatch.remote.output.ErrorOutput
import kotlinx.android.synthetic.main.activity_chart.*

class ChartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chart)

        getChartApi()
    }

    private fun getChartApi() {
        StockApi().getChart(4735, object : BaseWebApi.ResultListener {
            override fun onResult(response: String?) {
                textView4.text = response
            }

            override fun onError(errorOutput: ErrorOutput?) {
                textView4.text = errorOutput?.error?.message
            }

        })
    }
}