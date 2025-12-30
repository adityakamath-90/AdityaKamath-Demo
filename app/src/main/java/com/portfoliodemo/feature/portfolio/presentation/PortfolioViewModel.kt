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
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
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

    private val _refreshError = MutableStateFlow<String?>(null)
    val refreshError: StateFlow<String?> = _refreshError.asStateFlow()

    private var hasInitialRefresh = false

    val uiState: StateFlow<PortfolioUiState> = getPortfolioHoldingsUseCase()
        .onStart {
            if (!hasInitialRefresh) {
                hasInitialRefresh = true
                viewModelScope.launch {
                    refreshPortfolio()
                }
            }
        }
        .combine(_isSummaryExpanded) { holdings, isExpanded ->
            // Perform calculation on background thread for better performance
            // flowOn(defaultDispatcher) ensures this runs on background thread (120 FPS target)
            try {
                val summary = calculatePortfolioSummaryUseCase(holdings)
                PortfolioUiState.Success(
                    holdings = holdings,
                    summary = summary,
                    isSummaryExpanded = isExpanded
                )
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
        viewModelScope.launch {
            _refreshError.value = null
            val result = refreshPortfolioUseCase()
            if (result.isFailure) {
                val errorMessage = result.exceptionOrNull()?.message 
                    ?: "Failed to refresh portfolio data"
                _refreshError.value = errorMessage
            }
        }
    }

    fun toggleSummaryExpanded() {
        _isSummaryExpanded.value = !_isSummaryExpanded.value
    }

    fun setSelectedTab(tab: PortfolioTab) {
        _selectedTab.value = tab
    }

    enum class PortfolioTab {
        POSITIONS,
        HOLDINGS
    }
}

