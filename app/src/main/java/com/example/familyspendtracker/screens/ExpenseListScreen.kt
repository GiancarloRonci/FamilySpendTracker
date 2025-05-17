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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.familyspendtracker.viewmodel.ExpenseViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseListScreen(viewModel: ExpenseViewModel, navController: NavController) {
    val expenses by viewModel.expenses.observeAsState(emptyList())
    val categories by viewModel.categories.observeAsState(emptyList())
    val wallets by viewModel.wallets.observeAsState(emptyList())

    // Filtro temporale
    val filterOptions = listOf("Ultimi 5 giorni", "Ultimi 15 giorni", "Ultimo mese", "Ultimo anno")
    var expandedPeriod by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf(filterOptions[0]) }
    var selectedDays by remember { mutableStateOf(5) }

    // Filtro categoria
    val categoryNames = listOf("Tutte le categorie") + categories.map { it.name }
    var expandedCategory by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf("Tutte le categorie") }

    val now = System.currentTimeMillis()
    val filteredExpenses = expenses
        .filter { it.timestamp >= now - selectedDays * 24 * 60 * 60 * 1000L }
        .filter {
            selectedCategory == "Tutte le categorie" ||
                    categories.find { cat -> cat.id == it.passiveCategoryId }?.name == selectedCategory
        }
        .sortedByDescending { it.timestamp }

    val totalWalletBalance = wallets.sumOf { it.currentBalance }
    val totalCategoryBalance = categories.sumOf { it.currentBalance }
    val balanceComplessivo = totalWalletBalance - totalCategoryBalance

    val formattedBalance = "%.2f".format(kotlin.math.abs(balanceComplessivo))
    val sign = if (balanceComplessivo >= 0) "+" else "-"
    val balanceText = "$sign$formattedBalance€"

    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) }

    Column(modifier = Modifier.padding(16.dp)) {
        // 🔢 Balance Complessivo
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
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
                    text = "Balance Complessivo:",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Start
                )
                Text(
                    text = balanceText,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.End
                )
            }
        }

        // 🔽 Filtri verticali con sfondo uniforme
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(8.dp)
        ) {
            // Filtro temporale
            ExposedDropdownMenuBox(
                expanded = expandedPeriod,
                onExpandedChange = { expandedPeriod = !expandedPeriod },
                modifier = Modifier.fillMaxWidth()
            ) {
                TextField(
                    value = selectedFilter,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Filtra per periodo", fontSize = 12.sp) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandedPeriod) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                        .height(48.dp),
                    textStyle = TextStyle(fontSize = 14.sp),
                    colors = TextFieldDefaults.colors()
                )
                ExposedDropdownMenu(
                    expanded = expandedPeriod,
                    onDismissRequest = { expandedPeriod = false }
                ) {
                    filterOptions.forEachIndexed { index, option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                selectedFilter = option
                                selectedDays = when (index) {
                                    0 -> 5
                                    1 -> 15
                                    2 -> 30
                                    3 -> 365
                                    else -> 5
                                }
                                expandedPeriod = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Filtro categoria
            ExposedDropdownMenuBox(
                expanded = expandedCategory,
                onExpandedChange = { expandedCategory = !expandedCategory },
                modifier = Modifier.fillMaxWidth()
            ) {
                TextField(
                    value = selectedCategory,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Filtra per categoria", fontSize = 12.sp) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandedCategory) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                        .height(48.dp),
                    textStyle = TextStyle(fontSize = 14.sp),
                    colors = TextFieldDefaults.colors()
                )
                ExposedDropdownMenu(
                    expanded = expandedCategory,
                    onDismissRequest = { expandedCategory = false }
                ) {
                    categoryNames.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category) },
                            onClick = {
                                selectedCategory = category
                                expandedCategory = false
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 🔠 Intestazione
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

        if (filteredExpenses.isEmpty()) {
            Text("Nessuna spesa trovata.")
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredExpenses) { expense ->
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

                            if (expense.description.isNotEmpty()) {
                                Text("Descrizione: ${expense.description}")
                            }
                            if (expense.planned) {
                                Text("\uD83D\uDCA1 Spesa pianificata", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
            }
        }
    }
}
