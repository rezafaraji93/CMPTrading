package dev.reza.cmptrading.core.domain

sealed interface DataError: Error {

    enum class Remote: DataError {
        REQUEST_TIMEOUT,
        TOO_MANY_REQUESTS,
        NO_INTERNET,
        SERVER,
        SERIALIZATION,
        UNKNOWN
    }

    enum class LOCAL: DataError {
        DISK_FULL,
        INSUFFICIENT_FUNDS,
        UNKNOWN
    }

}