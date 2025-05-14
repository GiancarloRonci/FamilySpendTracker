package com.example.familyspendtracker.navigation

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.familyspendtracker.screens.AddCategoryScreen
import com.example.familyspendtracker.screens.AddExpenseScreen
import com.example.familyspendtracker.screens.AddWalletScreen
import com.example.familyspendtracker.screens.BalanceOverviewScreen
import com.example.familyspendtracker.screens.CategoryListScreen
import com.example.familyspendtracker.screens.EditCategoryScreen
import com.example.familyspendtracker.screens.EditExpenseScreen
import com.example.familyspendtracker.screens.EditWalletScreen
import com.example.familyspendtracker.screens.ExpenseListScreen
import com.example.familyspendtracker.screens.WalletListScreen
import com.example.familyspendtracker.viewmodel.ExpenseViewModel
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainNavigation(viewModel: ExpenseViewModel) {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val menuItems = listOf(
        NavigationItem("Lista spese", "expense_list"),
        NavigationItem("Nuova spesa", "add_expense"),
        NavigationItem("Aggiungi categoria", "add_category"),
        NavigationItem("Lista categorie", "list_categories"),
        NavigationItem("Aggiungi wallet", "add_wallet"),
        NavigationItem("Lista wallet", "list_wallets") ,
        NavigationItem("Overview Bilancio", "balance_overview")

    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text(
                    "Menu",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(16.dp)
                )
                menuItems.forEach { item ->
                    NavigationDrawerItem(
                        label = { Text(item.label) },
                        selected = false,
                        onClick = {
                            navController.navigate(item.route)
                            scope.launch { drawerState.close() }
                        }
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Family Spend Tracker")
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch { drawerState.open() }
                        }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    }
                )
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = "expense_list",
                modifier = Modifier.padding(innerPadding)
            ) {
                composable("expense_list") {
                    ExpenseListScreen(viewModel, navController)
                }


                composable("add_expense") {
                    AddExpenseScreen(viewModel)
                }

                composable("list_categories") {
                    CategoryListScreen(viewModel, navController) // ✅ correzione
                }


                composable("add_category") {
                    AddCategoryScreen(viewModel)
                }


                composable("edit_categories") {
                    Text("Schermata: Modifica categorie") // placeholder
                }


                composable("add_wallet") {
                    AddWalletScreen(viewModel)
                }


                composable("list_wallets") {
                    WalletListScreen(viewModel = viewModel, navController = navController)
                }


                composable("edit_wallet/{walletId}") { backStackEntry ->
                    val walletId = backStackEntry.arguments?.getString("walletId")?.toIntOrNull()
                    if (walletId != null) {
                        EditWalletScreen(viewModel, walletId)
                    }
                }

                composable("edit_expense/{expenseId}") { backStackEntry ->
                    val expenseId = backStackEntry.arguments?.getString("expenseId")?.toIntOrNull()
                    if (expenseId != null) {
                        EditExpenseScreen(viewModel = viewModel, expenseId = expenseId)
                    } else {
                        Text("Errore: ID spesa non valido")
                    }
                }

                composable("edit_category/{categoryId}") { backStackEntry ->
                    val categoryId = backStackEntry.arguments?.getString("categoryId")?.toInt()
                    if (categoryId != null) {
                        EditCategoryScreen(viewModel, categoryId, navController)
                    }
                }

                composable("balance_overview") {
                    BalanceOverviewScreen(viewModel)
                }




            }
        }
    }
}

data class NavigationItem(val label: String, val route: String)
