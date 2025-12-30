package com.portfoliodemo.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Portfolio-specific colors that adapt to dark/light mode
object PortfolioColors {
    // Header color - dark blue in light mode, primary container in dark mode
    val LightHeader = Color(0xFF1E3A5F)
    
    // Profit/Loss colors - these are semantic and should remain consistent
    val ProfitGreen = Color(0xFF4CAF50)
    val LossRed = Color(0xFFF44336)
}

/**
 * Portfolio header color that adapts to theme
 */
@Composable
fun portfolioHeaderColor(): Color {
    return if (!isSystemInDarkTheme()) {
        PortfolioColors.LightHeader
    } else {
        MaterialTheme.colorScheme.primaryContainer
    }
}

/**
 * Color for profit indicators
 */
@Composable
fun profitColor(): Color = PortfolioColors.ProfitGreen

/**
 * Color for loss indicators
 */
@Composable
fun lossColor(): Color = PortfolioColors.LossRed

/**
 * Background color for summary card
 */
@Composable
fun summaryBackgroundColor(): Color {
    return if (!isSystemInDarkTheme()) {
        Color(0xFFF5F5F5)
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }
}

/**
 * Secondary text color
 */
@Composable
fun textSecondaryColor(): Color = MaterialTheme.colorScheme.onSurfaceVariant

/**
 * Divider color
 */
@Composable
fun dividerColor(): Color = MaterialTheme.colorScheme.outlineVariant

/**
 * Primary text color
 */
@Composable
fun textPrimaryColor(): Color = MaterialTheme.colorScheme.onSurface

/**
 * Color for content on header/primary surfaces
 */
@Composable
fun onHeaderColor(): Color {
    return if (!isSystemInDarkTheme()) {
        Color.White
    } else {
        MaterialTheme.colorScheme.onPrimaryContainer
    }
}

