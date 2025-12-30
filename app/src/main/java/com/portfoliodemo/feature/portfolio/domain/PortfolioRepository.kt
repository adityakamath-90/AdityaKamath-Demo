package com.portfoliodemo.feature.portfolio.domain

import com.portfoliodemo.feature.portfolio.domain.model.PortfolioItem
import kotlinx.coroutines.flow.Flow

interface PortfolioRepository {
    fun getPortfolioHoldings(): Flow<List<PortfolioItem>>
    suspend fun refreshHoldings(): Result<Unit>
}

