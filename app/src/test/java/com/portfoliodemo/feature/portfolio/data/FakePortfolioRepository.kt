package com.portfoliodemo.feature.portfolio.data

import com.portfoliodemo.feature.portfolio.domain.PortfolioRepository
import com.portfoliodemo.feature.portfolio.domain.model.PortfolioItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Fake implementation of PortfolioRepository for testing
 * Uses in-memory storage with Flow support
 */
class FakePortfolioRepository : PortfolioRepository {
    private val _holdings = MutableStateFlow<List<PortfolioItem>>(emptyList())
    private var refreshResult: Result<Unit> = Result.success(Unit)
    
    override fun getPortfolioHoldings(): Flow<List<PortfolioItem>> {
        return _holdings.asStateFlow()
    }
    
    override suspend fun refreshHoldings(): Result<Unit> {
        return refreshResult
    }

    fun setHoldings(holdings: List<PortfolioItem>) {
        _holdings.value = holdings
    }
    
    fun setRefreshResult(result: Result<Unit>) {
        this.refreshResult = result
    }
}
