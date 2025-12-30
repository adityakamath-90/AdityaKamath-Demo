package com.portfoliodemo.feature.portfolio.data.remote

import com.portfoliodemo.core.common.throwIfCancellation
import com.portfoliodemo.core.network.PortfolioApiService
import com.portfoliodemo.core.network.retryWithExponentialBackoff
import com.portfoliodemo.feature.portfolio.data.mapper.toDomain
import com.portfoliodemo.feature.portfolio.domain.model.PortfolioItem
import javax.inject.Inject

class PortfolioRemoteDataSource @Inject constructor(
    private val apiService: PortfolioApiService
) {
    suspend fun getPortfolioHoldings(): List<PortfolioItem> {
        return try {
            // Retry with exponential backoff
            val response = retryWithExponentialBackoff {
                apiService.getPortfolioHoldings()
            }
            
            response.data.userHolding.map { it.toDomain() }
        }
        catch (e: Exception) {
            // Re-throw cancellation to maintain coroutine cancellation
            e.throwIfCancellation()
            throw e
        }
    }
}
