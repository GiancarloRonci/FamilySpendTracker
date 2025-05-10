package com.example.familyspendtracker.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {
    @Query("SELECT * FROM expenses")
    fun getAll(): Flow<List<Expense>>

    @Insert
    suspend fun insert(expense: Expense)

    @Update
    suspend fun update(expense: Expense)

    @Delete
    suspend fun delete(expense: Expense)

    // Totale spese per wallet (contenitore)
    @Query("SELECT SUM(amount) FROM expenses WHERE walletId = :walletId")
    fun getTotalSpentFromWallet(walletId: Int): Flow<Double?>

    // Totale spese per categoria passiva
    @Query("SELECT SUM(amount) FROM expenses WHERE passiveCategoryId = :categoryId")
    fun getTotalSpentForCategory(categoryId: Int): Flow<Double?>


    @Query("SELECT * FROM expenses WHERE walletId = :walletId AND timestamp >= :startTimestamp")
    suspend fun getExpensesForWalletSince(walletId: Int, startTimestamp: Long): List<Expense>

    @Query("SELECT IFNULL(SUM(amount), 0) FROM expenses WHERE passiveCategoryId = :categoryId AND timestamp >= :startDate")
    suspend fun getTotalAmountByCategory(categoryId: Int, startDate: Long): Double
    
}

