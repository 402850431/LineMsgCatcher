package com.example.linemsgcatch.data.manager


object ApiDataManager {


    const val baseUrl = "https://api.fugle.tw/realtime/v0"

    const val apiToken = "f641da89c49a52920d723cf2037bcbab"
    var symbolId = 4735 //股票id


    //提供盤中個股/指數 線圖時所需的各項即時資訊
    val chartUrl = "$baseUrl/intraday/chart?symbolId=$symbolId&apiToken=$apiToken"

    //提供盤中個股/指數逐筆交易金額、狀態、最佳五檔及統計資訊
    val quoteUrl = "$baseUrl/intraday/quote?symbolId=$symbolId&apiToken=$apiToken"

    //提供盤中個股/指數當日基本資訊
    val metaUrl = "$baseUrl/intraday/meta?symbolId=$symbolId&apiToken=$apiToken"

    //取得個股當日所有成交資訊（ex: 個股價量、大盤總量）
    val dealtUrl = "$baseUrl/intraday/dealts?symbolId=$symbolId&apiToken=$apiToken&limit=50"




}