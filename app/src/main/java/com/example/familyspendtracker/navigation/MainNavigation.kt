package com.example.familyspendtracker.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Category // ✅ questa è fondamentale per risolvere l'errore

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.*
import com.example.familyspendtracker.screens.*
import com.example.familyspendtracker.viewmodel.ExpenseViewModel
import kotlinx.coroutines.launch

data class NavigationItem(val label: String, val route: String, val icon: ImageVector)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainNavigation(viewModel: ExpenseViewModel) {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val currentDestination by navController.currentBackStackEntryAsState()
    val currentRoute = currentDestination?.destination?.route

    val menuItems = listOf(
        NavigationItem("Lista spese", "expense_list", Icons.Default.List),
        NavigationItem("Nuova spesa", "add_expense", Icons.Default.Add),
        NavigationItem("Aggiungi categoria", "add_category", Icons.Default.AddCircle),
        NavigationItem("Lista categorie", "list_categories", Icons.Default.Category),
        NavigationItem("Aggiungi wallet", "add_wallet", Icons.Default.AccountBalance),
        NavigationItem("Lista wallet", "list_wallets", Icons.Default.AccountBalanceWallet),
        NavigationItem("Overview Bilancio", "balance_overview", Icons.Default.BarChart)
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text(
                    text = "Menu",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(16.dp)
                )
                Spacer(Modifier.height(8.dp))
                menuItems.forEach { item ->
                    NavigationDrawerItem(
                        label = { Text(item.label) },
                        selected = currentRoute == item.route,
                        onClick = {
                            navController.navigate(item.route) {
                                launchSingleTop = true
                                restoreState = true
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                            }
                            scope.launch { drawerState.close() }
                        },
                        icon = {
                            Icon(imageVector = item.icon, contentDescription = item.label)
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
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
                            Icon(Icons.Default.Star, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(Modifier.width(8.dp))
                            Text("Family Spend Tracker")
                            Spacer(Modifier.width(8.dp))
                            Icon(Icons.Default.Star, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Apri menu")
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
                composable("expense_list") { ExpenseListScreen(viewModel, navController) }
                composable("add_expense") { AddExpenseScreen(viewModel) }
                composable("add_category") { AddCategoryScreen(viewModel) }
                composable("list_categories") { CategoryListScreen(viewModel, navController) }
                composable("add_wallet") { AddWalletScreen(viewModel) }
                composable("list_wallets") { WalletListScreen(viewModel, navController) }
                composable("balance_overview") { BalanceOverviewScreen(viewModel) }
                composable("edit_expense/{expenseId}") { backStackEntry ->
                    backStackEntry.arguments?.getString("expenseId")?.toIntOrNull()?.let {
                        EditExpenseScreen(viewModel, it)
                    }
                }
                composable("edit_wallet/{walletId}") { backStackEntry ->
                    backStackEntry.arguments?.getString("walletId")?.toIntOrNull()?.let {
                        EditWalletScreen(viewModel, it)
                    }
                }
                composable("edit_category/{categoryId}") { backStackEntry ->
                    backStackEntry.arguments?.getString("categoryId")?.toIntOrNull()?.let {
                        EditCategoryScreen(viewModel, it, navController)
                    }
                }
            }
        }
    }
}
