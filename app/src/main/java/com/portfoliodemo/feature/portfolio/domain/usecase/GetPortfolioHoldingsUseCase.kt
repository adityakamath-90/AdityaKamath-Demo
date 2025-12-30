package com.portfoliodemo.feature.portfolio.domain.usecase

import com.portfoliodemo.feature.portfolio.domain.PortfolioRepository
import com.portfoliodemo.feature.portfolio.domain.model.PortfolioItem
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPortfolioHoldingsUseCase @Inject constructor(
    private val repository: PortfolioRepository
) {
    operator fun invoke(): Flow<List<PortfolioItem>> {
        return repository.getPortfolioHoldings()
    }
}

