package com.example.familyspendtracker.repository

import com.example.familyspendtracker.data.*
import kotlinx.coroutines.flow.Flow

class ExpenseRepository(
    private val categoryDao: CategoryDao,
    private val expenseDao: ExpenseDao,
    private val walletDao: WalletDao
) {
    // Categorie
    val categories: Flow<List<Category>> = categoryDao.getAll()
    suspend fun addCategory(category: Category) = categoryDao.insert(category)

    // Wallets
    val wallets: Flow<List<Wallet>> = walletDao.getAll()
    suspend fun addWallet(wallet: Wallet) = walletDao.insert(wallet)

    // Spese
    val expenses: Flow<List<Expense>> = expenseDao.getAll()
    suspend fun addExpense(expense: Expense) {
        expenseDao.insert(expense)
        updateCategoryBalance(expense.passiveCategoryId)

        // 🔥 Aggiorna il balance del wallet
        updateWalletBalance(expense.walletId)
    }

    // Calcoli per residui
    fun getTotalSpentFromWallet(walletId: Int): Flow<Double?> =
        expenseDao.getTotalSpentFromWallet(walletId)

    fun getTotalSpentForCategory(categoryId: Int): Flow<Double?> =
        expenseDao.getTotalSpentForCategory(categoryId)

    suspend fun deleteWallet(wallet: Wallet) {
        walletDao.delete(wallet)
    }

    suspend fun updateWallet(wallet: Wallet) {
        walletDao.update(wallet)
        // 🔥 Ricalcolo immediato del currentBalance dopo l'update
        updateWalletBalance(wallet.id)
    }

    suspend fun deleteExpense(expense: Expense) {
        expenseDao.delete(expense)
        updateWalletBalance(expense.walletId)
        updateCategoryBalance(expense.passiveCategoryId)
    }

    suspend fun updateExpense(expense: Expense) {
        expenseDao.update(expense)
        updateWalletBalance(expense.walletId)
        updateCategoryBalance(expense.passiveCategoryId)
    }

    // 🔄 Funzione di ricalcolo del balance di un Wallet
    suspend fun calculateCurrentBalanceForWallet(walletId: Int): Double {
        val wallet = walletDao.getById(walletId) ?: return 0.0
        val expenses = expenseDao.getExpensesForWalletSince(walletId, wallet.startTimestamp)
        val totalSpent = expenses.sumOf { it.amount }
        return wallet.initialBalance - totalSpent
    }

    // 🔄 Aggiorna il currentBalance di un Wallet
    suspend fun updateWalletBalance(walletId: Int) {
        val newBalance = calculateCurrentBalanceForWallet(walletId)
        walletDao.updateBalance(walletId, newBalance)
    }

    suspend fun deleteCategory(category: Category) {
        categoryDao.delete(category)
    }

    // 🔄 Funzione che calcola il currentBalance di una categoria
    private suspend fun updateCategoryBalance(categoryId: Int) {
        val category = categoryDao.getById(categoryId)
        if (category != null) {
            val totalExpenses = expenseDao.getTotalAmountByCategory(categoryId, category.budgetStartDate)
            val updatedBalance = category.initialBudget - totalExpenses
            val updatedCategory = category.copy(currentBalance = updatedBalance)
            categoryDao.update(updatedCategory)
        }
    }

    suspend fun updateCategory(category: Category) {
        categoryDao.update(category)
        updateCategoryBalance(category.id)
    }
}
