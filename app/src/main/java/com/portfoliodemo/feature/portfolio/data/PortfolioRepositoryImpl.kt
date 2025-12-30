package com.portfoliodemo.feature.portfolio.data

import com.portfoliodemo.core.common.throwIfCancellation
import com.portfoliodemo.core.network.NetworkConnectivityManager
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
    private val localDataSource: PortfolioLocalDataSource,
    private val networkConnectivityManager: NetworkConnectivityManager
) : PortfolioRepository {

    override fun getPortfolioHoldings(): Flow<List<PortfolioItem>> {
        return localDataSource.getHoldings()
    }

    override suspend fun refreshHoldings(): Result<Unit> {
        return try {
            // Check if network is available
            if (networkConnectivityManager.isNetworkAvailable()) {
                // Network available - fetch from server
                val holdings = withContext(Dispatchers.IO) {
                    remoteDataSource.getPortfolioHoldings()
                }
                withContext(Dispatchers.IO) {
                    localDataSource.saveHoldings(holdings)
                }
                Result.success(Unit)
            } else {
                // No network - check if we have cached data
                val cachedHoldings = localDataSource.getHoldingsSync()
                if (cachedHoldings.isEmpty()) {
                    // No network and no cached data
                    Result.failure(Exception("No internet connection and no cached data available"))
                } else {
                    // No network but we have cached data - use it
                    Result.success(Unit)
                }
            }
        } catch (e: Exception) {
            // Re-throw cancellation to maintain coroutine cancellation
            e.throwIfCancellation()
            // If network fails, check if we have cached data
            val cachedHoldings = localDataSource.getHoldingsSync()
            if (cachedHoldings.isEmpty()) {
                Result.failure(e)
            } else {
                Result.success(Unit)
            }
        }
    }
}

