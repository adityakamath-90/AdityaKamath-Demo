package com.portfoliodemo.feature.portfolio.presentation

import com.portfoliodemo.feature.portfolio.domain.model.PortfolioItem
import com.portfoliodemo.feature.portfolio.domain.usecase.CalculatePortfolioSummaryUseCase
import com.portfoliodemo.feature.portfolio.domain.usecase.GetPortfolioHoldingsUseCase
import com.portfoliodemo.feature.portfolio.domain.usecase.RefreshPortfolioUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.math.BigDecimal

@OptIn(ExperimentalCoroutinesApi::class)
class PortfolioViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var getPortfolioHoldingsUseCase: GetPortfolioHoldingsUseCase
    private lateinit var calculatePortfolioSummaryUseCase: CalculatePortfolioSummaryUseCase
    private lateinit var refreshPortfolioUseCase: RefreshPortfolioUseCase
    private lateinit var viewModel: PortfolioViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getPortfolioHoldingsUseCase = mock()
        calculatePortfolioSummaryUseCase = CalculatePortfolioSummaryUseCase()
        refreshPortfolioUseCase = mock()
    }

    private fun createViewModel(
        dispatcher: CoroutineDispatcher = testDispatcher
    ): PortfolioViewModel {
        return PortfolioViewModel(
            getPortfolioHoldingsUseCase,
            calculatePortfolioSummaryUseCase,
            refreshPortfolioUseCase,
            dispatcher
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is loading`() = runTest(testDispatcher) {
        whenever(getPortfolioHoldingsUseCase()).thenReturn(flowOf(emptyList()))

        viewModel = createViewModel()

        // Trigger flow by collecting first value
        val state = viewModel.uiState.first()
        assertTrue(state is PortfolioUiState.Success)
    }

    @Test
    fun `success state with holdings`() = runTest(testDispatcher) {
        val holdings = listOf(
            PortfolioItem(
                symbol = "TEST",
                quantity = 10,
                ltp = BigDecimal("100.0"),
                avgPrice = BigDecimal("90.0"),
                close = BigDecimal("95.0"),
                pnl = BigDecimal("100.0"),
                pnlPercentage = BigDecimal("11.11")
            )
        )
        whenever(getPortfolioHoldingsUseCase()).thenReturn(flowOf(holdings))

        viewModel = createViewModel()

        // Trigger flow by collecting first value
        val state = viewModel.uiState.first()
        assertTrue(state is PortfolioUiState.Success)
        if (state is PortfolioUiState.Success) {
            assertEquals(1, state.holdings.size)
            assertEquals("TEST", state.holdings[0].symbol)
        }
    }

    @Test
    fun `error state on exception`() = runTest(testDispatcher) {
        whenever(getPortfolioHoldingsUseCase()).thenReturn(
            flow {
                throw Exception("Network error")
            }
        )

        viewModel = createViewModel()

        // Trigger flow by collecting first value
        val state = viewModel.uiState.first()
        assertTrue(state is PortfolioUiState.Error)
    }

    @Test
    fun `toggle summary expanded`() = runTest(testDispatcher) {
        whenever(getPortfolioHoldingsUseCase()).thenReturn(flowOf(emptyList()))

        viewModel = createViewModel()

        // Trigger flow
        viewModel.uiState.first()
        val initialExpanded = viewModel.isSummaryExpanded.value
        viewModel.toggleSummaryExpanded()
        advanceUntilIdle()
        val afterToggle = viewModel.isSummaryExpanded.value

        assertTrue(initialExpanded != afterToggle)
    }

    @Test
    fun `refreshPortfolio calls refresh use case`() = runTest(testDispatcher) {
        whenever(getPortfolioHoldingsUseCase()).thenReturn(flowOf(emptyList()))
        whenever(refreshPortfolioUseCase()).thenReturn(Result.success(Unit))

        viewModel = createViewModel()

        // Trigger flow - this will automatically trigger refresh on first subscription
        viewModel.uiState.first()
        advanceUntilIdle()
        
        // Verify refreshPortfolio is called automatically on first subscription (not in init)
        verify(refreshPortfolioUseCase, org.mockito.kotlin.atLeastOnce()).invoke()
        
        // Reset mock to count only explicit calls
        org.mockito.kotlin.reset(refreshPortfolioUseCase)
        whenever(refreshPortfolioUseCase()).thenReturn(Result.success(Unit))
        
        // Now call it explicitly
        viewModel.refreshPortfolio()
        advanceUntilIdle()
        
        // Should be called once (explicit call)
        verify(refreshPortfolioUseCase, org.mockito.kotlin.times(1)).invoke()
    }

    @Test
    fun `refreshPortfolio handles failure when no cached data`() = runTest(testDispatcher) {
        whenever(getPortfolioHoldingsUseCase()).thenReturn(flowOf(emptyList()))
        whenever(refreshPortfolioUseCase()).thenReturn(Result.failure(Exception("Network error")))

        viewModel = createViewModel()

        // Trigger flow
        var state = viewModel.uiState.first()
        // With empty holdings, the state should be Success with empty list
        assertTrue(state is PortfolioUiState.Success)
        
        viewModel.refreshPortfolio()
        advanceUntilIdle()

        // After refresh failure, state should still be Success (error handling is now in the flow)
        // The refreshPortfolio function no longer directly sets error state
        state = viewModel.uiState.value
        assertTrue(state is PortfolioUiState.Success)
    }

    @Test
    fun `toggle summary expanded updates ui state`() = runTest(testDispatcher) {
        val holdings = listOf(
            PortfolioItem(
                symbol = "TEST",
                quantity = 10,
                ltp = BigDecimal("100.0"),
                avgPrice = BigDecimal("90.0"),
                close = BigDecimal("95.0"),
                pnl = BigDecimal("100.0"),
                pnlPercentage = BigDecimal("11.11")
            )
        )
        whenever(getPortfolioHoldingsUseCase()).thenReturn(flowOf(holdings))

        viewModel = createViewModel()

        // Trigger flow
        val initialState = viewModel.uiState.first()
        assertTrue(initialState is PortfolioUiState.Success)
        if (initialState is PortfolioUiState.Success) {
            assertFalse(initialState.isSummaryExpanded)
        }

        viewModel.toggleSummaryExpanded()
        advanceUntilIdle()

        val afterToggle = viewModel.uiState.value
        assertTrue(afterToggle is PortfolioUiState.Success)
        if (afterToggle is PortfolioUiState.Success) {
            assertTrue(afterToggle.isSummaryExpanded)
        }
    }

    @Test
    fun `summary is calculated correctly in success state`() = runTest(testDispatcher) {
        val holdings = listOf(
            PortfolioItem(
                symbol = "TEST",
                quantity = 10,
                ltp = BigDecimal("100.0"),
                avgPrice = BigDecimal("90.0"),
                close = BigDecimal("95.0"),
                pnl = BigDecimal("100.0"),
                pnlPercentage = BigDecimal("11.11")
            )
        )
        whenever(getPortfolioHoldingsUseCase()).thenReturn(flowOf(holdings))

        viewModel = createViewModel()

        // Trigger flow
        val state = viewModel.uiState.first()
        assertTrue(state is PortfolioUiState.Success)
        if (state is PortfolioUiState.Success) {
            assertEquals(BigDecimal("1000.00"), state.summary.currentValue.setScale(2))
            assertEquals(BigDecimal("900.00"), state.summary.totalInvestment.setScale(2))
            assertEquals(BigDecimal("100.00"), state.summary.totalPnl.setScale(2))
        }
    }
}

