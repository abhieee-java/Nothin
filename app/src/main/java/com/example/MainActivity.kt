package com.example

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ui.screens.ActiveCallScreen
import com.example.ui.screens.ContactDetailScreen
import com.example.ui.screens.FavoritesScreen
import com.example.ui.screens.RecentsScreen
import com.example.ui.theme.NothingTheme
import com.example.viewmodel.DialerViewModel

class MainActivity : ComponentActivity() {

  private var dialerViewModel: DialerViewModel? = null

  private val requestPermissionLauncher = registerForActivityResult(
      ActivityResultContracts.RequestMultiplePermissions()
  ) { _ ->
      dialerViewModel?.refreshData()
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    
    // Asynchronously launch permissions to be ready to read contacts/call logs instantly
    requestPermissions()

    setContent {
      NothingTheme {
        val vm: DialerViewModel = viewModel()
        dialerViewModel = vm

        // If launched with standard link, pre-format number
        handleDialIntent(intent, vm)

        MainAppContent(vm)
      }
    }
  }

  override fun onNewIntent(intent: Intent) {
    super.onNewIntent(intent)
    setIntent(intent)
    dialerViewModel?.let { vm ->
        handleDialIntent(intent, vm)
    }
  }

  private fun requestPermissions() {
    requestPermissionLauncher.launch(
        arrayOf(
            android.Manifest.permission.READ_CONTACTS,
            android.Manifest.permission.READ_CALL_LOG,
            android.Manifest.permission.CALL_PHONE
        )
    )
  }

  private fun handleDialIntent(intent: Intent?, vm: DialerViewModel) {
    val data = intent?.data
    if (data != null && data.scheme == "tel") {
      val number = data.schemeSpecificPart
      if (!number.isNullOrBlank()) {
        vm.clearKeypad()
        for (char in number) {
          vm.appendKeypadDigit(char.toString())
        }
        // Open the bottom keypad drawer if closed, to show the preformatted input
        val state = vm.uiState.value
        if (!state.isKeypadOpen) {
          vm.toggleKeypad()
        }
      }
    }
  }
}

@Composable
fun MainAppContent(dialerViewModel: DialerViewModel) {
  val navController = rememberNavController()

  Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
    NavHost(
        navController = navController,
        startDestination = "recents",
        modifier = Modifier.padding(innerPadding)
    ) {
      composable("recents") {
        RecentsScreen(navController = navController, viewModel = dialerViewModel)
      }
      composable("favorites") {
        FavoritesScreen(navController = navController, viewModel = dialerViewModel)
      }
      composable("contacts") {
        com.example.ui.screens.ContactsScreen(navController = navController, viewModel = dialerViewModel)
      }
      composable("detail") {
        ContactDetailScreen(navController = navController, viewModel = dialerViewModel)
      }
      composable("call") {
        ActiveCallScreen(navController = navController, viewModel = dialerViewModel)
      }
    }
  }
}
