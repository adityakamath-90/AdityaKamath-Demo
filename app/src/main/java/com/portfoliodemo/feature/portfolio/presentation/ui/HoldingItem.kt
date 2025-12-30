package com.portfoliodemo.feature.portfolio.presentation.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.material3.HorizontalDivider
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
import com.portfoliodemo.core.common.Constants.CURRENCY_SYMBOL
import com.portfoliodemo.core.common.formatAsCurrency
import com.portfoliodemo.feature.portfolio.domain.model.PortfolioItem
import com.portfoliodemo.ui.theme.PortfolioDemoTheme
import com.portfoliodemo.ui.theme.dividerColor
import com.portfoliodemo.ui.theme.lossColor
import com.portfoliodemo.ui.theme.profitColor
import com.portfoliodemo.ui.theme.textPrimaryColor
import com.portfoliodemo.ui.theme.textSecondaryColor
import java.math.BigDecimal

@Composable
fun HoldingItem(
    holding: PortfolioItem,
    modifier: Modifier = Modifier
) {
    val profitColor = profitColor()
    val lossColor = lossColor()
    val textPrimary = textPrimaryColor()
    val textSecondary = textSecondaryColor()
    val divider = dividerColor()
    
    Column(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Top row: Symbol (left) and LTP (right)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = holding.symbol,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = textPrimary,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = stringResource(R.string.ltp_label, "$CURRENCY_SYMBOL ${holding.ltp.formatAsCurrency().removePrefix("₹")}"),
                    style = MaterialTheme.typography.bodyMedium,
                    color = textSecondary
                )
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // Bottom row: NET QTY (left) and P&L (right)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.net_qty_label, holding.quantity.toString()),
                    style = MaterialTheme.typography.bodyMedium,
                    color = textSecondary,
                    modifier = Modifier.weight(1f)
                )
                val pnlFormatted = holding.pnl.formatAsCurrency()
                val pnlContentDescription = if (holding.pnl >= BigDecimal.ZERO) {
                    stringResource(R.string.profit_indicator, pnlFormatted)
                } else {
                    stringResource(R.string.loss_indicator, pnlFormatted)
                }
                Text(
                    text = stringResource(R.string.pnl_label, pnlFormatted),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (holding.pnl >= BigDecimal.ZERO) {
                        profitColor
                    } else {
                        lossColor
                    },
                    modifier = Modifier.semantics {
                        contentDescription = pnlContentDescription
                    }
                )
            }
        }
        HorizontalDivider(
            color = divider,
            thickness = 1.dp,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}

@Preview(
    name = "HoldingItem - Profit",
    showBackground = true
)
@Composable
private fun HoldingItemProfitPreview() {
    PortfolioDemoTheme {
        HoldingItem(
            holding = PortfolioItem(
                symbol = "ASHOKLEY",
                quantity = 3,
                ltp = BigDecimal("119.10"),
                avgPrice = BigDecimal("115.00"),
                close = BigDecimal("120.00"),
                pnl = BigDecimal("12.90"),
                pnlPercentage = BigDecimal("3.74")
            )
        )
    }
}

@Preview(
    name = "HoldingItem - Loss",
    showBackground = true
)
@Composable
private fun HoldingItemLossPreview() {
    PortfolioDemoTheme {
        HoldingItem(
            holding = PortfolioItem(
                symbol = "HDFC",
                quantity = 7,
                ltp = BigDecimal("2497.20"),
                avgPrice = BigDecimal("2710.00"),
                close = BigDecimal("2500.00"),
                pnl = BigDecimal("-1517.46"),
                pnlPercentage = BigDecimal("-7.85")
            )
        )
    }
}

