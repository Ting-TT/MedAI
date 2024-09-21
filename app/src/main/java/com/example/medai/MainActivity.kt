package com.example.medai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.example.medai.ui.theme.MedAITheme
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MedAITheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MedAINavigation()
                }
            }
        }
    }
}

@Composable
fun MedAINavigation() {
    // Create a NavController to handle navigation
    val navController = rememberNavController()

    // Scaffold is the basic layout structure for your screen
    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { padding ->
        // NavHost to manage navigation
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(padding)
        ) {
            composable("home") { HomeScreen() }
            composable("account") { AccountScreen() }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    BottomNavigation {
        BottomNavigationItem(
            label = { Text("Home") },
            selected = navController.currentDestination?.route == "home",
            onClick = {
                navController.navigate("home")
            },
            icon = {
                Icon(Icons.Default.Home, contentDescription = "Home")
            }
        )
        BottomNavigationItem(
            label = { Text("Account") },
            selected = navController.currentDestination?.route == "account",
            onClick = {
                navController.navigate("account")
            },
            icon = {
                Icon(Icons.Default.AccountCircle, contentDescription = "Account")
            }
        )
    }
}

@Composable
fun HomeScreen() {
    // Two vertically placed buttons on the home screen
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = { /* TODO: Add action for symptom description */ },
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        ) {
            Text("Describe your symptom")
        }
        Button(
            onClick = { /* TODO: Add action for medicine details */ },
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        ) {
            Text("Get Medicine Details")
        }
    }
}

@Composable
fun AccountScreen() {
    // Account screen content (you can customize it later)
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Account Screen")
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MedAITheme {
        MedAINavigation()
    }
}
