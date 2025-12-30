package com.portfoliodemo.feature.portfolio.data

import com.portfoliodemo.core.common.throwIfCancellation
import com.portfoliodemo.feature.portfolio.data.local.PortfolioLocalDataSource
import com.portfoliodemo.feature.portfolio.data.remote.PortfolioRemoteDataSource
import com.portfoliodemo.feature.portfolio.domain.PortfolioRepository
import com.portfoliodemo.feature.portfolio.domain.model.PortfolioItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PortfolioRepositoryImpl @Inject constructor(
    private val remoteDataSource: PortfolioRemoteDataSource,
    private val localDataSource: PortfolioLocalDataSource
) : PortfolioRepository {

    override fun getPortfolioHoldings(): Flow<List<PortfolioItem>> {
        return localDataSource.getHoldings()
    }

    override suspend fun refreshHoldings(): Result<Unit> {
        return try {
            val holdings = withContext(Dispatchers.IO) {
                remoteDataSource.getPortfolioHoldings()
            }
            withContext(Dispatchers.IO) {
                localDataSource.saveHoldings(holdings)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            // Re-throw cancellation to maintain coroutine cancellation
            e.throwIfCancellation()
            // If network fails, check if we have cached data
            val cachedHoldings = localDataSource.getHoldingsSync()
            if (cachedHoldings.isEmpty()) {
                // No cached data, return error
                Result.failure(e)
            } else {
                // We have cached data, refresh succeeded (using cached data)
                Result.success(Unit)
            }
        }
    }
}

