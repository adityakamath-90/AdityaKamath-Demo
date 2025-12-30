package com.portfoliodemo.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.portfoliodemo.core.common.Constants

@Database(
    entities = [HoldingEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class PortfolioDatabase : RoomDatabase() {
    abstract fun holdingDao(): HoldingDao

    companion object {
        const val DATABASE_NAME = Constants.DATABASE_NAME
    }
}

