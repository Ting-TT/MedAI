package com.example.medai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Upload
import androidx.compose.foundation.text.BasicTextField

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
            composable("home") { HomeScreen(navController) }
            composable("account") { AccountScreen() }
            composable("describe_symptom") { DescribeSymptomScreen() }
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
fun HomeScreen(navController: NavHostController) {
    // Two vertically placed buttons on the home screen
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = { navController.navigate("describe_symptom") },
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
fun DescribeSymptomScreen() {
    var symptomDescription by remember { mutableStateOf("") }
    var responseText by remember { mutableStateOf("Response will be shown here.") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Upload image button
        Button(
            onClick = { /* TODO: Add image upload logic */ },
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        ) {
            Icon(Icons.Default.Upload, contentDescription = "Upload")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Upload Image")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Text prompting to describe symptoms
        Text(text = "Describe your symptoms:")

        // Text input box for symptom description
        BasicTextField(
            value = symptomDescription,
            onValueChange = { symptomDescription = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .height(150.dp)
                .border(1.dp, MaterialTheme.colorScheme.onBackground)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Submit button to send symptom data
        Button(
            onClick = {
                // Here you'd add the Gemini API logic to get a response
                responseText = "Simulated API response: You might have a cold. Suggested medicine: Paracetamol."
            },
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        ) {
            Text("Submit")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Response text to display Gemini's response
        Text(
            text = responseText,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .border(1.dp, MaterialTheme.colorScheme.onBackground)
                .padding(16.dp)
        )
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
