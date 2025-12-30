package com.portfoliodemo.feature.portfolio.data.mapper

import com.portfoliodemo.core.database.HoldingEntity
import com.portfoliodemo.feature.portfolio.data.model.Holding
import com.portfoliodemo.feature.portfolio.domain.model.PortfolioItem
import java.math.BigDecimal
import java.math.RoundingMode

fun Holding.toEntity(): HoldingEntity {
    return HoldingEntity(
        symbol = symbol,
        quantity = quantity,
        ltp = BigDecimal(ltp.toString()).setScale(2, RoundingMode.HALF_UP),
        avgPrice = BigDecimal(avgPrice.toString()).setScale(2, RoundingMode.HALF_UP),
        close = BigDecimal(close.toString()).setScale(2, RoundingMode.HALF_UP)
    )
}

fun HoldingEntity.toDomain(): PortfolioItem {
    val pnl = (ltp - avgPrice) * BigDecimal(quantity)
    val pnlPercentage = if (avgPrice > BigDecimal.ZERO) {
        ((ltp - avgPrice).divide(avgPrice, 4, RoundingMode.HALF_UP) * BigDecimal(100))
            .setScale(2, RoundingMode.HALF_UP)
    } else {
        BigDecimal.ZERO
    }
    return PortfolioItem(
        symbol = symbol,
        quantity = quantity,
        ltp = ltp,
        avgPrice = avgPrice,
        close = close,
        pnl = pnl,
        pnlPercentage = pnlPercentage
    )
}

fun Holding.toDomain(): PortfolioItem {
    val ltpBd = BigDecimal(ltp.toString()).setScale(2, RoundingMode.HALF_UP)
    val avgPriceBd = BigDecimal(avgPrice.toString()).setScale(2, RoundingMode.HALF_UP)
    val closeBd = BigDecimal(close.toString()).setScale(2, RoundingMode.HALF_UP)

    val pnl = (ltpBd - avgPriceBd) * BigDecimal(quantity)
    val pnlPercentage = if (avgPriceBd > BigDecimal.ZERO) {
        ((ltpBd - avgPriceBd).divide(avgPriceBd, 4, RoundingMode.HALF_UP) * BigDecimal(100))
            .setScale(2, RoundingMode.HALF_UP)
    } else {
        BigDecimal.ZERO
    }
    return PortfolioItem(
        symbol = symbol,
        quantity = quantity,
        ltp = ltpBd,
        avgPrice = avgPriceBd,
        close = closeBd,
        pnl = pnl,
        pnlPercentage = pnlPercentage
    )
}

