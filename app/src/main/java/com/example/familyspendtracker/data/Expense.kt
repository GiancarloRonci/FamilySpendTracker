package com.example.familyspendtracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timestamp: Long,
    val amount: Double,
    val walletId: Int,             // 🔄 al posto di activeCategoryId
    val passiveCategoryId: Int,    // categoria della spesa
    val planned: Boolean = false,
    val description: String = ""
)

