package com.portfoliodemo.feature.portfolio.presentation

import androidx.compose.runtime.Immutable
import com.portfoliodemo.feature.portfolio.domain.model.PortfolioItem
import com.portfoliodemo.feature.portfolio.domain.model.PortfolioSummary

sealed class PortfolioUiState {
    data object Loading : PortfolioUiState()
    
    @Immutable
    data class Success(
        val holdings: List<PortfolioItem>,
        val summary: PortfolioSummary,
        val isSummaryExpanded: Boolean = false
    ) : PortfolioUiState()
    
    data class Error(val message: String) : PortfolioUiState()
}

