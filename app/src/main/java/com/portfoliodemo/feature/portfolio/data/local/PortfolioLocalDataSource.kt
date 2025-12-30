package com.portfoliodemo.feature.portfolio.data.local

import com.portfoliodemo.core.database.HoldingDao
import com.portfoliodemo.core.database.HoldingEntity
import com.portfoliodemo.feature.portfolio.data.mapper.toDomain
import com.portfoliodemo.feature.portfolio.domain.model.PortfolioItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PortfolioLocalDataSource @Inject constructor(
    private val holdingDao: HoldingDao
) {
    fun getHoldings(): Flow<List<PortfolioItem>> {
        return holdingDao.getAllHoldings().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    suspend fun saveHoldings(holdings: List<PortfolioItem>) {
        val entities = holdings.map { holding ->
            HoldingEntity(
                symbol = holding.symbol,
                quantity = holding.quantity,
                ltp = holding.ltp,
                avgPrice = holding.avgPrice,
                close = holding.close
            )
        }
        holdingDao.insertHoldings(entities)
    }

    suspend fun getHoldingsSync(): List<PortfolioItem> {
        return holdingDao.getAllHoldingsSync().map { it.toDomain() }
    }

    suspend fun clearHoldings() {
        holdingDao.deleteAllHoldings()
    }
}

