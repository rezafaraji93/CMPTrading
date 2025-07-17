package dev.reza.cmptrading.core.domain

/**
 * Represents a result of an operation that can either be a success or an error.
 * @param D The type of the data in case of success.
 * @param E The type of the error in case of failure, must extend [Error].
 */
sealed interface Result<out D, out E : Error> {
    data class Success<out D>(val data: D) : Result<D, Nothing>
    data class Error<out E : dev.reza.cmptrading.core.domain.Error>(val error: E) :
        Result<Nothing, E>

}

/**
 * Transforms the data of a [Result.Success] using the given [transform] function.
 * If the [Result] is an [Result.Error], it returns the original error.
 *
 * @param T The original type of the data in [Result.Success].
 * @param E The type of the error in [Result.Error].
 * @param R The type of the transformed data.
 * @param transform A function to apply to the data if the result is a success.
 * @return A new [Result] with transformed data if it was a success, or the original error.
 */
inline fun <T, E : Error, R> Result<T, E>.map(transform: (T) -> R): Result<R, E> {
    return when (this) {
        is Result.Success -> Result.Success(transform(data))
        is Result.Error -> this
    }
}
/**
 * Converts a [Result] with any data type [T] to an [EmptyResult], which is a [Result] with `Unit` as its data type.
 * This is useful when the actual data of a successful result is not important, only the success status itself.
 *
 * @param T The original type of the data in [Result.Success].
 * @param E The type of the error in [Result.Error].
 * @return An [EmptyResult] which is `Result.Success(Unit)` if the original was a success, or the original [Result.Error].
 */
fun <T, E: Error> Result<T, E>.asEmptyDataResult(): EmptyResult<E> {
    return map {  }
}

/**
 * Performs the given [action] if the [Result] is a [Result.Success].
 * Returns the original [Result] regardless of whether the action was performed.
 *
 * @param T The type of the data in [Result.Success].
 * @param E The type of the error in [Result.Error].
 * @param action The action to perform with the data if the result is a success.
 * @return The original [Result] instance.
 */
inline fun <T, E: Error> Result<T, E>.onSuccess(action: (T) -> Unit): Result<T, E> {
    if (this is Result.Success) {
        action(data)
    }
    return this
}
/**
 * Performs the given [action] if the [Result] is an [Result.Error].
 * Returns the original [Result] regardless of whether the action was performed.
 *
 * @param T The type of the data in [Result.Success].
 * @param E The type of the error in [Result.Error].
 * @param action The action to perform with the error if the result is an error.
 * @return The original [Result] instance.
 */
inline fun <T, E: Error> Result<T, E>.onError(action: (E) -> Unit): Result<T, E> {
    if (this is Result.Error) {
        action(error)
    }
    return this
}

/**
 * Type alias for a [Result] where the success data type is `Unit`.
 */
typealias EmptyResult<E> = Result<Unit, E>