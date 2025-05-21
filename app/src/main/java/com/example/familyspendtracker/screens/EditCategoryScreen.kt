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
import androidx.navigation.NavController
import com.example.familyspendtracker.data.Category
import com.example.familyspendtracker.viewmodel.ExpenseViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditCategoryScreen(viewModel: ExpenseViewModel, categoryId: Int, navController: NavController) {
    val categories by viewModel.categories.observeAsState(emptyList())
    val category = categories.find { it.id == categoryId }

    if (category == null) {
        Text("Categoria non trovata.")
        return
    }

    val context = LocalContext.current
    var name by remember { mutableStateOf(category.name) }
    var initialBudget by remember { mutableStateOf(category.initialBudget.toString()) }

    val calendar = remember { Calendar.getInstance().apply { timeInMillis = category.budgetStartDate } }
    var selectedDate by remember { mutableStateOf(category.budgetStartDate) }
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }
    val formattedDate = dateFormat.format(Date(selectedDate))

    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, day ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, day)
            selectedDate = calendar.timeInMillis
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Modifica Categoria", style = MaterialTheme.typography.titleMedium)

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nome categoria") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        )

        OutlinedTextField(
            value = initialBudget,
            onValueChange = { initialBudget = it },
            label = { Text("Budget iniziale") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        )

        // ✅ Campo in sola lettura: Saldo attuale
        Text(
            text = "Saldo attuale: €${"%.2f".format(category.currentBalance)}",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .clickable { datePickerDialog.show() }
        ) {
            OutlinedTextField(
                value = formattedDate,
                onValueChange = {},
                label = { Text("Data Inizio Budget") },
                readOnly = true,
                enabled = false,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Button(
            onClick = {
                val parsedBudget = initialBudget.toDoubleOrNull()
                if (parsedBudget != null) {
                    val updatedCategory = category.copy(
                        name = name,
                        initialBudget = parsedBudget,
                        budgetStartDate = selectedDate
                    )
                    viewModel.updateCategory(updatedCategory)
                    viewModel.refreshCategories() // 🔥 Forza il refresh
                    navController.navigateUp()
                }
            },
            modifier = Modifier.padding(top = 8.dp)
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
