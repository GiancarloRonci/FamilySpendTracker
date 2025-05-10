package com.example.familyspendtracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val initialBudget: Double = 0.0,
    val addedBudget: Double = 0.0,
    val budgetStartDate: Long = System.currentTimeMillis(), // nuovo campo
    val currentBalance: Double = initialBudget // 🔥 Nuovo campo, inizializzato a initialBudget

)
