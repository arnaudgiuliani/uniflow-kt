package io.uniflow.result

import arrow.core.Either
import arrow.core.orNull
import io.uniflow.core.flow.data.UIEvent
import io.uniflow.core.flow.data.UIState

fun <A> Either<Throwable, A>.getOrNull(): A? = orNull()

fun <A> Either<Throwable, A>.get(): A =
    fold(
        ifLeft = { throw it },
        ifRight = { it }
    )

fun <A, R> Either<Throwable, A>.get(result: (A) -> R): R =
    fold(
        ifLeft = { throw it },
        ifRight = { result(it) }
    )

suspend fun <A> Either<Throwable, A>.onSuccess(f: suspend (A) -> Unit): Either<Throwable, A> =
    when (this) {
        is Either.Right -> {
            f(value)
            this
        }
        else -> this
    }

suspend fun <A> Either<Throwable, A>.onFailure(f: suspend (Throwable) -> Unit): Either<Throwable, A> =
    when (this) {
        is Either.Left -> {
            f(value)
            this
        }
        else -> this
    }

fun <A> Either<Throwable, A>.onValue(f: (A) -> Unit): Either<Throwable, A> =
    when (this) {
        is Either.Right -> {
            f(value)
            this
        }
        else -> this
    }

suspend fun <T, R : UIState> Either<Throwable, T>.toState(onSuccess: suspend (T) -> R): R = onSuccess(get())

suspend fun <T, R : UIState> Either<Throwable, T>.toStateOrNull(onSuccess: suspend (T) -> R?): R? =
    fold(
        ifLeft = { null },
        ifRight = { onSuccess(it) }
    )

suspend fun <T, R : UIState> Either<Throwable, T>.toState(onSuccess: suspend (T) -> R, onError: suspend (Throwable) -> R): R =
    fold(
        ifLeft = { onError(it) },
        ifRight = { onSuccess(it) }
    )

suspend fun <T, R : UIEvent> Either<Throwable, T>.toEvent(onSuccess: suspend (T) -> R): R = onSuccess(get())

suspend fun <T, R : UIEvent> Either<Throwable, T>.toEvent(onSuccess: suspend (T) -> R, onError: suspend (Throwable) -> R): R =
    fold(
        ifLeft = { onError(it) },
        ifRight = { onSuccess(it) }
    )

suspend fun <T, R : UIEvent> Either<Throwable, T>.toEventOrNull(onSuccess: suspend (T) -> R): R? =
    fold(
        ifLeft = { null },
        ifRight = { onSuccess(it) }
    )

