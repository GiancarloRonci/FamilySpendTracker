package com.example.familyspendtracker.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
            // LazyColumn per abilitare lo scroll
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(wallets) { wallet ->
                    WalletItem(
                        wallet = wallet,
                        onDelete = { viewModel.deleteWallet(wallet) },
                        onEdit = { onEditClick(wallet.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun WalletItem(
    wallet: Wallet,
    onDelete: () -> Unit,
    onEdit: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text("Nome: ${wallet.name}")
            Text("Saldo iniziale: €${"%.2f".format(wallet.initialBalance)}")
            Text("Saldo attuale: €${"%.2f".format(wallet.currentBalance)}")

            Row(modifier = Modifier.padding(top = 8.dp)) {
                Button(
                    onClick = onEdit,
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .weight(1f) // Distribuisce meglio lo spazio
                ) {
                    Text("Modifica")
                }

                Button(
                    onClick = onDelete,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    ),
                    modifier = Modifier.weight(1f) // Distribuisce meglio lo spazio
                ) {
                    Text("Elimina")
                }
            }
        }
    }
}
