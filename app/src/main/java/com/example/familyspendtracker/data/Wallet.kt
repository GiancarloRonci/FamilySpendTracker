package com.example.familyspendtracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "wallets")
data class Wallet(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,                  // es. "Contanti", "Conto corrente"
    val initialBalance: Double = 0.0,  // saldo iniziale
    val startTimestamp: Long = System.currentTimeMillis(), // nuova proprietà
    val currentBalance: Double = 0.0 // nuovo campo
)
