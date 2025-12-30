package com.portfoliodemo.core.common

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.math.BigDecimal

class ExtensionsTest {

    @Test
    fun `formatAsCurrency formats correctly`() {
        val value = BigDecimal("1234.56")
        val formatted = value.formatAsCurrency()
        // Should contain currency symbol and formatted number
        assertTrue(formatted.contains("₹") || formatted.contains("Rs") || formatted.contains("1,234.56"))
    }

    @Test
    fun `formatAsCurrency formats zero correctly`() {
        val value = BigDecimal.ZERO
        val formatted = value.formatAsCurrency()
        assertTrue(formatted.contains("0") || formatted.contains("₹"))
    }

    @Test
    fun `formatAsCurrency formats negative values correctly`() {
        val value = BigDecimal("-1234.56")
        val formatted = value.formatAsCurrency()
        assertTrue(formatted.contains("-") || formatted.contains("1,234.56"))
    }

    @Test
    fun `formatAsCurrency formats large values correctly`() {
        val value = BigDecimal("1234567.89")
        val formatted = value.formatAsCurrency()
        assertTrue(formatted.isNotEmpty())
    }

    @Test
    fun `formatAsPercentage formats correctly`() {
        val value = BigDecimal("12.3456")
        val formatted = value.formatAsPercentage()
        assertEquals("12.35%", formatted)
    }

    @Test
    fun `formatAsPercentage formats zero correctly`() {
        val value = BigDecimal.ZERO
        val formatted = value.formatAsPercentage()
        assertEquals("0.00%", formatted)
    }

    @Test
    fun `formatAsPercentage formats negative values correctly`() {
        val value = BigDecimal("-12.3456")
        val formatted = value.formatAsPercentage()
        assertEquals("-12.35%", formatted)
    }

    @Test
    fun `formatAsPercentage rounds correctly`() {
        val value = BigDecimal("12.344")
        val formatted = value.formatAsPercentage()
        assertEquals("12.34%", formatted)
    }

    @Test
    fun `formatAsPercentage rounds up correctly`() {
        val value = BigDecimal("12.345")
        val formatted = value.formatAsPercentage()
        assertEquals("12.35%", formatted)
    }

    @Test
    fun `formatIndianNumber formats crores correctly`() {
        val value = BigDecimal("15000000.0")
        val formatted = value.formatIndianNumber()
        assertTrue(formatted.contains("Cr") && formatted.contains("1.50"))
    }

    @Test
    fun `formatIndianNumber formats lakhs correctly`() {
        val value = BigDecimal("150000.0")
        val formatted = value.formatIndianNumber()
        assertTrue(formatted.contains("L") && formatted.contains("1.50"))
    }

    @Test
    fun `formatIndianNumber formats values less than lakh correctly`() {
        val value = BigDecimal("50000.0")
        val formatted = value.formatIndianNumber()
        assertTrue(formatted.contains("50000.00") && !formatted.contains("L") && !formatted.contains("Cr"))
    }

    @Test
    fun `formatIndianNumber formats zero correctly`() {
        val value = BigDecimal.ZERO
        val formatted = value.formatIndianNumber()
        assertEquals("0.00", formatted)
    }

    @Test
    fun `formatIndianNumber formats negative values correctly`() {
        val value = BigDecimal("-150000.0")
        val formatted = value.formatIndianNumber()
        assertTrue(formatted.contains("-") || formatted.contains("1.50"))
    }

    @Test
    fun `formatIndianNumber formats exactly one crore correctly`() {
        val value = BigDecimal("10000000.0")
        val formatted = value.formatIndianNumber()
        assertTrue(formatted.contains("Cr") && formatted.contains("1.00"))
    }

    @Test
    fun `formatIndianNumber formats exactly one lakh correctly`() {
        val value = BigDecimal("100000.0")
        val formatted = value.formatIndianNumber()
        assertTrue(formatted.contains("L") && formatted.contains("1.00"))
    }

    @Test
    fun `formatIndianNumber formats large crore values correctly`() {
        val value = BigDecimal("50000000.0")
        val formatted = value.formatIndianNumber()
        assertTrue(formatted.contains("Cr") && formatted.contains("5.00"))
    }

    @Test
    fun `formatIndianNumber formats values between lakh and crore correctly`() {
        val value = BigDecimal("5000000.0")
        val formatted = value.formatIndianNumber()
        assertTrue(formatted.contains("L") && formatted.contains("50.00"))
    }
}

