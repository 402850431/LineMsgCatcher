package com.example.linemsgcatch.remote.output

import com.example.linemsgcatch.remote.output.common.InfoObject

class ChartOutput {

    val apiVersion: String? = null
    val data: DataObject? = null

    data class DataObject (
        val info: InfoObject? = null,
        val chart: Map<String, ChartObject>? = null
    )

    data class ChartObject (
        val open: Double? = null, //此分鐘的開盤價
        val high: Double? = null, //此分鐘的最高價
        val low: Double? = null, //此分鐘的最低價
        val close: Double? = null, //此分鐘的收盤價
        val unit: Double? = null, //此分鐘的交易張數
        val volume: Double? = null //此分鐘的交易量
    )
}