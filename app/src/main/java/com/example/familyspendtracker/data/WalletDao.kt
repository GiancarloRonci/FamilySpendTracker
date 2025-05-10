package com.example.familyspendtracker.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface WalletDao {
    @Query("SELECT * FROM wallets WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): Wallet?

    @Insert
    suspend fun insert(wallet: Wallet)

    @Update
    suspend fun update(wallet: Wallet)

    @Delete
    suspend fun delete(wallet: Wallet)

    @Query("SELECT * FROM wallets")
    fun getAll(): Flow<List<Wallet>>

    @Query("UPDATE wallets SET currentBalance = :balance WHERE id = :walletId")
    suspend fun updateBalance(walletId: Int, balance: Double)


}
