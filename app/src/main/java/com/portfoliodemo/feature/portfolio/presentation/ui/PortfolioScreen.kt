package com.portfoliodemo.feature.portfolio.presentation.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.portfoliodemo.R
import com.portfoliodemo.feature.portfolio.presentation.PortfolioViewModel
import com.portfoliodemo.feature.portfolio.presentation.PortfolioUiState
import com.portfoliodemo.ui.theme.onHeaderColor
import com.portfoliodemo.ui.theme.portfolioHeaderColor

/**
 * Top App Bar for Portfolio screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PortfolioTopAppBar(
    modifier: Modifier = Modifier
) {
    val headerColor = portfolioHeaderColor()
    val onHeader = onHeaderColor()
    
    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = stringResource(R.string.account_icon_description),
                    tint = onHeader,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = stringResource(R.string.portfolio),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = onHeader,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = headerColor,
            titleContentColor = onHeader,
            navigationIconContentColor = onHeader,
            actionIconContentColor = onHeader
        ),
        modifier = modifier
    )
}

/**
 * Tab Row for Positions and Holdings tabs
 */
@Composable
private fun PortfolioTabs(
    selectedTab: PortfolioViewModel.PortfolioTab,
    onTabSelected: (PortfolioViewModel.PortfolioTab) -> Unit,
    modifier: Modifier = Modifier
) {
    TabRow(
        selectedTabIndex = selectedTab.ordinal,
        modifier = modifier
    ) {
        PortfolioViewModel.PortfolioTab.entries.forEach { tab ->
            Tab(
                selected = selectedTab == tab,
                onClick = {
                    // Positions is a no-op - do nothing
                    if (tab == PortfolioViewModel.PortfolioTab.HOLDINGS) {
                        onTabSelected(tab)
                    }
                },
                text = {
                    val tabText = when (tab) {
                        PortfolioViewModel.PortfolioTab.POSITIONS -> stringResource(R.string.positions)
                        PortfolioViewModel.PortfolioTab.HOLDINGS -> stringResource(R.string.holdings)
                    }
                    Text(
                        text = tabText,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = if (selectedTab == tab) {
                            FontWeight.Bold
                        } else {
                            FontWeight.Normal
                        }
                    )
                }
            )
        }
    }
}

/**
 * Content area showing holdings, loading, or error states
 */
@Composable
private fun PortfolioContent(
    uiState: PortfolioUiState,
    selectedTab: PortfolioViewModel.PortfolioTab,
    isSummaryExpanded: Boolean,
    onSummaryToggle: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
    ) {
        // Use uiState::class as targetState to prevent re-animation on data updates
        // Animation only triggers when switching between Loading, Success, and Error states
        AnimatedContent(
            targetState = uiState::class,
            transitionSpec = {
                fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(200))
            },
            label = "content_transition"
        ) { stateClass ->
            // Access current uiState to get latest data
            when (val state = uiState) {
                is PortfolioUiState.Loading -> {
                    LoadingIndicator()
                }
                is PortfolioUiState.Success -> {
                    // This block persists as long as the state is 'Success'
                    // Only show HoldingsList when Holdings tab is selected
                    if (selectedTab == PortfolioViewModel.PortfolioTab.HOLDINGS) {
                        HoldingsList(
                            holdings = state.holdings
                        )
                    } else {
                        // Positions tab - show empty or placeholder (no-op)
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            // Empty state for Positions (no-op)
                        }
                    }
                }
                is PortfolioUiState.Error -> {
                    ErrorScreen(
                        message = state.message,
                        onRetry = onRetry
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PortfolioScreen(
    viewModel: PortfolioViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val isSummaryExpanded by viewModel.isSummaryExpanded.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()

    Scaffold(
        topBar = {
            PortfolioTopAppBar()
        },
        bottomBar = {
            // Show PortfolioSummaryCard only when Holdings tab is selected and we have data
            if (selectedTab == PortfolioViewModel.PortfolioTab.HOLDINGS && 
                uiState is PortfolioUiState.Success) {
                PortfolioSummaryCard(
                    summary = (uiState as PortfolioUiState.Success).summary,
                    isExpanded = isSummaryExpanded,
                    onToggleExpanded = { viewModel.toggleSummaryExpanded() }
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            PortfolioTabs(
                selectedTab = selectedTab,
                onTabSelected = { viewModel.setSelectedTab(it) }
            )
            
            PortfolioContent(
                uiState = uiState,
                selectedTab = selectedTab,
                isSummaryExpanded = isSummaryExpanded,
                onSummaryToggle = { viewModel.toggleSummaryExpanded() },
                onRetry = { viewModel.refreshPortfolio() },
                modifier = Modifier.weight(1f)
            )
        }
    }
}


