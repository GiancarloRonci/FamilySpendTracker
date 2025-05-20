package com.example.familyspendtracker.screens

import android.app.DatePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.familyspendtracker.data.Wallet
import com.example.familyspendtracker.viewmodel.ExpenseViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddWalletScreen(viewModel: ExpenseViewModel) {
    val context = LocalContext.current

    var name by remember { mutableStateOf("") }
    var balance by remember { mutableStateOf("") }
    var balanceError by remember { mutableStateOf(false) }

    val calendar = remember { Calendar.getInstance() }
    var selectedDate by remember { mutableStateOf(calendar.timeInMillis) }
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }
    val formattedDate = dateFormat.format(Date(selectedDate))

    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, day ->
            calendar.set(year, month, day)
            selectedDate = calendar.timeInMillis
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    val wallets by viewModel.wallets.observeAsState(emptyList())

    Column(modifier = Modifier
        .padding(16.dp)
        .fillMaxWidth()) {

        Text("Aggiungi nuovo wallet", style = MaterialTheme.typography.titleMedium)

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
            onValueChange = { newValue ->
                balance = newValue
                balanceError = !newValue.matches(Regex("^\\d{0,7}(\\.\\d{0,2})?$"))
            },
            label = { Text("Saldo iniziale") },
            isError = balanceError,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            singleLine = true
        )

        if (balanceError) {
            Text(
                text = "Inserire un numero valido (es. 123.45)",
                color = Color.Red,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .clickable { datePickerDialog.show() }
        ) {
            OutlinedTextField(
                value = formattedDate,
                onValueChange = {},
                label = { Text("Data di riferimento") },
                readOnly = true,
                enabled = false,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Button(
            onClick = {
                val parsedBalance = balance.toDoubleOrNull()
                balanceError = parsedBalance == null
                if (!balanceError) {
                    val wallet = Wallet(
                        name = name,
                        initialBalance = parsedBalance!!,
                        currentBalance = parsedBalance
                    )
                    viewModel.addWallet(wallet)
                    name = ""
                    balance = ""
                }
            },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Salva wallet")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Wallet salvati:")
        wallets.forEach {
            Text("- ${it.name}: ${it.initialBalance}")
        }
    }
}
