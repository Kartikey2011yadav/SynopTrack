package com.example.synoptrack

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform