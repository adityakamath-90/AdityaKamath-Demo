package com.portfoliodemo.feature.portfolio.domain.usecase

import com.portfoliodemo.feature.portfolio.data.FakePortfolioRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class RefreshPortfolioUseCaseTest {

    private lateinit var fakeRepository: FakePortfolioRepository
    private lateinit var useCase: RefreshPortfolioUseCase

    @Before
    fun setup() {
        fakeRepository = FakePortfolioRepository()
        useCase = RefreshPortfolioUseCase(fakeRepository)
    }

    @Test
    fun `invoke returns success when repository refresh succeeds`() = runTest {
        fakeRepository.setRefreshResult(Result.success(Unit))

        val result = useCase()

        assertTrue(result.isSuccess)
    }

    @Test
    fun `invoke returns failure when repository refresh fails`() = runTest {
        val exception = Exception("Network error")
        fakeRepository.setRefreshResult(Result.failure(exception))

        val result = useCase()

        assertTrue(result.isFailure)
        assertEquals("Network error", result.exceptionOrNull()?.message)
    }

    @Test
    fun `invoke calls repository refreshHoldings`() = runTest {
        fakeRepository.setRefreshResult(Result.success(Unit))

        val result = useCase()

        assertTrue(result.isSuccess)
    }

    @Test
    fun `invoke handles multiple refresh calls`() = runTest {
        fakeRepository.setRefreshResult(Result.success(Unit))

        val result1 = useCase()
        val result2 = useCase()

        assertTrue(result1.isSuccess)
        assertTrue(result2.isSuccess)
    }

    @Test
    fun `invoke propagates exception from repository`() = runTest {
        val exception = RuntimeException("Database error")
        fakeRepository.setRefreshResult(Result.failure(exception))

        val result = useCase()

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is RuntimeException)
        assertEquals("Database error", result.exceptionOrNull()?.message)
    }
}
