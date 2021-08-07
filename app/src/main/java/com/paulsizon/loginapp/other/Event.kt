package com.paulsizon.loginapp.other


//to make sure any event doesn't happen twice
open class Event<out T>( private val content: T) {
    var hasBeenHandled = false
    private set

    fun getContentIfnotHandled() = if (hasBeenHandled){
        null
    } else {
        hasBeenHandled = true
        content
    }

    fun peekContent() = content



}