package com.example.familyspendtracker.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.familyspendtracker.data.Wallet
import com.example.familyspendtracker.viewmodel.ExpenseViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun EditWalletScreen(viewModel: ExpenseViewModel, walletId: Int) {
    val wallets by viewModel.wallets.observeAsState(emptyList())
    val wallet = wallets.find { it.id == walletId }

    var name by remember { mutableStateOf("") }
    var balance by remember { mutableStateOf("") }
    var lastSavedBalance by remember { mutableStateOf<Double?>(null) }

    // inizializza solo una volta con i dati del wallet
    LaunchedEffect(wallet) {
        wallet?.let {
            name = it.name
            balance = it.initialBalance.toString()
            lastSavedBalance = it.initialBalance
        }
    }

    if (wallet == null) {
        Text("Wallet non trovato.")
        return
    }

    val formattedDate = remember(wallet.startTimestamp) {
        val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
        formatter.format(Date(wallet.startTimestamp))
    }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {

        Text("Modifica Wallet", style = MaterialTheme.typography.titleMedium)

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nome wallet") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        )

        OutlinedTextField(
            value = balance,
            onValueChange = { balance = it },
            label = { Text("Saldo iniziale") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        )

        // ✅ Campo in sola lettura: Saldo attuale
        Text(
            text = "Saldo attuale: €${"%.2f".format(wallet.currentBalance)}",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
        )

        Text(
            "Inizio validità: $formattedDate",
            modifier = Modifier.padding(top = 8.dp)
        )

        Button(
            onClick = {
                val parsedBalance = balance.toDoubleOrNull() ?: wallet.initialBalance

                val updatedWallet = wallet.copy(
                    name = name,
                    initialBalance = parsedBalance,
                    startTimestamp = if (parsedBalance != lastSavedBalance)
                        System.currentTimeMillis()
                    else
                        wallet.startTimestamp
                )

                viewModel.updateWallet(updatedWallet)
            },
            modifier = Modifier.padding(top = 12.dp)
        ) {
            Text("Salva modifiche")
        }

        Text(
            text = "Modificando la data inizio budget, per il calcolo del balance residuo, verranno prese in considerazione solo le spese successive a tale data.",
            color = Color(0xFF2E7D32),
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(top = 12.dp)
        )
    }
}
