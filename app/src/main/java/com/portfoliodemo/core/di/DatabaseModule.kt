package com.portfoliodemo.core.di

import android.content.Context
import androidx.room.Room
import com.portfoliodemo.core.database.HoldingDao
import com.portfoliodemo.core.database.PortfolioDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun providePortfolioDatabase(
        @ApplicationContext context: Context
    ): PortfolioDatabase {
        return Room.databaseBuilder(
            context,
            PortfolioDatabase::class.java,
            PortfolioDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideHoldingDao(database: PortfolioDatabase): HoldingDao {
        return database.holdingDao()
    }
}

