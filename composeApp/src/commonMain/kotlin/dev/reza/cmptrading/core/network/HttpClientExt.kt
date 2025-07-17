package dev.reza.cmptrading.core.network

import dev.reza.cmptrading.core.domain.DataError
import dev.reza.cmptrading.core.domain.Result
import io.ktor.client.call.body
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.statement.HttpResponse
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.coroutines.ensureActive
import kotlin.coroutines.coroutineContext

/**
 * Executes a network request safely and wraps the result in a [Result] object.
 *
 * This function handles common network exceptions and maps them to [DataError.Remote] types.
 *
 * @param T The expected type of the successful response body.
 * @param execute A suspending lambda function that performs the actual network request and returns an [HttpResponse].
 * @return A [Result] object containing either the successful response of type [T] or a [DataError.Remote]
 *         in case of an error.
 * @see responseToResult
 */
suspend inline fun <reified T> safeCall(
    crossinline execute: suspend () -> HttpResponse
): Result<T, DataError.Remote> {

    val response = try {
        execute()
    } catch (e: SocketTimeoutException) {
        return Result.Error(DataError.Remote.REQUEST_TIMEOUT)
    } catch (e: UnresolvedAddressException) {
        return Result.Error(DataError.Remote.NO_INTERNET)
    } catch (e: kotlinx.serialization.SerializationException) {
        return Result.Error(DataError.Remote.SERIALIZATION)
    } catch (e: Exception) {
        coroutineContext.ensureActive()
        return Result.Error(DataError.Remote.UNKNOWN)
    }

    return responseToResult(response)
}

/**
 * Converts an [HttpResponse] to a [Result] object.
 *
 * This function inspects the HTTP status code of the response and attempts to deserialize
 * the body if the request was successful (2xx status codes). It maps various HTTP error
 * codes to corresponding [DataError.Remote] types.
 *
 * @param T The expected type of the successful response body.
 * @param response The [HttpResponse] to convert.
 * @return A [Result] object containing either the successful response of type [T] or a [DataError.Remote]
 *         in case of an error or non-successful status code.
 */
suspend inline fun <reified T> responseToResult(
    response: HttpResponse
): Result<T, DataError.Remote> {
    return when (response.status.value) {
        in 200..299 -> {
            try {
                Result.Success(response.body<T>())
            } catch (e: Exception) {
                Result.Error(DataError.Remote.SERIALIZATION)
            }
        }

        408 -> Result.Error(DataError.Remote.REQUEST_TIMEOUT)
        429 -> Result.Error(DataError.Remote.TOO_MANY_REQUESTS)
        // Consider adding more specific 4xx error handling if needed (e.g., 401 Unauthorized, 403 Forbidden, 404 Not Found)
        in 500..599 -> {
            Result.Error(DataError.Remote.SERVER)
        }

        else -> Result.Error(DataError.Remote.UNKNOWN)
    }
}