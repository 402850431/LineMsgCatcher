package com.example.linemsgcatch.remote.api

import com.example.linemsgcatch.data.manager.ApiDataManager
import com.example.linemsgcatch.remote.BaseWebApi

class StockApi : BaseWebApi()  {

    /**
     * 提供盤中個股/指數 線圖時所需的各項即時資訊
     * @param symbolId: 個股、指數識別代碼
     */
    fun getChart(symbolId: Int, resultListener: ResultListener) {
        val url = "${ApiDataManager.baseUrl}/intraday/chart?symbolId=${symbolId}&apiToken=${ApiDataManager.apiToken}"
        val jsonObjectRequest = createJsonObjectRequest(url, null, resultListener)
        requestQueue.add(jsonObjectRequest)
    }

    /**
     * 提供盤中個股/指數逐筆交易金額、狀態、最佳五檔及統計資訊
     * @param symbolId: 個股、指數識別代碼
     */
    fun getQuote(symbolId: Int, resultListener: ResultListener) {
        val url = "${ApiDataManager.baseUrl}/intraday/quote?symbolId=${ApiDataManager.symbolId}&apiToken=${ApiDataManager.apiToken}"
        val jsonObjectRequest = createJsonObjectRequest(url, null, resultListener)
        requestQueue.add(jsonObjectRequest)
    }

    /**
     * 提供盤中個股/指數當日基本資訊
     * @param symbolId: 個股、指數識別代碼
     */
    fun getMeta(symbolId: Int, resultListener: ResultListener) {
        val url = "${ApiDataManager.baseUrl}/intraday/meta?symbolId=${ApiDataManager.symbolId}&apiToken=${ApiDataManager.apiToken}"
        val jsonObjectRequest = createJsonObjectRequest(url, null, resultListener)
        requestQueue.add(jsonObjectRequest)
    }

    /**
     * 取得個股當日所有成交資訊（ex: 個股價量、大盤總量）
     * @param symbolId: 個股、指數識別代碼
     * @param limit: 限制最多回傳的資料筆數。預設值：50
     * @param offset: 指定從第幾筆後開始回傳。預設值：0
//     * @param oddLot: 設置 true 回傳零股行情。預設值：false
     */
    fun getDealt(symbolId: Int, limit:Int?, offset:Int?,/* oddLot:Boolean? = false, */resultListener: ResultListener) {
        var url = "${ApiDataManager.baseUrl}/intraday/dealts?symbolId=${ApiDataManager.symbolId}&apiToken=${ApiDataManager.apiToken}&limit=50"

        if (limit != null) url += "&limit=$limit"
        if (offset != null) url += "&offset=$offset"
//        if (oddLot != null) url += "&oddLot=$oddLot"

        val jsonObjectRequest = createJsonObjectRequest(url, null, resultListener)
        requestQueue.add(jsonObjectRequest)
    }


}