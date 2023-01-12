package com.example.myapplication.data.coroutines

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel

class ReusableScope(val context: CoroutineDispatcher) {

    var scope = CoroutineScope(context)

    fun cancel() {
        scope.cancel()
        scope = CoroutineScope(context)
    }

}
