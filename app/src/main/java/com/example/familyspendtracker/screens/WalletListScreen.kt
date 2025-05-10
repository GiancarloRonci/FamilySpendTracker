package com.example.familyspendtracker.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.familyspendtracker.data.Wallet
import com.example.familyspendtracker.viewmodel.ExpenseViewModel

@Composable
fun WalletListScreen(
    viewModel: ExpenseViewModel,
    onEditClick: (walletId: Int) -> Unit
) {
    val wallets by viewModel.wallets.observeAsState(emptyList())

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Lista Wallet", style = MaterialTheme.typography.titleMedium)

        if (wallets.isEmpty()) {
            Text("Nessun wallet disponibile.")
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(wallets) { wallet ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        elevation = CardDefaults.cardElevation(4.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        // 🔥 Riga compatta con icone di modifica e cancellazione
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Informazioni del Wallet
                            Column {
                                Text("Nome: ${wallet.name}")
                                Text("Saldo Iniziale: €${"%.2f".format(wallet.initialBalance)}")
                                Text("Saldo Attuale: €${"%.2f".format(wallet.currentBalance)}")
                            }

                            // Icone
                            Row {
                                IconButton(onClick = {
                                    onEditClick(wallet.id)
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Modifica Wallet",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }

                                IconButton(onClick = {
                                    viewModel.deleteWallet(wallet)
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Elimina Wallet",
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
