package com.portfoliodemo.feature.portfolio.data.mapper

import com.portfoliodemo.core.database.HoldingEntity
import com.portfoliodemo.feature.portfolio.data.model.Holding
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.math.BigDecimal

class HoldingMapperTest {

    @Test
    fun `holding to domain maps correctly with PNL calculation`() {
        val holding = Holding(
            symbol = "TEST",
            quantity = 10,
            ltp = 100.0,
            avgPrice = 90.0,
            close = 95.0
        )

        val result = holding.toDomain()

        assertEquals("TEST", result.symbol)
        assertEquals(10, result.quantity)
        assertEquals(BigDecimal("100.00"), result.ltp)
        assertEquals(BigDecimal("90.00"), result.avgPrice)
        assertEquals(BigDecimal("95.00"), result.close)
        // PNL = (100 - 90) * 10 = 100
        assertEquals(BigDecimal("100.00"), result.pnl.setScale(2))
        // PNL % = ((100 - 90) / 90) * 100 = 11.11
        assertEquals(BigDecimal("11.11"), result.pnlPercentage)
    }

    @Test
    fun `holding entity to domain maps correctly`() {
        val entity = HoldingEntity(
            symbol = "TEST",
            quantity = 5,
            ltp = BigDecimal("50.0"),
            avgPrice = BigDecimal("40.0"),
            close = BigDecimal("45.0")
        )

        val result = entity.toDomain()

        assertEquals("TEST", result.symbol)
        assertEquals(5, result.quantity)
        assertEquals(BigDecimal("50.00"), result.ltp.setScale(2))
        assertEquals(BigDecimal("40.00"), result.avgPrice.setScale(2))
        assertEquals(BigDecimal("45.00"), result.close.setScale(2))
        // PNL = (50 - 40) * 5 = 50
        assertEquals(BigDecimal("50.00"), result.pnl.setScale(2))
        // PNL % = ((50 - 40) / 40) * 100 = 25.0
        assertEquals(BigDecimal("25.00"), result.pnlPercentage)
    }

    @Test
    fun `holding to entity maps correctly`() {
        val holding = Holding(
            symbol = "TEST",
            quantity = 10,
            ltp = 100.0,
            avgPrice = 90.0,
            close = 95.0
        )

        val result = holding.toEntity()

        assertEquals("TEST", result.symbol)
        assertEquals(10, result.quantity)
        assertEquals(BigDecimal("100.00"), result.ltp)
        assertEquals(BigDecimal("90.00"), result.avgPrice)
        assertEquals(BigDecimal("95.00"), result.close)
    }

    @Test
    fun `negative PNL calculation works correctly`() {
        val holding = Holding(
            symbol = "TEST",
            quantity = 10,
            ltp = 80.0,
            avgPrice = 100.0,
            close = 90.0
        )

        val result = holding.toDomain()

        // PNL = (80 - 100) * 10 = -200
        assertEquals(BigDecimal("-200.00"), result.pnl.setScale(2))
        // PNL % = ((80 - 100) / 100) * 100 = -20.0
        assertEquals(BigDecimal("-20.00"), result.pnlPercentage)
    }

    @Test
    fun `zero avgPrice handles division by zero`() {
        val holding = Holding(
            symbol = "TEST",
            quantity = 10,
            ltp = 100.0,
            avgPrice = 0.0,
            close = 95.0
        )

        val result = holding.toDomain()

        assertEquals(BigDecimal.ZERO, result.pnlPercentage)
    }

    @Test
    fun `holding to domain with zero quantity`() {
        val holding = Holding(
            symbol = "TEST",
            quantity = 0,
            ltp = 100.0,
            avgPrice = 90.0,
            close = 95.0
        )

        val result = holding.toDomain()

        assertEquals("TEST", result.symbol)
        assertEquals(0, result.quantity)
        assertEquals(0, BigDecimal("100.00").compareTo(result.ltp.setScale(2)))
        assertEquals(0, BigDecimal("90.00").compareTo(result.avgPrice.setScale(2)))
        assertEquals(0, BigDecimal("95.00").compareTo(result.close.setScale(2)))
        assertEquals(0, BigDecimal.ZERO.compareTo(result.pnl))
        // PNL % = ((100 - 90) / 90) * 100 = 11.11 (calculated based on price, not quantity)
        assertEquals(0, BigDecimal("11.11").compareTo(result.pnlPercentage.setScale(2)))
    }

    @Test
    fun `holding entity to domain with zero quantity`() {
        val entity = HoldingEntity(
            symbol = "TEST",
            quantity = 0,
            ltp = BigDecimal("100.0"),
            avgPrice = BigDecimal("90.0"),
            close = BigDecimal("95.0")
        )

        val result = entity.toDomain()

        assertEquals("TEST", result.symbol)
        assertEquals(0, result.quantity)
        assertEquals(0, BigDecimal("100.00").compareTo(result.ltp.setScale(2)))
        assertEquals(0, BigDecimal("90.00").compareTo(result.avgPrice.setScale(2)))
        assertEquals(0, BigDecimal("95.00").compareTo(result.close.setScale(2)))
        assertEquals(0, BigDecimal.ZERO.compareTo(result.pnl))
        // PNL % = ((100 - 90) / 90) * 100 = 11.11 (calculated based on price, not quantity)
        assertEquals(0, BigDecimal("11.11").compareTo(result.pnlPercentage.setScale(2)))
    }

    @Test
    fun `holding to entity with decimal values`() {
        val holding = Holding(
            symbol = "TEST",
            quantity = 10,
            ltp = 100.123,
            avgPrice = 90.456,
            close = 95.789
        )

        val result = holding.toEntity()

        assertEquals("TEST", result.symbol)
        assertEquals(10, result.quantity)
        assertEquals(BigDecimal("100.12"), result.ltp)
        assertEquals(BigDecimal("90.46"), result.avgPrice)
        assertEquals(BigDecimal("95.79"), result.close)
    }

    @Test
    fun `holding to domain with very small values`() {
        val holding = Holding(
            symbol = "TEST",
            quantity = 1,
            ltp = 0.01,
            avgPrice = 0.005,
            close = 0.008
        )

        val result = holding.toDomain()

        assertEquals("TEST", result.symbol)
        assertEquals(1, result.quantity)
        // After rounding: ltp = 0.01, avgPrice = 0.01 (0.005 rounds to 0.01)
        // PNL = (0.01 - 0.01) * 1 = 0.00
        assertEquals(0, BigDecimal.ZERO.compareTo(result.pnl))
        // PNL % = ((0.01 - 0.01) / 0.01) * 100 = 0.00
        assertEquals(0, BigDecimal.ZERO.compareTo(result.pnlPercentage.setScale(2)))
    }

    @Test
    fun `holding entity to domain with negative PNL`() {
        val entity = HoldingEntity(
            symbol = "TEST",
            quantity = 10,
            ltp = BigDecimal("80.0"),
            avgPrice = BigDecimal("100.0"),
            close = BigDecimal("90.0")
        )

        val result = entity.toDomain()

        // PNL = (80 - 100) * 10 = -200
        assertEquals(BigDecimal("-200.00"), result.pnl.setScale(2))
        // PNL % = ((80 - 100) / 100) * 100 = -20.0
        assertEquals(BigDecimal("-20.00"), result.pnlPercentage)
    }

    @Test
    fun `holding to entity preserves all fields correctly`() {
        val holding = Holding(
            symbol = "SYMBOL123",
            quantity = 999,
            ltp = 123.45,
            avgPrice = 100.00,
            close = 110.00
        )

        val result = holding.toEntity()

        assertEquals("SYMBOL123", result.symbol)
        assertEquals(999, result.quantity)
        assertEquals(BigDecimal("123.45"), result.ltp)
        assertEquals(BigDecimal("100.00"), result.avgPrice)
        assertEquals(BigDecimal("110.00"), result.close)
    }
}

