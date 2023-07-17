package com.pnj.gudang.chat

import com.google.firebase.database.Exclude

data class Chat(
    val email: String? = null,
    val message: String? = null,
    val time: String?= null){
    @Exclude
    fun getMap(): Map<String,Any?>{
        return mapOf(
            "email" to email,
            "message" to message,
            "time" to time,
        )
    }
}
