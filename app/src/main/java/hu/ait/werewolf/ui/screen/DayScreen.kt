package hu.ait.werewolf.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayScreen(
    onWriteNewPostClick: () -> Unit = {},
    dayScreenViewModel:DayScreenViewModel = viewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val postListState =
        dayScreenViewModel.postsList().collectAsState(initial = DayScreenUIState.Init)

    Scaffold(
        floatingActionButton = {
            MainFloatingActionButton(
                onWriteNewPostClick = onWriteNewPostClick,
                snackbarHostState = snackbarHostState
            )
        }
    ) { contentPadding ->
        // Screen content
        Column(modifier = Modifier.padding(contentPadding)) {
            Text("Username: ${dayScreenViewModel.currentUser}")
            if (postListState.value == MainScreenUIState.Init) {
                Text("initializing")
            } else if (postListState.value is MainScreenUIState.Success) {
                //Text(text = "Messages number: " +
                //        "${(postListState.value as MainScreenUIState.Success).postList.size}")
                LazyColumn() {
                    items((postListState.value as MainScreenUIState.Success).postList.sortedBy { it.post.time }) {
                        MessageCard(
                            post = it.post,
                            currentUserId = dayScreenViewModel.currentUserId
                        )
                    }
                }
            }
        }
    }
}
