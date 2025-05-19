package com.example.familyspendtracker.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.familyspendtracker.viewmodel.ExpenseViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs

@Composable
fun BalanceOverviewScreen(viewModel: ExpenseViewModel) {
    val wallets by viewModel.wallets.observeAsState(emptyList())
    val categories by viewModel.categories.observeAsState(emptyList())
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val totalWalletBalance = wallets.sumOf { it.currentBalance }
    val totalCategoryBalance = categories.sumOf { it.currentBalance }
    val balanceComplessivo = totalWalletBalance - totalCategoryBalance

    var showMessage by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Balance Overview", style = MaterialTheme.typography.titleLarge)
        }

        // 🔢 Balance Complessivo formattato
        item {
            val formattedBalance = "%.2f".format(abs(balanceComplessivo))
            val sign = if (balanceComplessivo >= 0) "+" else "-"
            val balanceText = "$sign$formattedBalance€"

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
                        text = "Saldo:",
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
        }

        // 🔘 Pulsante copia bilancio
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = {
                        val formattedText = buildString {
                            append("💰 Balance Overview\n\n")
                            append("🏦 Wallets:\n")
                            wallets.forEach {
                                append("- ${it.name}: €${"%.2f".format(it.currentBalance)}\n")
                            }
                            append("\n📂 Categorie:\n")
                            categories.forEach {
                                append("- ${it.name}: €${"%.2f".format(it.currentBalance)}\n")
                            }
                            append("\n💡 Balance Complessivo: €${"%.2f".format(balanceComplessivo)}")
                        }
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Balance Overview", formattedText)
                        clipboard.setPrimaryClip(clip)

                        showMessage = true
                        coroutineScope.launch {
                            delay(2000)
                            showMessage = false
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Copia Bilancio Testuale")
                }

                if (showMessage) {
                    Text(
                        text = "✅ Testo copiato negli appunti!",
                        color = Color.Green,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }

        // 📄 Lista Wallet
        item {
            Text("Lista Wallet", style = MaterialTheme.typography.titleMedium)
        }
        items(wallets) { wallet ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Nome: ${wallet.name}")
                    Text("Saldo Attuale: €${"%.2f".format(wallet.currentBalance)}")
                }
            }
        }

        // 📄 Lista Categorie
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text("Lista Categorie", style = MaterialTheme.typography.titleMedium)
        }
        items(categories) { category ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Nome: ${category.name}")
                    Text("Saldo Attuale: €${"%.2f".format(category.currentBalance)}")
                }
            }
        }
    }
}
