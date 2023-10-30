package com.asepssp.storyapps.pref

data class Model(
    val name: String,
    val email: String,
    val token: String,
    val isLogin: Boolean = false
)