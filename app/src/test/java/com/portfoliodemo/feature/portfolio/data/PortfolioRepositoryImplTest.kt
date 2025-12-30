package com.portfoliodemo.feature.portfolio.data

import com.portfoliodemo.feature.portfolio.data.local.PortfolioLocalDataSource
import com.portfoliodemo.feature.portfolio.data.remote.PortfolioRemoteDataSource
import com.portfoliodemo.feature.portfolio.domain.model.PortfolioItem
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.math.BigDecimal

class PortfolioRepositoryImplTest {

    private lateinit var mockLocalDataSource: PortfolioLocalDataSource
    private lateinit var mockRemoteDataSource: PortfolioRemoteDataSource
    private lateinit var repository: PortfolioRepositoryImpl

    @Before
    fun setup() {
        mockLocalDataSource = mock()
        mockRemoteDataSource = mock()
        repository = PortfolioRepositoryImpl(
            remoteDataSource = mockRemoteDataSource,
            localDataSource = mockLocalDataSource
        )
    }

    @Test
    fun `getPortfolioHoldings returns holdings from local data source`() = runTest {
        val holdings = listOf(
            createPortfolioItem("TEST1", 10, 100.0, 90.0, 95.0),
            createPortfolioItem("TEST2", 5, 50.0, 40.0, 45.0)
        )
        whenever(mockLocalDataSource.getHoldings()).thenReturn(flowOf(holdings))

        val result = repository.getPortfolioHoldings().first()

        assertEquals(2, result.size)
        assertEquals("TEST1", result[0].symbol)
        assertEquals("TEST2", result[1].symbol)
    }

    @Test
    fun `getPortfolioHoldings returns empty list when local data source is empty`() = runTest {
        whenever(mockLocalDataSource.getHoldings()).thenReturn(flowOf(emptyList()))

        val result = repository.getPortfolioHoldings().first()

        assertTrue(result.isEmpty())
    }

    @Test
    fun `refreshHoldings saves remote holdings to local data source`() = runTest {
        val remoteHoldings = listOf(
            createPortfolioItem("REMOTE1", 10, 100.0, 90.0, 95.0),
            createPortfolioItem("REMOTE2", 5, 50.0, 40.0, 45.0)
        )
        whenever(mockRemoteDataSource.getPortfolioHoldings()).thenReturn(remoteHoldings)

        val result = repository.refreshHoldings()

        assertTrue(result.isSuccess)
        verify(mockRemoteDataSource).getPortfolioHoldings()
        verify(mockLocalDataSource).saveHoldings(remoteHoldings)
    }

    @Test
    fun `refreshHoldings returns success when network fails but cached data exists`() = runTest {
        val cachedHoldings = listOf(
            createPortfolioItem("CACHED1", 10, 100.0, 90.0, 95.0)
        )
        // For suspend functions, use doAnswer or doThrow with Answer
        org.mockito.kotlin.doAnswer { throw Exception("Network error") }
            .whenever(mockRemoteDataSource).getPortfolioHoldings()
        whenever(mockLocalDataSource.getHoldingsSync()).thenReturn(cachedHoldings)

        val result = repository.refreshHoldings()

        assertTrue(result.isSuccess)
        verify(mockRemoteDataSource).getPortfolioHoldings()
        verify(mockLocalDataSource).getHoldingsSync()
    }

    @Test
    fun `refreshHoldings returns failure when network fails and no cached data exists`() = runTest {
        org.mockito.kotlin.doAnswer { throw Exception("Network error") }
            .whenever(mockRemoteDataSource).getPortfolioHoldings()
        whenever(mockLocalDataSource.getHoldingsSync()).thenReturn(emptyList())

        val result = repository.refreshHoldings()

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("Network error") == true)
        verify(mockRemoteDataSource).getPortfolioHoldings()
        verify(mockLocalDataSource).getHoldingsSync()
    }

    @Test
    fun `refreshHoldings updates existing holdings with new data`() = runTest {
        val newHoldings = listOf(
            createPortfolioItem("NEW1", 20, 200.0, 180.0, 190.0),
            createPortfolioItem("NEW2", 15, 150.0, 140.0, 145.0)
        )
        whenever(mockRemoteDataSource.getPortfolioHoldings()).thenReturn(newHoldings)

        val result = repository.refreshHoldings()

        assertTrue(result.isSuccess)
        verify(mockRemoteDataSource).getPortfolioHoldings()
        verify(mockLocalDataSource).saveHoldings(newHoldings)
    }

    @Test
    fun `refreshHoldings handles empty remote response`() = runTest {
        // Given
        whenever(mockRemoteDataSource.getPortfolioHoldings()).thenReturn(emptyList())

        // When
        val result = repository.refreshHoldings()

        // Then
        assertTrue(result.isSuccess)
        verify(mockRemoteDataSource).getPortfolioHoldings()
        verify(mockLocalDataSource).saveHoldings(emptyList())
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
