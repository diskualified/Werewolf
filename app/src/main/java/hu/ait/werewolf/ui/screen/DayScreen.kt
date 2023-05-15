package hu.ait.werewolf.ui.screen

import android.util.Log
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
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayScreen(
    onWriteNewPostClick: () -> Unit = {},
    dayScreenViewModel:DayScreenViewModel = viewModel()
) {
    val voteState = dayScreenViewModel.findMaxVotes().collectAsState(initial = DayScreenUIState.Init)
    val snackbarHostState = remember { SnackbarHostState() }
    val playerListState =
        dayScreenViewModel.playerList().collectAsState(initial = DayScreenUIState.Init)
    var selectedOption = remember { mutableStateOf("") }
    var currCount by remember { mutableStateOf(0) }
    val collection = FirebaseFirestore.getInstance().collection("players")
    var playerCount by remember {
        mutableStateOf(0)
    }
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
                playerCount = (playerListState.value as DayScreenUIState.Success).playerNames.size
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
                        currCount = 1
                        collection.get()
                            .addOnSuccessListener { querySnapshot ->
                                for (document in querySnapshot) {
                                    val documentData = document.data
                                    currCount += (documentData["votes"] as String).toInt()
                                }
                            }
                        dayScreenViewModel.addVote(selectedOption.value)
                    }
                ) {
                    Text("Vote")
                }
            }
//            while(dayScreenViewModel.countVotes() < (playerListState.value as DayScreenUIState.Success).playerNames.size){
//
//            }
            Text("Vote Count: $currCount")
//            if (voteState.value is DayScreenUIState.Success2) {
//                val voteCount = (voteState.value as DayScreenUIState.Success2).count
//                val user =
//                Text(dayScreenViewModel.getResult(user))
//            }
            Button(onClick = {
                if (voteState.value is DayScreenUIState.Success2) {
                    val res = (voteState.value as DayScreenUIState.Success2).res
                    Log.d("res", res)
                }
            }) {
                Text("Reveal Winner when Vote Count is ${playerCount}")
            }
        }
    }
}
