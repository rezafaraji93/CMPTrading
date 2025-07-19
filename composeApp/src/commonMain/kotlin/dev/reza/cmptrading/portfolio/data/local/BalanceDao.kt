package dev.reza.cmptrading.portfolio.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface BalanceDao {

    @Query("SELECT cashBalance FROM UserBalanceEntity WHERE id = 1")
    suspend fun getCashBalance(): Double?

    @Upsert
    suspend fun insert(entity: UserBalanceEntity)

    @Query("UPDATE UserBalanceEntity SET cashBalance = :newBalance WHERE id = 1")
    suspend fun updateCashBalance(newBalance: Double)

}