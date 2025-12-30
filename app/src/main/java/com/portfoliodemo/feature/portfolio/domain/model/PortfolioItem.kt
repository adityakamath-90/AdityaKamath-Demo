package com.portfoliodemo.feature.portfolio.domain.model

import androidx.compose.runtime.Immutable
import java.math.BigDecimal

@Immutable
data class PortfolioItem(
    val symbol: String,
    val quantity: Int,
    val ltp: BigDecimal,
    val avgPrice: BigDecimal,
    val close: BigDecimal,
    val pnl: BigDecimal,
    val pnlPercentage: BigDecimal
)

