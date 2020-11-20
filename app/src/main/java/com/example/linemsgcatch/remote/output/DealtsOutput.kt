package com.example.linemsgcatch.remote.output

import com.example.linemsgcatch.remote.output.common.InfoObject

class DealtsOutput {

    val apiVersion: String? = null
    val data: DataObject? = null

    data class DataObject(
        val info: InfoObject? = null,
        val dealts: DealtsObject? = null
    )

    data class DealtsObject(
        val at: String? = null, //此筆交易的成交時間
        val price: Number? = null, //此筆交易的成交價格
        val unit: Number? = null, //此筆交易的成交張數 (上市、上櫃股票)
        val volume: Number? = null, //此筆交易的成交股數 (興櫃股票)
        val serial: Number? = null //此筆交易的序號
    )

}