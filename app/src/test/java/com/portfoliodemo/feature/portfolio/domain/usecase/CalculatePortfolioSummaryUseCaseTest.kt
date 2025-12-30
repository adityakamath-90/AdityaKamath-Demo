package com.portfoliodemo.feature.portfolio.domain.usecase

import com.portfoliodemo.feature.portfolio.domain.model.PortfolioItem
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal

class CalculatePortfolioSummaryUseCaseTest {

    private lateinit var useCase: CalculatePortfolioSummaryUseCase

    @Before
    fun setup() {
        useCase = CalculatePortfolioSummaryUseCase()
    }

    @Test
    fun `calculate summary with empty holdings returns zero values`() {
        val holdings = emptyList<PortfolioItem>()
        val result = useCase(holdings)

        assertEquals(BigDecimal.ZERO, result.currentValue)
        assertEquals(BigDecimal.ZERO, result.totalInvestment)
        assertEquals(BigDecimal.ZERO, result.totalPnl)
        assertEquals(BigDecimal.ZERO, result.totalPnlPercentage)
        assertEquals(BigDecimal.ZERO, result.todayPnl)
    }

    @Test
    fun `calculate summary with single holding`() {
        val holdings = listOf(
            PortfolioItem(
                symbol = "TEST",
                quantity = 10,
                ltp = BigDecimal("100.0"),
                avgPrice = BigDecimal("90.0"),
                close = BigDecimal("95.0"),
                pnl = BigDecimal("100.0"), // (100 - 90) * 10
                pnlPercentage = BigDecimal("11.11")
            )
        )
        val result = useCase(holdings)

        // Current value = 100 * 10 = 1000
        assertEquals(BigDecimal("1000.00"), result.currentValue.setScale(2))
        // Total investment = 90 * 10 = 900
        assertEquals(BigDecimal("900.00"), result.totalInvestment.setScale(2))
        // Total PNL = 1000 - 900 = 100
        assertEquals(BigDecimal("100.00"), result.totalPnl.setScale(2))
        // Total PNL % = (100 / 900) * 100 = 11.11
        assertEquals(BigDecimal("11.11"), result.totalPnlPercentage.setScale(2))
        // Today's PNL = (95 - 100) * 10 = -50
        assertEquals(BigDecimal("-50.00"), result.todayPnl.setScale(2))
    }

    @Test
    fun `calculate summary with multiple holdings`() {
        val holdings = listOf(
            PortfolioItem(
                symbol = "STOCK1",
                quantity = 5,
                ltp = BigDecimal("50.0"),
                avgPrice = BigDecimal("40.0"),
                close = BigDecimal("45.0"),
                pnl = BigDecimal("50.0"),
                pnlPercentage = BigDecimal("25.0")
            ),
            PortfolioItem(
                symbol = "STOCK2",
                quantity = 10,
                ltp = BigDecimal("100.0"),
                avgPrice = BigDecimal("110.0"),
                close = BigDecimal("105.0"),
                pnl = BigDecimal("-100.0"),
                pnlPercentage = BigDecimal("-9.09")
            )
        )
        val result = useCase(holdings)

        // Current value = (50 * 5) + (100 * 10) = 250 + 1000 = 1250
        assertEquals(BigDecimal("1250.00"), result.currentValue.setScale(2))
        // Total investment = (40 * 5) + (110 * 10) = 200 + 1100 = 1300
        assertEquals(BigDecimal("1300.00"), result.totalInvestment.setScale(2))
        // Total PNL = 1250 - 1300 = -50
        assertEquals(BigDecimal("-50.00"), result.totalPnl.setScale(2))
        // Total PNL % = (-50 / 1300) * 100 = -3.85
        assertEquals(BigDecimal("-3.85"), result.totalPnlPercentage.setScale(2))
        // Today's PNL = ((45 - 50) * 5) + ((105 - 100) * 10) = -25 + 50 = 25
        assertEquals(BigDecimal("25.00"), result.todayPnl.setScale(2))
    }

    @Test
    fun `calculate summary with zero total investment handles division by zero`() {
        val holdings = listOf(
            PortfolioItem(
                symbol = "TEST",
                quantity = 0,
                ltp = BigDecimal("100.0"),
                avgPrice = BigDecimal("90.0"),
                close = BigDecimal("95.0"),
                pnl = BigDecimal.ZERO,
                pnlPercentage = BigDecimal.ZERO
            )
        )
        val result = useCase(holdings)

        // Use compareTo with setScale to handle BigDecimal precision
        assertEquals(0, BigDecimal.ZERO.setScale(2).compareTo(result.currentValue.setScale(2)))
        assertEquals(0, BigDecimal.ZERO.setScale(2).compareTo(result.totalInvestment.setScale(2)))
        assertEquals(0, BigDecimal.ZERO.setScale(2).compareTo(result.totalPnl.setScale(2)))
        assertEquals(0, BigDecimal.ZERO.setScale(2).compareTo(result.totalPnlPercentage.setScale(2)))
        assertEquals(0, BigDecimal.ZERO.setScale(2).compareTo(result.todayPnl.setScale(2)))
    }
}

