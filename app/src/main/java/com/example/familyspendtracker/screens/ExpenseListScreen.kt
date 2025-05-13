package com.example.familyspendtracker.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.familyspendtracker.viewmodel.ExpenseViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ExpenseListScreen(viewModel: ExpenseViewModel, navController: NavController) {
    val expenses by viewModel.expenses.observeAsState(emptyList())
    val categories by viewModel.categories.observeAsState(emptyList())
    val wallets by viewModel.wallets.observeAsState(emptyList())

    // 🔥 Calcolo del Balance Complessivo
    val totalWalletBalance = wallets.sumOf { it.currentBalance }
    val totalCategoryBalance = categories.sumOf { it.currentBalance }
    val balanceComplessivo = totalWalletBalance - totalCategoryBalance

    // 🔄 Ordinamento delle spese in ordine decrescente
    val sortedExpenses = expenses.sortedByDescending { it.timestamp }

    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) }

    Column(modifier = Modifier.padding(16.dp)) {
        // 🔥 Header con titolo e icona
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Spese registrate", style = MaterialTheme.typography.titleMedium)
            IconButton(onClick = {
                navController.navigate("add_expense")
            }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Aggiungi Spesa",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        // 🔥 Sezione per il Balance Complessivo - Design Evidenziato
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            elevation = CardDefaults.cardElevation(6.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .background(MaterialTheme.colorScheme.primary),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Balance Complessivo",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Start
                )
                Text(
                    text = "€${"%.2f".format(balanceComplessivo)}",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.End
                )
            }
        }

        if (sortedExpenses.isEmpty()) {
            Text("Nessuna spesa trovata.")
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(sortedExpenses) { expense ->
                    val categoryName = categories.find { it.id == expense.passiveCategoryId }?.name ?: "Categoria?"
                    val walletName = wallets.find { it.id == expense.walletId }?.name ?: "Wallet?"
                    val formattedDate = dateFormat.format(Date(expense.timestamp))

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            // 🔥 Riga principale compatta
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Data: $formattedDate")
                                    Text("Importo: €${"%.2f".format(expense.amount)}")
                                    Text("Categoria: $categoryName")
                                    Text("Wallet: $walletName")
                                }

                                Row {
                                    IconButton(onClick = {
                                        navController.navigate("edit_expense/${expense.id}")
                                    }) {
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = "Modifica spesa",
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }

                                    IconButton(onClick = {
                                        viewModel.deleteExpense(expense)
                                    }) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Elimina spesa",
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }
                            }

                            // 🔥 Riga descrizione compatta
                            if (expense.description.isNotEmpty()) {
                                Text("Descrizione: ${expense.description}")
                            }
                            if (expense.planned) {
                                Text("💡 Spesa pianificata", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
            }
        }
    }
}
