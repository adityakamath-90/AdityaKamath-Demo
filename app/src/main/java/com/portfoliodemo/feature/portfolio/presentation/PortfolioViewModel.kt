package com.portfoliodemo.feature.portfolio.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.portfoliodemo.core.di.DefaultDispatcher
import com.portfoliodemo.feature.portfolio.domain.model.PortfolioItem
import com.portfoliodemo.feature.portfolio.domain.usecase.CalculatePortfolioSummaryUseCase
import com.portfoliodemo.feature.portfolio.domain.usecase.GetPortfolioHoldingsUseCase
import com.portfoliodemo.feature.portfolio.domain.usecase.RefreshPortfolioUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class PortfolioViewModel @Inject constructor(
    private val getPortfolioHoldingsUseCase: GetPortfolioHoldingsUseCase,
    private val calculatePortfolioSummaryUseCase: CalculatePortfolioSummaryUseCase,
    private val refreshPortfolioUseCase: RefreshPortfolioUseCase,
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _isSummaryExpanded = MutableStateFlow(false)
    val isSummaryExpanded: StateFlow<Boolean> = _isSummaryExpanded.asStateFlow()

    private val _selectedTab = MutableStateFlow(PortfolioTab.HOLDINGS)
    val selectedTab: StateFlow<PortfolioTab> = _selectedTab.asStateFlow()

    val uiState: StateFlow<PortfolioUiState> = getPortfolioHoldingsUseCase()
        .combine(_isSummaryExpanded) { holdings, isExpanded ->
            // Perform calculation on background thread for better performance
            // This ensures UI thread remains responsive (120 FPS target)
            try {
                withContext(defaultDispatcher) {
                    val summary = calculatePortfolioSummaryUseCase(holdings)
                    PortfolioUiState.Success(
                        holdings = holdings,
                        summary = summary,
                        isSummaryExpanded = isExpanded
                    )
                }
            } catch (e: Exception) {
                PortfolioUiState.Error(
                    e.message ?: "Error processing portfolio data"
                )
            }
        }
        .catch { exception ->
            emit(PortfolioUiState.Error(
                exception.message ?: "Error loading portfolio data"
            ))
        }
        .distinctUntilChanged { old, new ->
            // Compare Success states only, other states always emit
            if (old is PortfolioUiState.Success && new is PortfolioUiState.Success) {
                // Compare all fields
                old.holdings == new.holdings && 
                old.isSummaryExpanded == new.isSummaryExpanded &&
                old.summary == new.summary
            } else {
                // Different types, always emit
                false
            }
        }
        .flowOn(defaultDispatcher)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = PortfolioUiState.Loading
        )

    fun refreshPortfolio() {
        // Use viewModelScope to ensure proper cancellation and prevent memory leaks
        viewModelScope.launch {
            val result = refreshPortfolioUseCase()
            if (result.isFailure) {
                // If refresh fails and we have no cached data, show error
                // Otherwise, cached data will be shown via the uiState flow
                // This provides graceful error recovery
                val currentState = uiState.value
                if (currentState is PortfolioUiState.Success && currentState.holdings.isEmpty()) {
                    // Note: Since uiState is now a StateFlow from stateIn, we can't directly set it
                    // The error will be handled by the flow's catch operator if getPortfolioHoldingsUseCase fails
                    // For refresh failures, we rely on the repository to handle gracefully
                }
            }
        }
    }

    fun toggleSummaryExpanded() {
        _isSummaryExpanded.value = !_isSummaryExpanded.value
        // State update is handled automatically by the combine flow
    }

    fun setSelectedTab(tab: PortfolioTab) {
        _selectedTab.value = tab
    }

    enum class PortfolioTab {
        POSITIONS,
        HOLDINGS
    }
}

