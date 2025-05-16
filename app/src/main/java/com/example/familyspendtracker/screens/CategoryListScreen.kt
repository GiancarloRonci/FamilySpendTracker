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
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryListScreen(viewModel: ExpenseViewModel, navController: NavController) {
    val categories by viewModel.categories.observeAsState(emptyList())
    val wallets by viewModel.wallets.observeAsState(emptyList())
    val currencyFormatter = remember { NumberFormat.getCurrencyInstance(Locale.getDefault()) }

    val totalWalletBalance = wallets.sumOf { it.currentBalance }
    val totalCategoryBalance = categories.sumOf { it.currentBalance }
    val balanceComplessivo = totalWalletBalance - totalCategoryBalance

    val formattedBalance = "%.2f".format(kotlin.math.abs(balanceComplessivo))
    val sign = if (balanceComplessivo >= 0) "+" else "-"
    val balanceText = "$sign$formattedBalance€"

    Column(modifier = Modifier.fillMaxSize()) {

        // 🔢 Balance sopra la barra del titolo
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
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

        // 🔼 Scaffold con TopAppBar e contenuto
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Lista Categorie") },
                    actions = {
                        IconButton(onClick = {
                            navController.navigate("add_category")
                        }) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Aggiungi Categoria",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                )
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .padding(top = paddingValues.calculateTopPadding()),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(categories) { category ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        elevation = CardDefaults.cardElevation(4.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Nome: ${category.name}")
                                Text("Budget Iniziale: ${currencyFormatter.format(category.initialBudget)}")
                                Text("Saldo Attuale: ${currencyFormatter.format(category.currentBalance)}")
                                Text("Data inizio: ${java.text.SimpleDateFormat("dd/MM/yyyy").format(Date(category.budgetStartDate))}")
                            }

                            Row {
                                IconButton(onClick = {
                                    navController.navigate("edit_category/${category.id}")
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Modifica Categoria",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }

                                IconButton(onClick = {
                                    viewModel.deleteCategory(category)
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Elimina Categoria",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
