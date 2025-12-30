package com.portfoliodemo.feature.portfolio.presentation.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.tooling.preview.Preview
import com.portfoliodemo.R
import com.portfoliodemo.core.common.formatAsCurrency
import com.portfoliodemo.core.common.formatAsPercentage
import com.portfoliodemo.feature.portfolio.domain.model.PortfolioSummary
import com.portfoliodemo.ui.theme.PortfolioDemoTheme
import com.portfoliodemo.ui.theme.dividerColor
import com.portfoliodemo.ui.theme.lossColor
import com.portfoliodemo.ui.theme.profitColor
import com.portfoliodemo.ui.theme.summaryBackgroundColor
import com.portfoliodemo.ui.theme.textPrimaryColor
import com.portfoliodemo.ui.theme.textSecondaryColor
import java.math.BigDecimal

@Composable
fun PortfolioSummaryCard(
    summary: PortfolioSummary,
    isExpanded: Boolean,
    onToggleExpanded: () -> Unit,
    modifier: Modifier = Modifier
) {
    val background = summaryBackgroundColor()
    val profitColor = profitColor()
    val lossColor = lossColor()
    val textPrimary = textPrimaryColor()
    val textSecondary = textSecondaryColor()
    val divider = dividerColor()
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(background)
    ) {
        // Collapsed view - Always visible (clickable header)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onToggleExpanded)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.profit_loss),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = textPrimary,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "${summary.totalPnl.formatAsCurrency()} (${summary.totalPnlPercentage.formatAsPercentage()})",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = if (summary.totalPnl >= BigDecimal.ZERO) {
                    profitColor
                } else {
                    lossColor
                },
                modifier = Modifier.padding(end = 8.dp)
            )
            Icon(
                imageVector = if (isExpanded) Icons.Default.ExpandMore else Icons.Default.ExpandLess,
                contentDescription = if (isExpanded) stringResource(R.string.collapse_summary) else stringResource(R.string.expand_summary),
                tint = textSecondary,
                modifier = Modifier.size(24.dp)
            )
        }

        // Expanded view - Animated
        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                SummaryRow(
                    label = stringResource(R.string.current_value_star),
                    value = summary.currentValue.formatAsCurrency()
                )
                Spacer(modifier = Modifier.height(12.dp))
                SummaryRow(
                    label = stringResource(R.string.total_investment_star),
                    value = summary.totalInvestment.formatAsCurrency()
                )
                Spacer(modifier = Modifier.height(12.dp))
                SummaryRow(
                    label = stringResource(R.string.todays_pnl_star),
                    value = summary.todayPnl.formatAsCurrency(),
                    isProfit = summary.todayPnl >= BigDecimal.ZERO
                )
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(
                    color = divider,
                    thickness = 1.dp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                SummaryRow(
                    label = stringResource(R.string.profit_loss),
                    value = "${summary.totalPnl.formatAsCurrency()} (${summary.totalPnlPercentage.formatAsPercentage()})",
                    isProfit = summary.totalPnl >= BigDecimal.ZERO
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun SummaryRow(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    isProfit: Boolean? = null,
) {
    val profitColor = profitColor()
    val lossColor = lossColor()
    val textPrimary = textPrimaryColor()
    val textSecondary = textSecondaryColor()
    
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = textSecondary,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = if (isProfit != null) {
                if (isProfit) {
                    profitColor
                } else {
                    lossColor
                }
            } else {
                textPrimary
            }
        )
    }
}

@Preview(
    name = "SummaryCard - Collapsed (Loss)",
    showBackground = true
)
@Composable
private fun PortfolioSummaryCardCollapsedLossPreview() {
    PortfolioDemoTheme {
        PortfolioSummaryCard(
            summary = PortfolioSummary(
                currentValue = BigDecimal("27893.65"),
                totalInvestment = BigDecimal("28590.71"),
                totalPnl = BigDecimal("-697.06"),
                totalPnlPercentage = BigDecimal("-2.44"),
                todayPnl = BigDecimal("-235.65")
            ),
            isExpanded = false,
            onToggleExpanded = {}
        )
    }
}

@Preview(
    name = "SummaryCard - Collapsed (Profit)",
    showBackground = true
)
@Composable
private fun PortfolioSummaryCardCollapsedProfitPreview() {
    PortfolioDemoTheme {
        PortfolioSummaryCard(
            summary = PortfolioSummary(
                currentValue = BigDecimal("35000.00"),
                totalInvestment = BigDecimal("30000.00"),
                totalPnl = BigDecimal("5000.00"),
                totalPnlPercentage = BigDecimal("16.67"),
                todayPnl = BigDecimal("500.00")
            ),
            isExpanded = false,
            onToggleExpanded = {}
        )
    }
}

@Preview(
    name = "SummaryCard - Expanded (Loss)",
    showBackground = true
)
@Composable
private fun PortfolioSummaryCardExpandedLossPreview() {
    PortfolioDemoTheme {
        PortfolioSummaryCard(
            summary = PortfolioSummary(
                currentValue = BigDecimal("27893.65"),
                totalInvestment = BigDecimal("28590.71"),
                totalPnl = BigDecimal("-697.06"),
                totalPnlPercentage = BigDecimal("-2.44"),
                todayPnl = BigDecimal("-235.65")
            ),
            isExpanded = true,
            onToggleExpanded = {}
        )
    }
}

@Preview(
    name = "SummaryCard - Expanded (Profit)",
    showBackground = true
)
@Composable
private fun PortfolioSummaryCardExpandedProfitPreview() {
    PortfolioDemoTheme {
        PortfolioSummaryCard(
            summary = PortfolioSummary(
                currentValue = BigDecimal("35000.00"),
                totalInvestment = BigDecimal("30000.00"),
                totalPnl = BigDecimal("5000.00"),
                totalPnlPercentage = BigDecimal("16.67"),
                todayPnl = BigDecimal("500.00")
            ),
            isExpanded = true,
            onToggleExpanded = {}
        )
    }
}

