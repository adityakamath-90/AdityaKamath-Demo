package com.portfoliodemo.feature.portfolio.data.local

import com.portfoliodemo.core.database.HoldingDao
import com.portfoliodemo.core.database.HoldingEntity
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

class PortfolioLocalDataSourceTest {

    private lateinit var holdingDao: HoldingDao
    private lateinit var dataSource: PortfolioLocalDataSource

    @Before
    fun setup() {
        holdingDao = mock()
        dataSource = PortfolioLocalDataSource(holdingDao)
    }

    @Test
    fun `getHoldings returns mapped domain items from dao`() = runTest {
        // Given
        val entities = listOf(
            HoldingEntity(
                symbol = "TEST1",
                quantity = 10,
                ltp = BigDecimal("100.00"),
                avgPrice = BigDecimal("90.00"),
                close = BigDecimal("95.00")
            ),
            HoldingEntity(
                symbol = "TEST2",
                quantity = 5,
                ltp = BigDecimal("50.00"),
                avgPrice = BigDecimal("40.00"),
                close = BigDecimal("45.00")
            )
        )
        whenever(holdingDao.getAllHoldings()).thenReturn(flowOf(entities))

        // When
        val result = dataSource.getHoldings().first()

        // Then
        assertEquals(2, result.size)
        assertEquals("TEST1", result[0].symbol)
        assertEquals(10, result[0].quantity)
        assertEquals("TEST2", result[1].symbol)
        assertEquals(5, result[1].quantity)
    }

    @Test
    fun `getHoldings returns empty list when dao returns empty`() = runTest {
        // Given
        whenever(holdingDao.getAllHoldings()).thenReturn(flowOf(emptyList()))

        // When
        val result = dataSource.getHoldings().first()

        // Then
        assertTrue(result.isEmpty())
    }

    @Test
    fun `saveHoldings converts domain items to entities and saves to dao`() = runTest {
        // Given
        val holdings = listOf(
            PortfolioItem(
                symbol = "TEST1",
                quantity = 10,
                ltp = BigDecimal("100.00"),
                avgPrice = BigDecimal("90.00"),
                close = BigDecimal("95.00"),
                pnl = BigDecimal("100.00"),
                pnlPercentage = BigDecimal("11.11")
            ),
            PortfolioItem(
                symbol = "TEST2",
                quantity = 5,
                ltp = BigDecimal("50.00"),
                avgPrice = BigDecimal("40.00"),
                close = BigDecimal("45.00"),
                pnl = BigDecimal("50.00"),
                pnlPercentage = BigDecimal("25.00")
            )
        )

        // When
        dataSource.saveHoldings(holdings)

        // Then
        verify(holdingDao).insertHoldings(any())
    }

    @Test
    fun `saveHoldings correctly maps domain items to entities`() = runTest {
        // Given
        val holdings = listOf(
            PortfolioItem(
                symbol = "TEST",
                quantity = 10,
                ltp = BigDecimal("100.00"),
                avgPrice = BigDecimal("90.00"),
                close = BigDecimal("95.00"),
                pnl = BigDecimal("100.00"),
                pnlPercentage = BigDecimal("11.11")
            )
        )

        // When
        dataSource.saveHoldings(holdings)

        // Then
        verify(holdingDao).insertHoldings(
            org.mockito.kotlin.argThat { entities ->
                entities.size == 1 &&
                entities[0].symbol == "TEST" &&
                entities[0].quantity == 10 &&
                entities[0].ltp == BigDecimal("100.00") &&
                entities[0].avgPrice == BigDecimal("90.00") &&
                entities[0].close == BigDecimal("95.00")
            }
        )
    }

    @Test
    fun `getHoldingsSync returns mapped domain items from dao`() = runTest {
        // Given
        val entities = listOf(
            HoldingEntity(
                symbol = "TEST",
                quantity = 10,
                ltp = BigDecimal("100.00"),
                avgPrice = BigDecimal("90.00"),
                close = BigDecimal("95.00")
            )
        )
        whenever(holdingDao.getAllHoldingsSync()).thenReturn(entities)

        // When
        val result = dataSource.getHoldingsSync()

        // Then
        assertEquals(1, result.size)
        assertEquals("TEST", result[0].symbol)
        assertEquals(10, result[0].quantity)
    }

    @Test
    fun `clearHoldings calls dao deleteAllHoldings`() = runTest {
        // When
        dataSource.clearHoldings()

        // Then
        verify(holdingDao).deleteAllHoldings()
    }

    @Test
    fun `saveHoldings handles empty list`() = runTest {
        // Given
        val holdings = emptyList<PortfolioItem>()

        // When
        dataSource.saveHoldings(holdings)

        // Then
        verify(holdingDao).insertHoldings(emptyList())
    }
}
