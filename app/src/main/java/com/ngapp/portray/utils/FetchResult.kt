package com.ngapp.portray.utils

/**
 * Generic class for holding success response, error response and loading status
 */
data class FetchResult<out T>(val status: Status, val data: T?, val error: Error?, val message: String?) {

    enum class Status {
        SUCCESS,
        ERROR,
        LOADING
    }

    companion object {
        fun <T> success(data: T?): FetchResult<T> {
            return FetchResult(Status.SUCCESS, data, null, null)
        }

        fun <T> error(message: String, error: Error?): FetchResult<T> {
            return FetchResult(Status.ERROR, null, error, message)
        }

        fun <T> loading(data: T? = null): FetchResult<T> {
            return FetchResult(Status.LOADING, data, null, null)
        }
    }

    override fun toString(): String {
        return "Result(status=$status, data=$data, error=$error, message=$message)"
    }
}