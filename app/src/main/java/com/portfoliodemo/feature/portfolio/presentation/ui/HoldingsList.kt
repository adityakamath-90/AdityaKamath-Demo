package com.portfoliodemo.feature.portfolio.presentation.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.portfoliodemo.feature.portfolio.domain.model.PortfolioItem

@Composable
fun HoldingsList(
    holdings: List<PortfolioItem>,
    modifier: Modifier = Modifier
) {
    // Fixed padding to account for maximum expanded summary card height
    // This prevents list items from moving when summary expands/collapses
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 250.dp)
    ) {
        // Show empty state if no holdings
        if (holdings.isEmpty()) {
            item {
                EmptyState()
            }
        } else {
            // Holdings items
            items(
                items = holdings,
                key = { it.symbol }
            ) { holding ->
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(animationSpec = tween(300)) + slideInVertically(
                        animationSpec = tween(300),
                        initialOffsetY = { it / 2 }
                    ),
                    exit = fadeOut(animationSpec = tween(200)) + slideOutVertically(
                        animationSpec = tween(200),
                        targetOffsetY = { it / 2 }
                    )
                ) {
                    HoldingItem(holding = holding)
                }
            }
        }
    }
}

