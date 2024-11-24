package ru.master.app.util

import io.ktor.http.HttpStatusCode
import ru.master.app.network.HttpException
import java.net.ConnectException

sealed class Either<out T> private constructor() {

    data class Success<out R>(val value: R) : Either<R>()

    data class Failure(
        val throwable: Throwable? = null,
        val statusCode: HttpStatusCode? = null,
        val details: String = throwable.resolveError(),
    ) : Either<Nothing>()

    val isSuccess: Boolean
        get() = this is Success

    suspend fun onSuccess(block: suspend (T) -> Unit): Either<T> {
        if (this is Success) {
            block(this.value)
        }
        return this
    }

    suspend fun onFailure(block: suspend (Failure) -> Unit): Either<T> {
        if (this is Failure) {
            block(this)
        }
        return this
    }
}

inline fun <reified T> runResulting(
    block: () -> T
): Either<T> {
    return runCatching {
        Either.Success(block())
    }.getOrElse { e ->
        e.printStackTrace()
        Either.Failure(
            throwable = e,
            statusCode = (e as? HttpException)?.statusCode,
        )
    }
}

@JvmName("map")
inline fun <T, reified R> Either<T>.map(
    crossinline transform: (value: T) -> R
): Either<R> {
    return when (this) {
        is Either.Failure -> {
            Either.Failure(throwable = throwable, details = details)
        }
        is Either.Success -> {
            Either.Success(transform(this.value))
        }
    }
}

@JvmName("mapList")
inline fun <T, reified R> Either<List<T>>.map(
    crossinline transform: (value: T) -> R
): Either<List<R>> {
    return when (this) {
        is Either.Failure -> {
            Either.Failure(throwable = throwable, details = details)
        }
        is Either.Success -> {
            Either.Success(value.map(transform))
        }
    }
}

inline fun <R, T : R> Either<T>.getOrElse(onFailure: (Either.Failure) -> R): R {
    return when (this) {
        is Either.Success -> value
        is Either.Failure -> onFailure(this)
    }
}

fun <R, T : R> Either<T>.getOrThrow(): R {
    return when (this) {
        is Either.Success -> value
        is Either.Failure -> throwable?.let { throw throwable } ?: throw NullPointerException()
    }
}

inline fun <reified T> Either<T>.getOrNull(): T? {
    if (this is Either.Success)
        return this.value
    return null
}

private fun Throwable?.resolveError(): String {
    return when (this) {
        is ConnectException -> "Соединение разорвано"
        is HttpException -> message
        else -> "Неизвестная ошибка"
    }
}