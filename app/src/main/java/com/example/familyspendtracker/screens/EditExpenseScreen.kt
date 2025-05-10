package com.example.familyspendtracker.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.familyspendtracker.data.Expense
import com.example.familyspendtracker.viewmodel.ExpenseViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditExpenseScreen(viewModel: ExpenseViewModel, expenseId: Int) {
    val expenses by viewModel.expenses.observeAsState(emptyList())
    val wallets by viewModel.wallets.observeAsState(emptyList())
    val categories by viewModel.categories.observeAsState(emptyList())
    val expense = expenses.find { it.id == expenseId }

    if (expense == null) {
        Text("Spesa non trovata.")
        return
    }

    val context = LocalContext.current

    var amount by remember { mutableStateOf(expense.amount.toString()) }
    var description by remember { mutableStateOf(expense.description ?: "") }
    var selectedWalletId by remember { mutableStateOf(expense.walletId) }
    var selectedCategoryId by remember { mutableStateOf(expense.passiveCategoryId) }
    var isPlanned by remember { mutableStateOf(expense.planned) }

    val calendar = remember { Calendar.getInstance().apply { timeInMillis = expense.timestamp } }
    var selectedDate by remember { mutableStateOf(expense.timestamp) }
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) }
    val formattedDate = dateFormat.format(Date(selectedDate))

    // TimePicker
    val timePickerDialog = TimePickerDialog(
        context,
        { _, hour, minute ->
            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, minute)
            selectedDate = calendar.timeInMillis
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        true
    )

    // DatePicker
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, day ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, day)
            timePickerDialog.show()
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Modifica Spesa", style = MaterialTheme.typography.titleMedium)

        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it },
            label = { Text("Importo") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        )

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Descrizione") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        )

        // Wallet dropdown
        var walletExpanded by remember { mutableStateOf(false) }
        val selectedWalletName = wallets.find { it.id == selectedWalletId }?.name ?: "Seleziona wallet"
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

        // Categoria dropdown
        var categoryExpanded by remember { mutableStateOf(false) }
        val selectedCategoryName = categories.find { it.id == selectedCategoryId }?.name ?: "Seleziona categoria"
        ExposedDropdownMenuBox(
            expanded = categoryExpanded,
            onExpandedChange = { categoryExpanded = !categoryExpanded }
        ) {
            OutlinedTextField(
                value = selectedCategoryName,
                onValueChange = {},
                readOnly = true,
                label = { Text("Categoria") },
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
                            selectedCategoryId = category.id
                            categoryExpanded = false
                        }
                    )
                }
            }
        }

        // Data e ora: clic su Box che contiene il campo
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .clickable { datePickerDialog.show() }
        ) {
            OutlinedTextField(
                value = formattedDate,
                onValueChange = {},
                readOnly = true,
                enabled = false,
                label = { Text("Data e ora spesa") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            Checkbox(checked = isPlanned, onCheckedChange = { isPlanned = it })
            Text("Spesa pianificata")
        }

        Button(
            onClick = {
                val updated = expense.copy(
                    amount = amount.toDoubleOrNull() ?: expense.amount,
                    description = description,
                    walletId = selectedWalletId,
                    passiveCategoryId = selectedCategoryId,
                    timestamp = selectedDate,
                    planned = isPlanned
                )
                viewModel.updateExpense(updated)
            },
            modifier = Modifier.padding(top = 12.dp)
        ) {
            Text("Salva modifiche")
        }
    }
}
