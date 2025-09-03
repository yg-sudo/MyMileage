@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.yg.mileage

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.yg.mileage.auth.FirebaseAuthClient
import com.yg.mileage.data.Repository
import com.yg.mileage.navigation.Screen
import com.yg.mileage.navigation.bottomNavItems
import com.yg.mileage.ui.theme.MileageCalculatorTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
class MainActivity : ComponentActivity() {
    private lateinit var carViewModel: CarViewModel
    private lateinit var firebaseAuthClient: FirebaseAuthClient
    private lateinit var googleSignInClient: GoogleSignInClient

    private var phoneVerificationId: String? = null
    var currentGoogleAccount: GoogleSignInAccount? = null

    private val signInLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            lifecycleScope.launch {
                val signInResult = firebaseAuthClient.signInWithIntent(result.data!!)
                carViewModel.onSignInResult(signInResult)
                val account = GoogleSignIn.getLastSignedInAccount(this@MainActivity)
                currentGoogleAccount = account
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firebaseAuthClient = FirebaseAuthClient(applicationContext)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        val repository = Repository.getRepository(applicationContext)
        val carViewModelFactory = CarViewModelFactory(repository)
        currentGoogleAccount = GoogleSignIn.getLastSignedInAccount(this)

        setContent {
            carViewModel = viewModel(factory = carViewModelFactory)

            MileageCalculatorTheme {
                val navController = rememberNavController()
                var currentScreenTitle by remember { mutableStateOf(Screen.TripLog.label) }
                var canNavigateBack by remember { mutableStateOf(false) }
                val coroutineScope = rememberCoroutineScope()
                val currentUser = carViewModel.currentUser.collectAsState().value

                LaunchedEffect(Unit) {
                    carViewModel.updateSignInState(firebaseAuthClient.getSignedInUser())
                }
                LaunchedEffect(navController) {
                    navController.currentBackStackEntryFlow.collect { backStackEntry ->
                        val route = backStackEntry.destination.route
                        val screen = when (route) {
                            Screen.TripDetails.route -> Screen.TripDetails
                            Screen.Profile.route -> Screen.Profile
                            Screen.TripLog.route -> Screen.TripLog
                            Screen.AddVehicle.route -> Screen.AddVehicle
                            Screen.Account.route -> Screen.Account
                            Screen.PersonalInfo.route -> Screen.PersonalInfo
                            Screen.SecuritySettings.route -> Screen.SecuritySettings
                            else -> null
                        }
                        currentScreenTitle = screen?.label ?: "App"
                        canNavigateBack = navController.previousBackStackEntry != null
                    }
                }
                LaunchedEffect(Unit) {
                    carViewModel.signInCompleted.collectLatest {
                        navController.navigate(Screen.TripLog.route) {
                            popUpTo(0)
                            launchSingleTop = true
                        }
                    }
                }

                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text(currentScreenTitle) },
                            navigationIcon = {
                                if (canNavigateBack && !bottomNavItems.any { it.route == currentScreenTitle.lowercase() }) {
                                    IconButton(onClick = { navController.navigateUp() }) {
                                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                                    }
                                }
                            },
                            actions = {
                                if (currentUser != null) {
                                    Text(
                                        currentUser.username?.take(10) ?: "",
                                        modifier = Modifier.padding(end = 8.dp),
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                                IconButton(onClick = { navController.navigate(Screen.Account.route) }) {
                                    Icon(Icons.Default.AccountCircle, contentDescription = "Account")
                                }
                            }
                        )
                    },
                    bottomBar = {
                        AppBottomNavigationBar(navController)
                    }
                ) { innerPadding ->
                    AppNavHost(
                        navController = navController,
                        modifier = Modifier.padding(innerPadding),
                        carViewModel = carViewModel,
                        currentGoogleAccount = currentGoogleAccount,
                        googleSignInClient = googleSignInClient,
                        signInLauncher = signInLauncher,
                        firebaseAuthClient = firebaseAuthClient,
                        coroutineScope = coroutineScope,
                        onEmailSignInClick = { email, password ->
                            lifecycleScope.launch {
                                val result = firebaseAuthClient.signInWithEmailPassword(email, password)
                                carViewModel.onSignInResult(result)
                                if (result.errorMessage != null) {
                                    Toast.makeText(this@MainActivity, result.errorMessage, Toast.LENGTH_LONG).show()
                                }
                            }
                        },
                        onEmailSignUpClick = { email, password ->
                            lifecycleScope.launch {
                                val result = firebaseAuthClient.createUserWithEmailPassword(email, password)
                                carViewModel.onSignInResult(result)
                                if (result.errorMessage != null) {
                                    Toast.makeText(this@MainActivity, result.errorMessage, Toast.LENGTH_LONG).show()
                                }
                            }
                        },
                        onSendOtpClick = { phoneNumber ->
                            firebaseAuthClient.verifyPhoneNumber(
                                activity = this@MainActivity,
                                phoneNumber = phoneNumber,
                                onCodeSent = { verificationId ->
                                    this@MainActivity.phoneVerificationId = verificationId
                                    Toast.makeText(this@MainActivity, "Code sent!", Toast.LENGTH_SHORT).show()
                                },
                                onVerificationFailed = { e ->
                                    Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_LONG).show()
                                }
                            )
                        },
                        onVerifyOtpClick = { otp ->
                            this@MainActivity.phoneVerificationId?.let { verificationId ->
                                lifecycleScope.launch {
                                    val result = firebaseAuthClient.signInWithPhoneCredential(verificationId, otp)
                                    carViewModel.onSignInResult(result)
                                    if (result.errorMessage != null) {
                                        Toast.makeText(this@MainActivity, result.errorMessage, Toast.LENGTH_LONG).show()
                                    }
                                }
                            } ?: Toast.makeText(this@MainActivity, "Please send code first.", Toast.LENGTH_LONG).show()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun AppBottomNavigationBar(navController: NavHostController) {
    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        bottomNavItems.forEach { screen ->
            NavigationBarItem(
                icon = { Icon(screen.icon, contentDescription = screen.label) },
                label = { Text(screen.label) },
                selected = currentDestination?.route == screen.route,
                onClick = {
                    if (currentDestination?.route != screen.route) {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    carViewModel: CarViewModel,
    currentGoogleAccount: GoogleSignInAccount?,
    googleSignInClient: GoogleSignInClient,
    signInLauncher: androidx.activity.result.ActivityResultLauncher<android.content.Intent>,
    firebaseAuthClient: FirebaseAuthClient,
    coroutineScope: CoroutineScope,
    onEmailSignInClick: (String, String) -> Unit,
    onEmailSignUpClick: (String, String) -> Unit,
    onSendOtpClick: (String) -> Unit,
    onVerifyOtpClick: (String) -> Unit
) {
    val savedVehicles by carViewModel.savedVehicles.collectAsState()
    var backupResultMsg by remember { mutableStateOf<String?>(null) }
    val isGoogleUser = carViewModel.isGoogleUser()

    NavHost(
        navController = navController,
        startDestination = Screen.TripLog.route,
        modifier = modifier
    ) {
        composable(Screen.Profile.route) {
            ProfileScreen(
                navController = navController,
                savedVehicles = savedVehicles,
                onAddVehicle = { vehicle: Vehicle -> coroutineScope.launch { carViewModel.addVehicle(vehicle) } },
                onEditVehicle = { oldName: String, newVehicle: Vehicle -> coroutineScope.launch { carViewModel.updateVehicle(newVehicle) } },
                onDeleteVehicle = { vehicleName: String -> coroutineScope.launch { carViewModel.deleteVehicle(vehicleName) } },
                canDeleteVehicle = { vehicleName: String -> runBlocking { carViewModel.canDeleteVehicle(vehicleName) } }
            )
        }
        composable(Screen.TripLog.route) {
            TripLogScreen(
                carViewModel = carViewModel,
                onNavigateToTripDetails = { navController.navigate("trip_details") }
            )
        }
        composable("trip_details") {
            MileageCalculatorScreen(
                carViewModel = carViewModel,
                googleAccount = currentGoogleAccount
            )
        }
        composable(Screen.AddVehicle.route) {
            AddVehicleScreen(
                navController = navController,
                onSaveVehicle = { vehicle: Vehicle -> coroutineScope.launch { carViewModel.addVehicle(vehicle) } }
            )
        }
        composable(
            route = "add_vehicle/{vehicleName}",
            arguments = listOf(
                navArgument("vehicleName") { type = NavType.StringType; nullable = true; defaultValue = null }
            )
        ) { backStackEntry ->
            val vehicleName = backStackEntry.arguments?.getString("vehicleName")
            val vehicleToEdit = savedVehicles.find { it.name == vehicleName }
            AddVehicleScreen(
                navController = navController,
                onSaveVehicle = { newVehicle: Vehicle ->
                    vehicleToEdit?.let { oldVehicle ->
                        coroutineScope.launch { carViewModel.updateVehicle(newVehicle) }
                    } ?: coroutineScope.launch { carViewModel.addVehicle(newVehicle) }
                },
                vehicleToEdit = vehicleToEdit
            )
        }
        composable(Screen.Account.route) {
            AccountScreen(
                currentUser = carViewModel.currentUser.collectAsState().value,
                onSignOutClick = {
                    coroutineScope.launch {
                        firebaseAuthClient.signOut()
                        carViewModel.updateSignInState(null)
                        Log.d("MainActivity", "User signed out.")
                    }
                },
                onGoogleSignInClick = { signInLauncher.launch(googleSignInClient.signInIntent) },
                onEmailSignInClick = onEmailSignInClick,
                onEmailSignUpClick = onEmailSignUpClick,
                onSendOtpClick = onSendOtpClick,
                onVerifyOtpClick = onVerifyOtpClick,
                onNavigateToPersonalInfo = { navController.navigate(Screen.PersonalInfo.route) },
                onNavigateToSecurity = { navController.navigate(Screen.SecuritySettings.route) }
            )
            // Google Drive backup section:
            if (isGoogleUser) {
                Button(
                    onClick = {
                        carViewModel.backupTripsToDrive(currentGoogleAccount) { success, msg ->
                            backupResultMsg = msg
                        }
                    },
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text("Backup trips to Google Drive")
                }
                backupResultMsg?.let {
                    Text(text = it, color = if (it.contains("success", true)) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error)
                }
            } else {
                Text(
                    text = "Sign in with Google to enable Drive backup.",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
        composable(Screen.PersonalInfo.route) { PersonalInfoScreen(carViewModel = carViewModel) }
        composable(Screen.SecuritySettings.route) { SecuritySettingsScreen(carViewModel = carViewModel) }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MileageCalculatorTheme {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("App Preview Root")
        }
    }
}


