package com.portfoliodemo.core.common

import kotlinx.coroutines.CancellationException
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.NumberFormat
import java.util.Locale

fun BigDecimal.formatAsCurrency(): String {
    val locale = Locale.Builder().setLanguage("en").setRegion("IN").build()
    val formatter = NumberFormat.getCurrencyInstance(locale)
    return formatter.format(this)
}

fun BigDecimal.formatAsPercentage(): String {
    return String.format(Locale.getDefault(), "%.2f%%", this.setScale(2, RoundingMode.HALF_UP))
}

fun BigDecimal.formatIndianNumber(): String {
    val tenMillion = BigDecimal("10000000")
    val oneLakh = BigDecimal("100000")

    return when {
        this >= tenMillion -> {
            val value = this.divide(tenMillion, 2, RoundingMode.HALF_UP)
            String.format(Locale.getDefault(), "%.2f Cr", value)
        }
        this >= oneLakh -> {
            val value = this.divide(oneLakh, 2, RoundingMode.HALF_UP)
            String.format(Locale.getDefault(), "%.2f L", value)
        }
        else -> String.format(Locale.getDefault(), "%.2f", this.setScale(2, RoundingMode.HALF_UP))
    }
}

/**
 * Extension function to re-throw CancellationException to maintain coroutine cancellation
 */
fun Exception.throwIfCancellation() {
    if (this is CancellationException) throw this
}

