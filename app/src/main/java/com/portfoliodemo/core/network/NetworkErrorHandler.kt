package com.portfoliodemo.core.network

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.retryWhen
import java.io.IOException
import java.net.SocketTimeoutException
import kotlin.math.pow
import kotlin.time.Duration.Companion.milliseconds

/**
 * Retry configuration for network operations
 */
data class RetryConfig(
    val maxRetries: Int = 3,
    val initialDelayMillis: Long = 1000,
    val maxDelayMillis: Long = 10000,
    val multiplier: Double = 2.0,
    val retryableExceptions: Set<Class<out Throwable>> = setOf(
        IOException::class.java,
        SocketTimeoutException::class.java
    )
)

/**
 * Exponential backoff retry logic
 */
suspend fun <T> retryWithExponentialBackoff(
    config: RetryConfig = RetryConfig(),
    block: suspend () -> T
): T {
    var currentDelay = config.initialDelayMillis
    var lastException: Throwable? = null

    repeat(config.maxRetries) { attempt ->
        try {
            return block()
        } catch (e: Throwable) {
            // Re-throw cancellation immediately to maintain coroutine cancellation
            if (e is CancellationException) throw e
            lastException = e
            
            // Check if exception is retryable
            val isRetryable = config.retryableExceptions.any { it.isInstance(e) }
            if (!isRetryable || attempt == config.maxRetries - 1) {
                throw e
            }

            // Exponential backoff: delay = initialDelay * (multiplier ^ attempt)
            currentDelay = (currentDelay * config.multiplier.pow(attempt)).toLong()
                .coerceAtMost(config.maxDelayMillis)
            
            delay(currentDelay)
        }
    }

    throw lastException ?: Exception("Unknown error")
}

/**
 * Flow retry with exponential backoff
 */
fun <T> Flow<T>.retryWithBackoff(
    config: RetryConfig = RetryConfig()
): Flow<T> {
    return retryWhen { cause, attempt ->
        val isRetryable = config.retryableExceptions.any { it.isInstance(cause) }
        if (!isRetryable || attempt >= config.maxRetries) {
            return@retryWhen false
        }

        val delay = (config.initialDelayMillis * config.multiplier.pow(attempt.toDouble()))
            .toLong()
            .coerceAtMost(config.maxDelayMillis)
        
        delay(delay.milliseconds)
        true
    }
}
