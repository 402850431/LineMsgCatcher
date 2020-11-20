package com.example.linemsgcatch.remote.output

import com.example.linemsgcatch.remote.output.common.InfoObject

class QuoteOutput {

    val apiVersion: String? = null
    val data: DataObject? = null

    data class DataObject(
        val info: InfoObject? = null,
        val quote: QuoteObject? = null
    )

    data class QuoteObject(
        val dateTimeMap: Map<String, ChartValue>? = null // < dateTime, items >
    )

    data class ChartValue(
        val isCurbing: Boolean? = null, //最近一次更新是否為瞬間價格穩定措施
        val isCurbingRise: Boolean? = null, //最近一次更新是否為暫緩撮合且瞬間趨漲
        val isCurbingFall: Boolean? = null, //最近一次更新是否為暫緩撮合且瞬間趨跌
        val isTrial: Boolean? = null, //最近一次更新是否為試算
        val isOpenDelayed: Boolean? = null, //當日是否曾發生延後開盤
        val isCloseDelayed: Boolean? = null, //當日是否曾發生延後收盤
        val isHalting: Boolean? = null, //最近一次更新是否為暫停交易
        val isClosed: Boolean? = null, //當日是否為已收盤

        val total: TotalObject? = null,
        val trial: TrialObject? = null,
        val trade: TradeObject? = null,
        val order: OrderObject? = null,
        val priceHigh: PriceObject? = null, //當日之最高價 第一次到達當日最高價之時間
        val priceLow: PriceObject? = null, //當日之最低價 第一次到達當日最低價之時間
        val priceOpen: PriceObject? = null //當日之開盤價，開盤定義：當天第一筆成交時才開盤 當日第一筆成交時間
    )

    data class TotalObject (
        val at: String? = null, //最新一筆成交時間
        val order: Number? = null, //總成交委託, 負數表示無 order
        val price: Number? = null, //總成交價, 負數表示無 price
        val unit: Number? = null, //總成交張數
        val volume: Number? = null //總成交量
    )

    data class TrialObject (
        val at: String? = null, //最新一筆試撮時間
        val price: Number? = null, //最新一筆試撮價格
        val unit: Number? = null, //最新一筆試撮張數
        val volume: Number? = null //最新一筆試撮成交量
    )

    data class TradeObject (
        val at: String? = null, //最新一筆成交時間
        val price: Number? = null, //最新一筆成交價格
        val unit: Number? = null, //最新一筆成交張數
        val volume: Number? = null, //最新一筆成交之成交量
        val serial: Number? = null //	最新一筆成交之序號
    )

    data class OrderObject (
        val at: String? = null, //最新一筆最佳五檔更新時間
        val bestBids: BestPriceObject? = null,
        val bestAsks: Number? = null
    )
    data class BestPriceObject (
        val price: Number? = null, //價格
        val unit: Number? = null, //張數
        val volume: Number? = null //量
    )

    data class PriceObject (
        val at: String? = null, //時間
        val price: Number? = null //價格
    )


}