package com.example.linemsgcatch.data

import android.graphics.drawable.Icon


data class MessageOutput(val name: String?, val content: String?, val time: Long?) {
    constructor() : this("", "", 0)
}
//data class MessageOutput(val name: String?, val content: String?, val time: String)
