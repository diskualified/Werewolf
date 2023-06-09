package hu.ait.werewolf.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Logout
import androidx.compose.runtime.*
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun MainScreen(
    onLogout: () -> Unit = {},
    toNight : () -> Unit = {},
    mainScreenViewModel : MainScreenViewModel = viewModel()
) {
    val roleListState =
        mainScreenViewModel.rolesList().collectAsState(initial = MainScreenUIState.Init)
    var expanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf("Villager") }
    var lobbySize by remember { mutableStateOf(0) }
    var roleNum by remember { mutableStateOf(0) }
    var isChatUIExpanded by remember { mutableStateOf(false) }
    val collection = FirebaseFirestore.getInstance().collection("activeUsers")

    collection.get().addOnSuccessListener {
        lobbySize = it.documents.size
        roleNum = lobbySize
    }

    Scaffold(
        topBar = { MainTopBar(title = "AIT Werewolf", onLogout) },
    ) { contentPadding ->
        // Screen content
        Column(modifier = Modifier.padding(contentPadding)) {
            Column(modifier = Modifier.weight(if (isChatUIExpanded) 0.05f else 1.0f)) {
                Text("Username: ${mainScreenViewModel.currentUser}")
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    OutlinedButton(
                        onClick = { expanded = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(selectedOption)
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            onClick = {
                                selectedOption = "Villager"
                                expanded = false
                            },
                            text = { Text(text = "Villager") }
                        )
                        DropdownMenuItem(onClick = {
                            selectedOption = "Werewolf"
                            expanded = false
                        },
                            text = { Text("Werewolf") })

                        DropdownMenuItem(onClick = {
                            selectedOption = "Troublemaker"
                            expanded = false
                            }, text = { Text("Troublemaker") }
                        )
                        DropdownMenuItem(onClick = {
                            selectedOption = "Seer"
                            expanded = false
                        }, text = { Text("Seer") }
                        )
                    }
                }

                Row(Modifier.fillMaxWidth()) {
                    Button(onClick = {
                        mainScreenViewModel.uploadRole(selectedOption)
                    }, modifier=Modifier.padding(start = 5.dp)) {
                        Text(text = "Add Role")
                    }
                    Button(onClick = {
                        mainScreenViewModel.assign()
                        toNight()
                    }, modifier=Modifier.padding(start = 5.dp)) {
                        Text("Start Game")
                    }
                    Button(onClick = {
                        mainScreenViewModel.deleteRoles()
                    }, modifier=Modifier.padding(start = 5.dp)) {
                        Text("Reset Roles")
                    }
                }

                Text("Roles: ")
                Text("$roleNum more roles required")

                if (roleListState.value is MainScreenUIState.Success2) {
                    //Text(text = "Messages number: " +
                    //        "${(postListState.value as MainScreenUIState.Success).postList.size}")
                    roleNum = lobbySize - (roleListState.value as MainScreenUIState.Success2).roleList.size
                    LazyColumn() {
                        items((roleListState.value as MainScreenUIState.Success2).roleList) {
                            Text(it)
                        }
                    }
                }
            }
            // Message
            Column(modifier = Modifier.weight(if (isChatUIExpanded) 1.0f else 0.05f)) {
                ChatUI(
                    currentUserId = mainScreenViewModel.currentUserId,
                    isChatUIExpanded = isChatUIExpanded
                ) { isChatUIExpanded = !isChatUIExpanded }
            }

            }
        }
    }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTopBar(title: String,
               onLogout: () -> Unit = {},
               mainScreenViewModel : MainScreenViewModel = viewModel()) {
    TopAppBar(
        title = { Text(title) },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor =
            MaterialTheme.colorScheme.secondaryContainer
        ),
        actions = {
            IconButton(
                onClick = { }
            ) {
                Icon(Icons.Filled.Info, contentDescription = "Info")
            }
            IconButton(onClick = {
                mainScreenViewModel.logout()
                onLogout()
            }) {
                Icon(Icons.Filled.Logout, contentDescription = "Logout")
            }
        }
    )
}

