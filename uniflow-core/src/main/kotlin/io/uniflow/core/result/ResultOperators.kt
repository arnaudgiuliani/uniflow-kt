package io.uniflow.core.result

import io.uniflow.core.flow.UIState

suspend fun <R : Any, T : Any> FlowResult<R>.map(result: suspend (R) -> T): FlowResult<T> {
    return when (this) {
        is FlowResult.Success -> flowSuccess(result(this.value))
        is FlowResult.Error -> this
    }
}

suspend fun <R : Any, T : Any> FlowResult<R>.flatMap(result: suspend (R) -> FlowResult<T>): FlowResult<T> {
    return when (this) {
        is FlowResult.Success -> result(this.value)
        is FlowResult.Error -> this
    }
}

fun <R : Any> FlowResult<R>.value(): R {
    return when (this) {
        is FlowResult.Success -> this.value
        is FlowResult.Error -> exception?.let { throw exception } ?: kotlin.error(message)
    }
}

fun <R : Any> FlowResult<R>.valueOrNull(): R? {
    return when (this) {
        is FlowResult.Success -> this.value
        is FlowResult.Error -> null
    }
}

suspend fun <R : Any> FlowResult<R>.onValue(block: suspend (R) -> Unit): FlowResult<R> {
    return when (this) {
        is FlowResult.Success -> {
            block(this.value)
            this
        }
        is FlowResult.Error -> exception?.let { throw exception } ?: kotlin.error(message)
    }
}

suspend fun <R : Any> FlowResult<R>.mapUIState(onResult: suspend (R) -> UIState): UIState {
    return when (this) {
        is FlowResult.Success -> onResult(this.value)
        is FlowResult.Error -> exception?.let { throw exception } ?: kotlin.error(message)
    }
}
