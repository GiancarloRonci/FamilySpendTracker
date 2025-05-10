package com.example.familyspendtracker.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [Category::class, Expense::class, Wallet::class], // ➕ aggiunto Wallet
    version = 10, // 🔄 incrementa la versione se prima era 3
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun walletDao(): WalletDao // ➕ aggiunto il DAO
}

