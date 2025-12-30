package com.portfoliodemo.core.network

import com.portfoliodemo.feature.portfolio.data.model.PortfolioResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class PortfolioApiService(
    private val httpClient: HttpClient,
    private val baseUrl: String
) {
    suspend fun getPortfolioHoldings(): PortfolioResponse {
        return httpClient.get(baseUrl).body()
    }
}
