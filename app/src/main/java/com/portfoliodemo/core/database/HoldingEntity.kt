package com.portfoliodemo.core.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.math.BigDecimal

@Entity(tableName = "holdings")
data class HoldingEntity(
    @PrimaryKey
    val symbol: String,
    val quantity: Int,
    val ltp: BigDecimal,
    val avgPrice: BigDecimal,
    val close: BigDecimal,
    val timestamp: Long = System.currentTimeMillis()
)

