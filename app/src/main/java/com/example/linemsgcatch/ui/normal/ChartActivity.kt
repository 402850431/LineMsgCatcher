package com.example.linemsgcatch.ui.normal

import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.linemsgcatch.R
import com.example.linemsgcatch.remote.BasicWebApi
import com.example.linemsgcatch.remote.api.StockApi
import com.example.linemsgcatch.remote.output.ChartOutput
import com.example.linemsgcatch.remote.output.ErrorOutput
import com.example.linemsgcatch.remote.output.MetaOutput
import com.example.linemsgcatch.tool.apiTimeToMyTimeFormat
import com.example.linemsgcatch.tool.apiTimeToTimeMillis
import com.example.linemsgcatch.tool.showDialog
import com.example.linemsgcatch.ui.kline.KData
import com.example.linemsgcatch.ui.kline.KLineView
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_chart.*
import java.util.*

class ChartActivity : AppCompatActivity() {

    private var mHandler: Handler? = null
    private var dataListAddRunnable: Runnable? = null
    private var singleDataAddRunnable: Runnable? = null
    private val stockApi = StockApi()
    private var symbolId = 6191

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chart)

        initData()
        getMetaApi()
        getChartApi()
    }

    private fun getMetaApi() {
        stockApi.getMeta(symbolId, object : BasicWebApi.ResultListener {
            override fun onResult(response: String?) {
                val metaOutput = Gson().fromJson(response, MetaOutput::class.java)
                tv_stockName.text = "${metaOutput.data?.meta?.nameZhTw}($symbolId)"
            }

            override fun onError(errorOutput: ErrorOutput?) {
                showDialog(this@ChartActivity, null, errorOutput?.error?.message, null)
            }

        })
    }

    private fun initData() {
        //初始化控件加载数据，仅限于首次初始化赋值，不可用于更新数据
        klv_main.initKDataList(getKDataList(10.0))

        //设置十字线移动模式，默认为0：固定指向收盘价
        klv_main.setCrossHairMoveMode(KLineView.CROSS_HAIR_MOVE_OPEN)

        mHandler = Handler()
        dataListAddRunnable = Runnable {
            //分页加载时添加多条数据
            klv_main.addPreDataList(getKDataList(10.0), true)
            //klv_main.addPreDataList(null, true);
        }

        singleDataAddRunnable = Runnable {
            //实时刷新时添加单条数据
            klv_main.addSingleData(getKDataList(0.1)?.get(0))
        }
//        mHandler.postDelayed(singleDataAddRunnable, 2000);

        //当控件显示数据属于总数据量的前三分之一时，会自动调用该接口，用于预加载数据，保证控件操作过程中的流畅性，
        //虽然做了预加载，当总数据量较小时，也会出现用户滑到左边界了，但数据还未获取到，依然会有停顿。
        //所以数据量越大，越不会出现停顿，也就越流畅
        klv_main.setOnRequestDataListListener { //延时3秒执行，模拟网络请求耗时
            mHandler!!.postDelayed(dataListAddRunnable, 3000)
        }
    }

    private fun initData2(chart: Map<String, ChartOutput.ChartObject>?) {
        //初始化控件加载数据，仅限于首次初始化赋值，不可用于更新数据
        klv_main2.initKDataList(getKDataListTest(10.0, chart))

        //设置十字线移动模式，默认为0：固定指向收盘价
        klv_main2.setCrossHairMoveMode(KLineView.CROSS_HAIR_MOVE_OPEN)

        mHandler = Handler()
        dataListAddRunnable = Runnable {
            //分页加载时添加多条数据
            klv_main2.addPreDataList(getKDataListTest(10.0, chart), true)
            //klv_main.addPreDataList(null, true);
        }

        singleDataAddRunnable = Runnable {
            //实时刷新时添加单条数据
            klv_main2.addSingleData(getKDataListTest(0.1, chart)?.get(0))
        }
//        mHandler.postDelayed(singleDataAddRunnable, 2000);

        //当控件显示数据属于总数据量的前三分之一时，会自动调用该接口，用于预加载数据，保证控件操作过程中的流畅性，
        //虽然做了预加载，当总数据量较小时，也会出现用户滑到左边界了，但数据还未获取到，依然会有停顿。
        //所以数据量越大，越不会出现停顿，也就越流畅
        klv_main2.setOnRequestDataListListener { //延时3秒执行，模拟网络请求耗时
            mHandler!!.postDelayed(dataListAddRunnable, 3000)
        }
    }


    private fun getChartApi() {
        stockApi.getChart(symbolId, object : BasicWebApi.ResultListener {
            override fun onResult(response: String?) {
                val chatOutput = Gson().fromJson(response, ChartOutput::class.java)
                initData2(chatOutput?.data?.chart)

//                tv_symbolId.text = chatOutput.data?.chart?.get("2020-11-16T01:01:00.000Z")?.open.toString()
            }

            override fun onError(errorOutput: ErrorOutput?) {
//                tv_stockName.text = errorOutput?.error?.message
            }

        })
    }

    private fun getKDataListTest(num: Double, chartMap: Map<String, ChartOutput.ChartObject>?): List<KData>? {
        val dataList = mutableListOf<KData>()

        Log.e(">>>", "getKDataListTest")
        chartMap?.forEach {
//            val keyTime = apiTimeToMyTimeFormat(it.key)
            val keyTime = apiTimeToTimeMillis(it.key)
            val value = it.value
            Log.e(">>>", "time = ${it.key}, high = ${value.high?:0.0}")
            dataList.add(KData(keyTime, value.open?:0.0,
                value.high?:0.0, value.low?:0.0, value.close?:0.0, value.volume?:0.0))
        }

        return dataList
    }

    //模拟K线数据
    private fun getKDataList(num: Double): List<KData>? {
        var start = System.currentTimeMillis()
        val random = Random()
        val dataList = mutableListOf<KData>()
        var openPrice = 100.0
        var closePrice: Double
        var maxPrice: Double
        var minPrice: Double
        var volume: Double

        var x = 0
        while (x < num * 10) {
            for (i in 0..11) {
                start += 60 * 1000 * 5.toLong()
                closePrice = openPrice + getAddRandomDouble()
                maxPrice = closePrice + getAddRandomDouble()
                minPrice = openPrice - getSubRandomDouble()
                volume =
                    random.nextInt(100) * 1000 + random.nextInt(10) * 10 + random.nextInt(10) + random.nextDouble()
                dataList.add(
                    KData(
                        start, //time
                        openPrice,
                        maxPrice,
                        minPrice,
                        closePrice,
                        volume //or unit???
                    )
                )
                openPrice = closePrice
            }
            for (i in 0..7) {
                start += 60 * 1000 * 5.toLong()
                closePrice = openPrice - getSubRandomDouble()
                maxPrice = openPrice + getAddRandomDouble()
                minPrice = closePrice - getSubRandomDouble()
                volume =
                    random.nextInt(100) * 1000 + random.nextInt(10) * 10 + random.nextInt(10) + random.nextDouble()
                dataList.add(
                    KData(
                        start,
                        openPrice,
                        closePrice,
                        maxPrice,
                        minPrice,
                        volume
                    )
                )
                openPrice = closePrice
            }
            x++
        }
        val end = System.currentTimeMillis()

        return dataList
    }

    private fun getAddRandomDouble(): Double {
        val random = Random()
        return random.nextInt(5) * 5 + random.nextDouble()
    }

    private fun getSubRandomDouble(): Double {
        val random = Random()
        return random.nextInt(5) * 5 - random.nextDouble()
    }

    override fun onDestroy() {
        super.onDestroy()
        //退出页面时停止子线程并置空，便于回收，避免内存泄露
        klv_main.cancelQuotaThread()
    }

}