package com.example.linemsgcatch.remote.output

open class ErrorOutput {

    var apiVersion: Boolean? = null
    var error: ErrorData? = null

    class ErrorData {
        var code: Int? = null
        var message: String? = null
    }
}