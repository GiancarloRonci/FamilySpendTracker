package com.example.familyspendtracker

import android.app.DatePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.livedata.observeAsState
import com.example.familyspendtracker.data.Category
import com.example.familyspendtracker.viewmodel.ExpenseViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: ExpenseViewModel) {
    val context = LocalContext.current
    val categories by viewModel.categories.observeAsState(emptyList())

    var name by remember { mutableStateOf("") }
    var budget by remember { mutableStateOf("") }

    // Tipo categoria
    val typeOptions = listOf("active", "passive")
    var selectedType by remember { mutableStateOf(typeOptions[0]) }
    var expanded by remember { mutableStateOf(false) }

    // Gestione data
    val calendar = remember { Calendar.getInstance() }
    var selectedDate by remember { mutableStateOf(calendar.timeInMillis) }
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }
    val formattedDate = dateFormat.format(Date(selectedDate))

    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            calendar.set(year, month, dayOfMonth)
            selectedDate = calendar.timeInMillis
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Aggiungi nuova categoria", style = MaterialTheme.typography.titleMedium)

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nome categoria") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
        )

        OutlinedTextField(
            value = budget,
            onValueChange = { budget = it },
            label = { Text("Importo iniziale") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
        )

        // Dropdown tipo categoria
        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
            OutlinedTextField(
                value = selectedType,
                onValueChange = {},
                readOnly = true,
                label = { Text("Tipo") },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                typeOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            selectedType = option
                            expanded = false
                        }
                    )
                }
            }
        }

        // Data con DatePicker (versione funzionante)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .clickable { datePickerDialog.show() }
        ) {
            OutlinedTextField(
                value = formattedDate,
                onValueChange = {},
                label = { Text("Data budget") },
                readOnly = true,
                enabled = false, // disabilita focus e input diretto
                modifier = Modifier.fillMaxWidth()
            )
        }

        Button(
            onClick = {
                val parsedBudget = budget.toDoubleOrNull() ?: 0.0

                val newCategory = Category(
                    name = name,
                    initialBudget = parsedBudget,
                    budgetStartDate = selectedDate
                )
                viewModel.addCategory(newCategory)

                name = ""
                budget = ""
            },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Aggiungi categoria")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Categorie salvate:")
        categories.forEach { category ->
            Text("- ${category.name}")
        }
    }
}
