package com.example.familyspendtracker.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.rounded.AccountBalanceWallet
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.familyspendtracker.R

// Caricamento del font personalizzato
val LobsterFont = FontFamily(
    Font(R.font.lobster_regular)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModernAppBar(title: String, onMenuClick: () -> Unit) {
    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Rounded.AccountBalanceWallet,
                    contentDescription = "Logo",
                    modifier = Modifier
                        .size(28.dp)
                        .padding(end = 8.dp),
                    tint = Color.White
                )
                Text(
                    text = title,
                    style = TextStyle(
                        fontFamily = LobsterFont,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.White,
                        letterSpacing = 1.2.sp,
                        shadow = Shadow(
                            color = Color.Black,
                            blurRadius = 3f
                        )
                    )
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = onMenuClick) {
                Icon(Icons.Filled.Menu, contentDescription = "Menu", tint = Color.White)
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color(0xFF4CAF50),
            titleContentColor = Color.White,
            navigationIconContentColor = Color.White
        )
    )
}
