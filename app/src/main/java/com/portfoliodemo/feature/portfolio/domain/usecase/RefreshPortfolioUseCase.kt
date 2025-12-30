package com.portfoliodemo.feature.portfolio.domain.usecase

import com.portfoliodemo.feature.portfolio.domain.PortfolioRepository
import javax.inject.Inject

class RefreshPortfolioUseCase @Inject constructor(
    private val repository: PortfolioRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        return repository.refreshHoldings()
    }
}

