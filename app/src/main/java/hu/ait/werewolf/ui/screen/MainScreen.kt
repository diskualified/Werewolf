package hu.ait.werewolf.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import hu.ait.werewolf.data.Post
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.icons.filled.Logout
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.sp
import hu.ait.werewolf.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onWriteNewPostClick: () -> Unit = {},
    onLogout: () -> Unit = {},
    toNight : () -> Unit = {},
    mainScreenViewModel : MainScreenViewModel = viewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val postListState =
        mainScreenViewModel.postsList().collectAsState(initial = MainScreenUIState.Init)
    val roleListState =
        mainScreenViewModel.rolesList().collectAsState(initial = MainScreenUIState.Init)
    var expanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf("Villager") }


    Scaffold(
        topBar = { MainTopBar(title = "AIT Werewolf", onLogout) },
        floatingActionButton = {
            MainFloatingActionButton(
                onWriteNewPostClick = onWriteNewPostClick,
                snackbarHostState = snackbarHostState
            )
        }
    ) { contentPadding ->
        // Screen content
        Column(modifier = Modifier.padding(contentPadding)) {
            Text("Username: ${mainScreenViewModel.currentUser}")
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp)
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
                }
            }

                Button(onClick = {
                    mainScreenViewModel.uploadRole(selectedOption)
                }) {
                    Text(text = "Add Role")
                }
                Button(onClick = {
                    mainScreenViewModel.assign()
                    toNight()
                }) {
                    Text("Assign Roles")
                }
//            Button(onClick = {
//                mainScreenViewModel.logout()
//            }) {
//                Text(text="Delete user")
//            }

                Text("Roles: ")

                if (roleListState.value is MainScreenUIState.Success2) {
                    //Text(text = "Messages number: " +
                    //        "${(postListState.value as MainScreenUIState.Success).postList.size}")

                    LazyColumn() {
                        items((roleListState.value as MainScreenUIState.Success2).roleList) {
                            Text(it)
                        }
                    }
                }

                if (postListState.value == MainScreenUIState.Init) {
                    Text("initializing")
                } else if (postListState.value is MainScreenUIState.Success) {
                    //Text(text = "Messages number: " +
                    //        "${(postListState.value as MainScreenUIState.Success).postList.size}")

                    LazyColumn() {
                        items((postListState.value as MainScreenUIState.Success).postList.sortedBy { it.post.time }) {
                            MessageCard(
                                post = it.post,
                                currentUserId = mainScreenViewModel.currentUserId
                            )
                        }
                    }
                }
            }
        }
    }



@Composable
fun MainFloatingActionButton(
    onWriteNewPostClick: () -> Unit = {},
    snackbarHostState: SnackbarHostState
) {
    val coroutineScope = rememberCoroutineScope()

    FloatingActionButton(
        onClick = {
            onWriteNewPostClick()
        },
        containerColor = MaterialTheme.colorScheme.secondary,
        shape = RoundedCornerShape(16.dp),
    ) {
        Icon(
            imageVector = Icons.Rounded.Add,
            contentDescription = "Add",
            tint = Color.White,
        )
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageCard(
    post: Post,
    currentUserId: String = ""
) {
    val isMine by remember {
        mutableStateOf(currentUserId == post.uid)
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalAlignment = when { // 2
            isMine -> Alignment.End
            else -> Alignment.Start
        },
    ) {
        Card(
            modifier = Modifier.widthIn(max = 340.dp),
            shape = cardShapeFor(post.body, isMine),
            colors = when {
                isMine -> CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                )
                else -> CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                )
            },
        ) {
            Text(
                modifier = Modifier.padding(8.dp),
                text = post.body
            )
        }
        Text(
            // 4
            text = post.author,
            fontSize = 12.sp,
        )
    }
}


@Composable
fun cardShapeFor(message: String, isMine: Boolean): Shape {
    val roundedCorners = RoundedCornerShape(16.dp)
    return when {
        isMine -> roundedCorners.copy(bottomEnd = CornerSize(0))
        else -> roundedCorners.copy(bottomStart = CornerSize(0))
    }
}
