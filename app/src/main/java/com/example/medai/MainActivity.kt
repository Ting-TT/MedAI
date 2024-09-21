package com.example.medai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.foundation.verticalScroll
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.launch
import coil.compose.AsyncImage
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.height

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.ui.text.font.FontWeight



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
                    //val loginViewModel: LoginViewModel = viewModel()
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
            composable("account") { AccountScreen(navController = navController) }
            composable("describe_symptom") { DescribeSymptomScreen() }
            composable("user_inquiries") { UserInquiries(profile = null) {navController.popBackStack()} }
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
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val coroutineScope = rememberCoroutineScope()
    val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash-latest", // Replace with your actual model
        apiKey = "XXX" // TODO: how to store this locally.
    )

    // Image picker launcher
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Upload image button
        Button(
            onClick = {
                // Open image picker to select an image
                launcher.launch("image/*")
            },
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        ) {
            Icon(Icons.Default.Upload, contentDescription = "Upload")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Upload Image")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Display selected image
        selectedImageUri?.let { uri ->
            AsyncImage(
                model = uri,
                contentDescription = "Selected Image",
                modifier = Modifier
                    .height(200.dp)
                    .fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Text prompting to describe symptoms
        Text(text = "Describe your symptoms:")

        // Text input box for symptom description
        TextField(
            value = symptomDescription,
            onValueChange = { symptomDescription = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            label = { Text("Symptom description") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Submit button to send symptom data
        Button(
            onClick = {
                coroutineScope.launch {
                    val input = "Based on the following symptom(s): $symptomDescription, please provide a detailed explanation of the potential condition or disease, along with suggestions for possible medications with the suggested dosage and recommended activities or lifestyle changes to alleviate the symptoms."
                    responseText = generativeModel.generateContent(input).text.orEmpty()
                }
            },
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        ) {
            Text("Submit")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Response text to display Gemini's response
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .border(1.dp, MaterialTheme.colorScheme.onBackground)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(text = responseText)
        }
    }
}

@Composable
fun AccountScreen(loginViewModel: LoginViewModel = viewModel(), navController: NavHostController) {
    val isLoggedIn by loginViewModel.isLoggedIn.collectAsState()
    val userProfile by loginViewModel.userProfile.collectAsState()

    if (isLoggedIn) {
        ProfileScreen(userProfile, loginViewModel::logout, navController)
    } else {
        LoginScreen(onLogin = { username, password -> loginViewModel.login(username, password) })
    }
}

@Composable
fun ProfileScreen(userProfile: UserProfile?, onLogout: () -> Unit, navController: NavHostController) {
    Box(
        modifier = Modifier
            .fillMaxSize() // Fill the maximum available size
            .padding(16.dp), // Add padding around the edges
        contentAlignment = Alignment.TopStart // Align content to the top start (upper left)
    ) {
        userProfile?.let {
            Column(
                horizontalAlignment = Alignment.Start, // Align items to the start
                verticalArrangement = Arrangement.Top // Align items to the top
            ) {
                Text(
                    text = "Welcome, ${it.username}",
                    style = TextStyle(fontSize = 24.sp) // Use a specific font size
                )
                Spacer(modifier = Modifier.height(8.dp)) // Add space between messages
                Text(
                    text = "Email: ${it.email}",
                    style = TextStyle(fontSize = 16.sp) // Use a specific font size
                )
                Spacer(modifier = Modifier.height(24.dp)) // Space before buttons

                // Create clickable list item
                ClickableListItem(label = "Profile") {  navController.navigate("user_inquiries")}
                ClickableListItem(label = "History") { /* TODO: Navigate to History */ }
                ClickableListItem(label = "Log Out") {
                    onLogout()
                }
            }
        } ?: run {
            Text(text = "No user profile available", style = TextStyle(fontSize = 16.sp))
        }
    }
}

@Composable
fun ClickableListItem(label: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth() // Make card fill the width
            .clickable(onClick = onClick) // Make the card clickable
            .padding(vertical = 30.dp, horizontal = 16.dp) // Add padding inside the card
        //elevation = 4.dp // Add elevation to give it depth
    ) {
        Text(text = label, style = TextStyle(fontSize = 30.sp)) // Set the text style
    }
}


@Composable
fun LoginScreen(onLogin: (String, String) -> Unit) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Use a Box or Column with a vertical arrangement
    Box(
        modifier = Modifier
            .fillMaxSize() // Fill the maximum available size
            .padding(16.dp), // Add padding around the edges
        contentAlignment = Alignment.Center // Center the content
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally, // Center the items horizontally
            verticalArrangement = Arrangement.Center // Center the items vertically
        ) {
            TextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                modifier = Modifier.fillMaxWidth() // Make the TextField fill the width
            )
            Spacer(modifier = Modifier.height(8.dp)) // Add space between fields
            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth() // Make the TextField fill the width
            )
            Spacer(modifier = Modifier.height(16.dp)) // Add space before the button
            Button(
                onClick = { onLogin(username, password) },
                modifier = Modifier.fillMaxWidth() // Make the button fill the width
            ) {
                Text("Login")
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MedAITheme {
        MedAINavigation()
    }
}

@Composable
fun UserInquiries(profile: Profile?, onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.TopStart
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Back Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                }
                Text("User Inquiries", style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold))
            }
            Spacer(modifier = Modifier.height(16.dp)) // Space below the toolbar

            if (profile != null) {
                // Existing profile display code
                Column(
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Top
                ) {
                    Text(text = "Name: ${profile.username}", style = TextStyle(fontSize = 24.sp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Gender: ${profile.gender}", style = TextStyle(fontSize = 16.sp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Height: ${profile.height}", style = TextStyle(fontSize = 16.sp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Weight: ${profile.weight}", style = TextStyle(fontSize = 16.sp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Date of Birth: ${profile.dateOfBirth}", style = TextStyle(fontSize = 16.sp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Address: ${profile.address}", style = TextStyle(fontSize = 16.sp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Phone Number: ${profile.phoneNumber}", style = TextStyle(fontSize = 16.sp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Known Medical Conditions: ${profile.medicalConditions}", style = TextStyle(fontSize = 16.sp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Allergies: ${profile.allergies}", style = TextStyle(fontSize = 16.sp))
                    Spacer(modifier = Modifier.height(24.dp))

                    // Clickable items for navigation can go here
                    ClickableListItem(label = "Profile") { /* TODO: Navigate to Profile */ }
                    ClickableListItem(label = "History") { /* TODO: Navigate to History */ }
                    ClickableListItem(label = "Log Out") { /* TODO: Log out functionality */ }
                }
            } else {
                // Form for user input
                Column(
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Top
                ) {
                    var username by remember { mutableStateOf("") }
                    var gender by remember { mutableStateOf("") }
                    var height by remember { mutableStateOf("") }
                    var weight by remember { mutableStateOf("") }
                    var dateOfBirth by remember { mutableStateOf("") }
                    var address by remember { mutableStateOf("") }
                    var phoneNumber by remember { mutableStateOf("") }
                    var medicalConditions by remember { mutableStateOf("") }
                    var allergies by remember { mutableStateOf("") }

                    TextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    TextField(
                        value = gender,
                        onValueChange = { gender = it },
                        label = { Text("Gender") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    TextField(
                        value = height,
                        onValueChange = { height = it },
                        label = { Text("Height") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    TextField(
                        value = weight,
                        onValueChange = { weight = it },
                        label = { Text("Weight") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    TextField(
                        value = dateOfBirth,
                        onValueChange = { dateOfBirth = it },
                        label = { Text("Date of Birth") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    TextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text("Address") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    TextField(
                        value = phoneNumber,
                        onValueChange = { phoneNumber = it },
                        label = { Text("Phone Number") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    TextField(
                        value = medicalConditions,
                        onValueChange = { medicalConditions = it },
                        label = { Text("Known Medical Conditions") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    TextField(
                        value = allergies,
                        onValueChange = { allergies = it },
                        label = { Text("Allergies") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            // Handle form submission, e.g., save the profile
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Save Profile")
                    }
                }
            }
        }
    }
}


data class Profile(
    val username: String,
    val gender: String,
    val height: String,
    val weight: String,
    val dateOfBirth: String,
    val address: String,
    val phoneNumber: String,
    val medicalConditions: String,
    val allergies: String
)

