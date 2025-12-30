package com.portfoliodemo.feature.portfolio.domain.usecase

import com.portfoliodemo.feature.portfolio.domain.model.PortfolioItem
import com.portfoliodemo.feature.portfolio.domain.model.PortfolioSummary
import java.math.BigDecimal
import java.math.RoundingMode
import javax.inject.Inject

class CalculatePortfolioSummaryUseCase @Inject constructor() {

    operator fun invoke(holdings: List<PortfolioItem>): PortfolioSummary {
        if (holdings.isEmpty()) {
            return PortfolioSummary(
                currentValue = BigDecimal.ZERO,
                totalInvestment = BigDecimal.ZERO,
                totalPnl = BigDecimal.ZERO,
                totalPnlPercentage = BigDecimal.ZERO,
                todayPnl = BigDecimal.ZERO
            )
        }

        // 1. Current value = sum of (LTP × quantity) for all holdings
        val currentValue = holdings.fold(BigDecimal.ZERO) { acc, holding ->
            acc + (holding.ltp * BigDecimal(holding.quantity))
        }

        // 2. Total investment = sum of (avgPrice × quantity) for all holdings
        val totalInvestment = holdings.fold(BigDecimal.ZERO) { acc, holding ->
            acc + (holding.avgPrice * BigDecimal(holding.quantity))
        }

        // 3. Total PNL = Current value - Total investment
        val totalPnl = currentValue - totalInvestment

        // 4. Total PNL percentage = (Total PNL / Total investment) × 100
        val totalPnlPercentage = if (totalInvestment > BigDecimal.ZERO) {
            (totalPnl.divide(totalInvestment, 4, RoundingMode.HALF_UP) * BigDecimal(100))
                .setScale(2, RoundingMode.HALF_UP)
        } else {
            BigDecimal.ZERO
        }

        // 5. Today's PNL = sum of ((close - LTP) × quantity) for all holdings
        val todayPnl = holdings.fold(BigDecimal.ZERO) { acc, holding ->
            acc + ((holding.close - holding.ltp) * BigDecimal(holding.quantity))
        }

        return PortfolioSummary(
            currentValue = currentValue,
            totalInvestment = totalInvestment,
            totalPnl = totalPnl,
            totalPnlPercentage = totalPnlPercentage,
            todayPnl = todayPnl
        )
    }
}

