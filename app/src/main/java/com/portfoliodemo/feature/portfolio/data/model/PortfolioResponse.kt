package com.portfoliodemo.feature.portfolio.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PortfolioResponse(
    @SerialName("data")
    val data: PortfolioData
)

@Serializable
data class PortfolioData(
    @SerialName("userHolding")
    val userHolding: List<Holding>
)

@Serializable
data class Holding(
    @SerialName("symbol")
    val symbol: String,
    @SerialName("quantity")
    val quantity: Int,
    @SerialName("ltp")
    val ltp: Double,
    @SerialName("avgPrice")
    val avgPrice: Double,
    @SerialName("close")
    val close: Double
)

