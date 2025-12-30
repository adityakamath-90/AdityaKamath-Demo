package com.portfoliodemo.feature.portfolio.data.remote

import com.portfoliodemo.core.network.PortfolioApiService
import com.portfoliodemo.feature.portfolio.data.model.Holding
import com.portfoliodemo.feature.portfolio.data.model.PortfolioData
import com.portfoliodemo.feature.portfolio.data.model.PortfolioResponse
import com.portfoliodemo.feature.portfolio.domain.model.PortfolioItem
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class PortfolioRemoteDataSourceTest {

    private lateinit var apiService: PortfolioApiService
    private lateinit var dataSource: PortfolioRemoteDataSource

    @Before
    fun setup() {
        apiService = mock()
        dataSource = PortfolioRemoteDataSource(apiService)
    }

    @Test
    fun `getPortfolioHoldings returns mapped domain items from api response`() = runTest {
        // Given
        val response = PortfolioResponse(
            data = PortfolioData(
                userHolding = listOf(
                    Holding(
                        symbol = "TEST1",
                        quantity = 10,
                        ltp = 100.0,
                        avgPrice = 90.0,
                        close = 95.0
                    ),
                    Holding(
                        symbol = "TEST2",
                        quantity = 5,
                        ltp = 50.0,
                        avgPrice = 40.0,
                        close = 45.0
                    )
                )
            )
        )
        whenever(apiService.getPortfolioHoldings()).thenReturn(response)

        // When
        val result = dataSource.getPortfolioHoldings()

        // Then
        assertEquals(2, result.size)
        assertEquals("TEST1", result[0].symbol)
        assertEquals(10, result[0].quantity)
        assertEquals("TEST2", result[1].symbol)
        assertEquals(5, result[1].quantity)
    }

    @Test
    fun `getPortfolioHoldings returns empty list when api returns empty holdings`() = runTest {
        // Given
        val response = PortfolioResponse(
            data = PortfolioData(
                userHolding = emptyList()
            )
        )
        whenever(apiService.getPortfolioHoldings()).thenReturn(response)

        // When
        val result = dataSource.getPortfolioHoldings()

        // Then
        assertTrue(result.isEmpty())
    }

    @Test
    fun `getPortfolioHoldings calls api service`() = runTest {
        // Given
        val response = PortfolioResponse(
            data = PortfolioData(
                userHolding = listOf(
                    Holding(
                        symbol = "TEST",
                        quantity = 10,
                        ltp = 100.0,
                        avgPrice = 90.0,
                        close = 95.0
                    )
                )
            )
        )
        whenever(apiService.getPortfolioHoldings()).thenReturn(response)

        // When
        dataSource.getPortfolioHoldings()

        // Then
        verify(apiService).getPortfolioHoldings()
    }

    @Test
    fun `getPortfolioHoldings correctly maps holding data`() = runTest {
        // Given
        val response = PortfolioResponse(
            data = PortfolioData(
                userHolding = listOf(
                    Holding(
                        symbol = "TEST",
                        quantity = 10,
                        ltp = 100.0,
                        avgPrice = 90.0,
                        close = 95.0
                    )
                )
            )
        )
        whenever(apiService.getPortfolioHoldings()).thenReturn(response)

        // When
        val result = dataSource.getPortfolioHoldings()

        // Then
        assertEquals(1, result.size)
        val item = result[0]
        assertEquals("TEST", item.symbol)
        assertEquals(10, item.quantity)
        // PNL = (100 - 90) * 10 = 100
        assertEquals(java.math.BigDecimal("100.00"), item.pnl.setScale(2))
        // PNL % = ((100 - 90) / 90) * 100 = 11.11
        assertEquals(java.math.BigDecimal("11.11"), item.pnlPercentage)
    }

    @Test
    fun `getPortfolioHoldings propagates exception from api service`() = runTest {
        // Given
        val exception = Exception("Network error")
        org.mockito.kotlin.doAnswer { throw exception }
            .whenever(apiService).getPortfolioHoldings()

        // When/Then
        try {
            dataSource.getPortfolioHoldings()
            org.junit.Assert.fail("Should have thrown exception")
        } catch (e: Exception) {
            // Exception might be wrapped by retry logic, so check message contains
            assertTrue(e.message?.contains("Network error") == true || e.cause?.message?.contains("Network error") == true)
        }
    }
}
