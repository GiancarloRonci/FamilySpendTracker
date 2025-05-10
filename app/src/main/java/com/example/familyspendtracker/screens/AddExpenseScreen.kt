package com.example.familyspendtracker.screens

import android.app.DatePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.familyspendtracker.data.Expense
import com.example.familyspendtracker.viewmodel.ExpenseViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseScreen(viewModel: ExpenseViewModel) {
    val context = LocalContext.current

    var amount by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedPassiveCategoryId by remember { mutableStateOf<Int?>(null) }
    var selectedWalletId by remember { mutableStateOf<Int?>(null) }
    var isPlanned by remember { mutableStateOf(false) }

    val categories by viewModel.categories.observeAsState(emptyList())
    val wallets by viewModel.wallets.observeAsState(emptyList())

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

    var categoryExpanded by remember { mutableStateOf(false) }
    val selectedCategoryName = categories.find { it.id == selectedPassiveCategoryId }?.name ?: ""

    var walletExpanded by remember { mutableStateOf(false) }
    val selectedWalletName = wallets.find { it.id == selectedWalletId }?.name ?: ""

    Column(modifier = Modifier
        .padding(16.dp)
        .fillMaxWidth()) {

        Text("Aggiungi nuova spesa", style = MaterialTheme.typography.titleMedium)

        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it },
            label = { Text("Importo spesa") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
        )

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Descrizione") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
        )

        // Dropdown categoria passiva
        ExposedDropdownMenuBox(
            expanded = categoryExpanded,
            onExpandedChange = { categoryExpanded = !categoryExpanded }
        ) {
            OutlinedTextField(
                value = selectedCategoryName,
                onValueChange = {},
                readOnly = true,
                label = { Text("Categoria di spesa") },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            )
            ExposedDropdownMenu(
                expanded = categoryExpanded,
                onDismissRequest = { categoryExpanded = false }
            ) {
                categories.forEach { category ->
                    DropdownMenuItem(
                        text = { Text(category.name) },
                        onClick = {
                            selectedPassiveCategoryId = category.id
                            categoryExpanded = false
                        }
                    )
                }
            }
        }

        // Dropdown wallet
        ExposedDropdownMenuBox(
            expanded = walletExpanded,
            onExpandedChange = { walletExpanded = !walletExpanded }
        ) {
            OutlinedTextField(
                value = selectedWalletName,
                onValueChange = {},
                readOnly = true,
                label = { Text("Wallet") },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            )
            ExposedDropdownMenu(
                expanded = walletExpanded,
                onDismissRequest = { walletExpanded = false }
            ) {
                wallets.forEach { wallet ->
                    DropdownMenuItem(
                        text = { Text(wallet.name) },
                        onClick = {
                            selectedWalletId = wallet.id
                            walletExpanded = false
                        }
                    )
                }
            }
        }

        // Data spesa
        OutlinedTextField(
            value = formattedDate,
            onValueChange = {},
            label = { Text("Data spesa") },
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .clickable { datePickerDialog.show() }
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = isPlanned, onCheckedChange = { isPlanned = it })
            Text("Spesa pianificata")
        }

        Button(
            onClick = {
                amount.toDoubleOrNull()?.let { parsedAmount ->
                    if (selectedPassiveCategoryId != null && selectedWalletId != null) {
                        val expense = Expense(
                            timestamp = selectedDate,
                            amount = parsedAmount,
                            walletId = selectedWalletId!!,
                            passiveCategoryId = selectedPassiveCategoryId!!,
                            planned = isPlanned,
                            description = description
                        )
                        viewModel.addExpense(expense)
                        amount = ""
                        description = ""
                    }
                }
            },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Salva spesa")
        }
    }
}
