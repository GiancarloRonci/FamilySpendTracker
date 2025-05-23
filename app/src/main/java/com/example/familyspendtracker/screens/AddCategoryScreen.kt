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
import com.example.familyspendtracker.data.Category
import com.example.familyspendtracker.viewmodel.ExpenseViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCategoryScreen(viewModel: ExpenseViewModel) {
    val context = LocalContext.current

    var name by remember { mutableStateOf("") }
    var budget by remember { mutableStateOf("") }
    var budgetError by remember { mutableStateOf(false) }

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

    val categories by viewModel.categories.observeAsState(emptyList())

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Text("Aggiungi nuova categoria", style = MaterialTheme.typography.titleMedium)

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nome categoria") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        )

        OutlinedTextField(
            value = budget,
            onValueChange = { newValue ->
                budget = newValue
                budgetError = !newValue.matches(Regex("^\\d{0,7}(\\.\\d{0,2})?$"))
            },
            label = { Text("Budget iniziale") },
            isError = budgetError,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            singleLine = true
        )

        if (budgetError) {
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
                label = { Text("Data inizio budget") },
                readOnly = true,
                enabled = false,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Button(
            onClick = {
                val parsedBudget = budget.toDoubleOrNull()
                budgetError = parsedBudget == null
                if (!budgetError) {
                    val category = Category(
                        name = name,
                        initialBudget = parsedBudget!!,
                        budgetStartDate = selectedDate
                    )
                    viewModel.addCategory(category)
                    name = ""
                    budget = ""
                }
            },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Salva categoria")
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Categorie salvate:")

        categories.forEach {
            Text("- ${it.name}: ${it.initialBudget}")
        }
    }
}
