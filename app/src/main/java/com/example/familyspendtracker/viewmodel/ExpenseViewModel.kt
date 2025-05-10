package com.example.familyspendtracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.familyspendtracker.data.AppDatabase
import com.example.familyspendtracker.data.Category
import com.example.familyspendtracker.data.Expense
import com.example.familyspendtracker.data.Wallet
import com.example.familyspendtracker.repository.ExpenseRepository
import kotlinx.coroutines.launch

class ExpenseViewModel(application: Application) : AndroidViewModel(application) {

    private val db = Room.databaseBuilder(
        application,
        AppDatabase::class.java,
        "spend-tracker-db"
    )
        .fallbackToDestructiveMigration()
        .build()

    private val repository = ExpenseRepository(
        db.categoryDao(),
        db.expenseDao(),
        db.walletDao()
    )

    val categories = repository.categories.asLiveData()
    val expenses = repository.expenses.asLiveData()
    val wallets = repository.wallets.asLiveData()

    // -- CATEGORIE --

    fun addCategory(category: Category) = viewModelScope.launch {
        repository.addCategory(category)
    }

    // -- WALLET --

    fun addWallet(wallet: Wallet) = viewModelScope.launch {
        repository.addWallet(wallet)
        repository.updateWalletBalance(wallet.id)
    }

    fun updateWallet(wallet: Wallet) = viewModelScope.launch {
        repository.updateWallet(wallet)
        repository.updateWalletBalance(wallet.id)
    }

    fun deleteWallet(wallet: Wallet) = viewModelScope.launch {
        repository.deleteWallet(wallet)
    }

    fun refreshWalletBalance(walletId: Int) = viewModelScope.launch {
        repository.updateWalletBalance(walletId)
    }

    // -- SPESE --

    fun addExpense(expense: Expense) = viewModelScope.launch {
        repository.addExpense(expense)
        repository.updateWalletBalance(expense.walletId)
    }

    fun deleteExpense(expense: Expense) = viewModelScope.launch {
        repository.deleteExpense(expense)
        repository.updateWalletBalance(expense.walletId)
    }

    fun updateExpense(expense: Expense) = viewModelScope.launch {
        repository.updateExpense(expense)
        repository.updateWalletBalance(expense.walletId)
    }

    fun deleteCategory(category: Category) = viewModelScope.launch {
        repository.deleteCategory(category)
    }

    fun updateCategory(category: Category) = viewModelScope.launch {
        repository.updateCategory(category)
    }

    fun refreshCategories() = viewModelScope.launch {
        repository.categories.collect {
            // Questo forza il ricaricamento della lista
        }
    }



}
