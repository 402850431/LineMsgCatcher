package com.example.linemsgcatch.remote.output

import com.example.linemsgcatch.remote.output.common.InfoObject

class MetaOutput {

    val apiVersion: String? = null
    val data: DataObject? = null

    data class DataObject(
        val info: InfoObject? = null,
        val meta: MetaObject? = null
    )

    data class MetaObject(
        val isIndex: Boolean? = null, //是否為指數
        val nameZhTw: String? = null, //股票中文簡稱
        val industryZhTw: String? = null, //產業別
        val priceReference: Number? = null, //今日參考價
        val priceHighLimit: Number? = null, //漲停價
        val priceLowLimit: Number? = null, //跌停價
        val canDayBuySell: Boolean? = null, //是否可先買後賣現股當沖
        val canDaySellBuy: Boolean? = null, //是否可先賣後買現股當沖
        val canShortMargin: Boolean? = null, //是否豁免平盤下融券賣出
        val canShortLend: Boolean? = null, //是否豁免平盤下借券賣出
        val volumePerUnit: Int? = null, //交易單位：股/張
        val currency: String? = null, //交易幣別代號
        val isTerminated: Boolean? = null, //今日是否已終止上市
        val isSuspended: Boolean? = null, //今日是否暫停買賣
        val isWarrant: Boolean? = null, //是否為權證
        val typeZhTw: String? = null //股票類別
    )

}