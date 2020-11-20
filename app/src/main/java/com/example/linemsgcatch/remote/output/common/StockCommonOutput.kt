package com.example.linemsgcatch.remote.output.common

data class InfoObject (
    val lastUpdatedAt: String? = null, //本筆資料最後更新時間
    val date: String? = null, //本筆資料所屬日期
    val mode: String? = null, //交易所-交易市場
    val symbolId: String? = null, //股票代號
    val countryCode: String? = null, //股票所屬國家ISO2代碼
    val timeZone: String? = null //股票所屬時區
)