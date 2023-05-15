package hu.ait.werewolf.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayScreen(
    onWriteNewPostClick: () -> Unit = {},
    dayScreenViewModel:DayScreenViewModel = viewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val playerListState =
        dayScreenViewModel.playerList().collectAsState(initial = DayScreenUIState.Init)
    val voteState = dayScreenViewModel.findMaxVotes().collectAsState(initial = DayScreenUIState.Init)
    var selectedOption = remember { mutableStateOf("") }

//    val isSelectedItem: (String) -> Boolean = { selectedValue.value == it }
//    val onChangeState: (String) -> Unit = { selectedValue.value = it }

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
            if (playerListState.value == DayScreenUIState.Init) {
                Text("initializing")
            } else if (playerListState.value is DayScreenUIState.Success) {
                LazyColumn() {
                    items((playerListState.value as DayScreenUIState.Success).playerNames) {
//                        var selectedOption by remember {
//                            mutableStateOf("")
//                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = selectedOption.value == it,
                                onClick = { selectedOption.value = it },
                            )
                            Text(text = it)
                        }
                    }
                }
                Button(
                    onClick = {
                        dayScreenViewModel.addVote(selectedOption.value)
                    }
                ) {
                    Text("Vote")
                }
            }
            if (voteState.value is DayScreenUIState.Success2) {
                val user = (voteState.value as DayScreenUIState.Success2).maxUser
                Text(dayScreenViewModel.getResult(user))
            }
        }

    }
}
