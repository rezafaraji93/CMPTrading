package dev.reza.cmptrading

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform