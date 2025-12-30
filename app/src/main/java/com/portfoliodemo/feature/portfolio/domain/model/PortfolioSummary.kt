package com.portfoliodemo.feature.portfolio.domain.model

import androidx.compose.runtime.Immutable
import java.math.BigDecimal

@Immutable
data class PortfolioSummary(
    val currentValue: BigDecimal,
    val totalInvestment: BigDecimal,
    val totalPnl: BigDecimal,
    val totalPnlPercentage: BigDecimal,
    val todayPnl: BigDecimal
)

