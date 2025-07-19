package dev.reza.cmptrading.core.database.portfolio

import androidx.room.Database
import androidx.room.RoomDatabase
import dev.reza.cmptrading.portfolio.data.local.PortfolioCoinEntity
import dev.reza.cmptrading.portfolio.data.local.PortfolioDao

@Database(entities = [PortfolioCoinEntity::class], version = 1)
abstract class PortfolioDatabase: RoomDatabase() {
    abstract fun portfolioDao(): PortfolioDao

}