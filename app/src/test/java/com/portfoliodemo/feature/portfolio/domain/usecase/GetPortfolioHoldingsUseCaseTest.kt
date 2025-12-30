package com.portfoliodemo.feature.portfolio.domain.usecase

import com.portfoliodemo.feature.portfolio.data.FakePortfolioRepository
import com.portfoliodemo.feature.portfolio.domain.model.PortfolioItem
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal

class GetPortfolioHoldingsUseCaseTest {

    private lateinit var fakeRepository: FakePortfolioRepository
    private lateinit var useCase: GetPortfolioHoldingsUseCase

    @Before
    fun setup() {
        fakeRepository = FakePortfolioRepository()
        useCase = GetPortfolioHoldingsUseCase(fakeRepository)
    }

    @Test
    fun `invoke returns holdings from repository`() = runTest {
        val holdings = listOf(
            createPortfolioItem("TEST1", 10, 100.0, 90.0, 95.0),
            createPortfolioItem("TEST2", 5, 50.0, 40.0, 45.0)
        )
        fakeRepository.setHoldings(holdings)

        val result = useCase().first()

        assertEquals(2, result.size)
        assertEquals("TEST1", result[0].symbol)
        assertEquals("TEST2", result[1].symbol)
    }

    @Test
    fun `invoke returns empty list when repository has no holdings`() = runTest {
        fakeRepository.setHoldings(emptyList())

        val result = useCase().first()

        assertTrue(result.isEmpty())
    }

    @Test
    fun `invoke returns updated holdings when repository updates`() = runTest {
        val initialHoldings = listOf(
            createPortfolioItem("INITIAL", 10, 100.0, 90.0, 95.0)
        )
        fakeRepository.setHoldings(initialHoldings)
        
        val firstResult = useCase().first()
        assertEquals(1, firstResult.size)

        val updatedHoldings = listOf(
            createPortfolioItem("UPDATED1", 20, 200.0, 180.0, 190.0),
            createPortfolioItem("UPDATED2", 15, 150.0, 140.0, 145.0)
        )
        fakeRepository.setHoldings(updatedHoldings)
        val secondResult = useCase().first()

        assertEquals(2, secondResult.size)
        assertEquals("UPDATED1", secondResult[0].symbol)
        assertEquals("UPDATED2", secondResult[1].symbol)
    }

    private fun createPortfolioItem(
        symbol: String,
        quantity: Int,
        ltp: Double,
        avgPrice: Double,
        close: Double
    ): PortfolioItem {
        val ltpBd = BigDecimal(ltp.toString())
        val avgPriceBd = BigDecimal(avgPrice.toString())
        val closeBd = BigDecimal(close.toString())
        val pnl = (ltpBd - avgPriceBd) * BigDecimal(quantity)
        val pnlPercentage = if (avgPriceBd > BigDecimal.ZERO) {
            ((ltpBd - avgPriceBd).divide(avgPriceBd, 4, java.math.RoundingMode.HALF_UP) * BigDecimal(100))
                .setScale(2, java.math.RoundingMode.HALF_UP)
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
}
