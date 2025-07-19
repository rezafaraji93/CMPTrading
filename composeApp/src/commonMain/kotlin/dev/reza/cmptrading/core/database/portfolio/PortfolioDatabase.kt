package dev.reza.cmptrading.core.database.portfolio

import androidx.room.Database
import androidx.room.RoomDatabase
import dev.reza.cmptrading.portfolio.data.local.BalanceDao
import dev.reza.cmptrading.portfolio.data.local.PortfolioCoinEntity
import dev.reza.cmptrading.portfolio.data.local.PortfolioDao
import dev.reza.cmptrading.portfolio.data.local.UserBalanceEntity

@Database(entities = [PortfolioCoinEntity::class, UserBalanceEntity::class], version = 2)
abstract class PortfolioDatabase: RoomDatabase() {
    abstract fun portfolioDao(): PortfolioDao
    abstract fun userBalanceDao(): BalanceDao

}