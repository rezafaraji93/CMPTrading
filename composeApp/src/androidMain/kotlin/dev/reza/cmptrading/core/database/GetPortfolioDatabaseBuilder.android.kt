package dev.reza.cmptrading.core.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import dev.reza.cmptrading.core.database.portfolio.PortfolioDatabase

fun getPortfolioDatabaseBuilder(
    applicationContext: Context
): RoomDatabase.Builder<PortfolioDatabase> {
    val dbFile = applicationContext.getDatabasePath("portfolio.db")
    return Room.databaseBuilder<PortfolioDatabase>(
        context = applicationContext,
        name = dbFile.name
    )

}